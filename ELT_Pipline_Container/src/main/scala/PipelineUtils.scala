import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession}
import org.apache.spark.sql.functions.udf
import java.util.Properties

object PipelineUtils extends Serializable {

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

  val brokers = Map[Int, String](
    1 -> "cxln1.c.thelab-240901.internal:6667",
    2 -> "cxln2.c.thelab-240901.internal:6667",
    3 -> "cxln3.c.thelab-240901.internal:6667",
    4 -> "cxln4.c.thelab-240901.internal:6667"
  )


  val filterRow = udf {x:String=>x.split("\\W").filter(y=>y.length>0) }


  val extractRowValueFromVmstat: Row=>String = (row:Row) =>{
    val rowMap: Map[String, AnyVal] = row.getValuesMap(row.schema.fieldNames)
    s"${rowMap("topic")}|${rowMap("time")}|${rowMap("r")}|${rowMap("b")}|${rowMap("swpd")}|${rowMap("buff")}|${rowMap("cache")}|${rowMap("si")}|" +
      s"${rowMap("so")}|${rowMap("bi")}|${rowMap("bo")}|${rowMap("in_val")}|${rowMap("cs")}|${rowMap("us")}|${rowMap("sy")}|${rowMap("id")}|${rowMap("wa")}|" +
      s"${rowMap("st")}"
  }

  class KafkaWriter (topic:String, servers:String, func: Row=>String) extends ForeachWriter[Row] {
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", servers)
    kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    var producer: KafkaProducer[String, String] = _

    def open(partitionId: Long, epochId: Long) = {
      producer = new KafkaProducer(kafkaProperties)
      true
    }
    def process(row: Row) = {
      producer.send(new ProducerRecord(topic, func(row)))
    }
    def close(errorOrNull: Throwable) = {
      producer.close
    }
  }




}
