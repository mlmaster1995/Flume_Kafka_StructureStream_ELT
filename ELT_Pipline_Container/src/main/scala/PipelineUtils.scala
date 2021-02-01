import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.StringType
import java.util.Properties


/*
- PipleineUtils Class
- this class contains all the "user-defined" functions
- user could define any sources and any functions related to ELT
- Pipeline Class takes the user-defined functions to process data and writes to certain destination
*/
object PipelineUtils extends Serializable {

  /*
  - return a SparkSession class
  */
  val getSparkSession = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    SparkSession
      .builder()
      .appName("Stream Data Pipeline")
      .config("spark.sql.shuffle.partitions", 100)
      .config("num-executors", 100)
      .master("yarn")
      .enableHiveSupport()
      .getOrCreate()
  }

  /*
  - kafka servers' info
  */
  val brokers = Map[Int, String](
    1 -> "cxln1.c.thelab-240901.internal:6667",
    2 -> "cxln2.c.thelab-240901.internal:6667",
    3 -> "cxln3.c.thelab-240901.internal:6667",
    4 -> "cxln4.c.thelab-240901.internal:6667"
  )

  /*
  - extract function define
  - user could specify any source and the function will return a spark dataframe
  */
  val extracFunc:(SparkSession=>sql.DataFrame) = (session: SparkSession) => session.readStream.format("kafka").option("kafka.bootstrap.servers", s"${brokers(1)}").option("subscribe", "exec").load()

  /*
  - transform function define
  - user could specify any transformation and the function will return a spark dataframe
  */
  val transformFunc: (sql.DataFrame, SparkSession)=>sql.DataFrame = (source: sql.DataFrame, session: SparkSession) =>{
    import session.implicits._

    val filterRow = udf {x:String=>x.split("\\W").filter(y=>y.length>0) }

    //    source.select('timestamp.cast(StringType), 'topic.cast(StringType))
    source.withWatermark("timestamp", "1 seconds").withColumn("raw_value", 'value.cast(StringType))
      .where( !'raw_value.contains("memory") and !'raw_value.contains("buff")).withColumn("value", filterRow('raw_value))
      .select('topic,'timestamp alias "time",$"value"(0) alias "r", $"value"(1) alias "b", $"value"(2) alias "swpd", $"value"(3) alias "free", $"value"(4) alias "buff",
        $"value"(5) alias "cache",$"value"(6) alias "si", $"value"(7) alias "so", $"value"(8) alias "bi", $"value"(9) alias "bo", $"value"(10) alias "in_val",
        $"value"(11) alias "cs", $"value"(12) alias "us", $"value"(13) alias "sy", $"value"(14) alias "id", $"value"(15) alias "wa", $"value"(16) alias "st")
  }

  /*
  - kafkaWriter Class
  - write row data into kafka producer
  - this class needs one user-define-function to extract data from each row of the dataframe
  */
  // user-define-function to extract value from a Row and this function only works with KafkaWriter class for vmstat data
   val extractRowDataForKafkaWriter: Row=>String = (row:Row) =>{
    val rowMap: Map[String, AnyVal] = row.getValuesMap(row.schema.fieldNames)
    s"${rowMap("topic")}|${rowMap("time")}|${rowMap("r")}|${rowMap("b")}|${rowMap("swpd")}|${rowMap("buff")}|${rowMap("cache")}|${rowMap("si")}|" +
      s"${rowMap("so")}|${rowMap("bi")}|${rowMap("bo")}|${rowMap("in_val")}|${rowMap("cs")}|${rowMap("us")}|${rowMap("sy")}|${rowMap("id")}|${rowMap("wa")}|" +
      s"${rowMap("st")}"
  }

  class KafkaWriter (val topic:String, val servers:String, val func: Row=>String) extends ForeachWriter[Row] {
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", servers)
    kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    var producer: KafkaProducer[String, String] = _
    def open(partitionId: Long, epochId: Long) = {producer = new KafkaProducer(kafkaProperties); true}
    def process(row: Row) = producer.send(new ProducerRecord(topic, func(row)))
    def close(errorOrNull: Throwable) = producer.close
  }
}
