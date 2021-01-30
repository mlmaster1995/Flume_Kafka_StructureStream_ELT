import PipelineUtils.{KafkaWriter, brokers, filterRow, getSparkSession}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import java.util.Properties
import scala.reflect.ClassManifestFactory.AnyVal

object ELT extends Serializable {
  // a global spark session
  val spark = getSparkSession
  import spark.implicits._

  // extract function
  def extract(session: SparkSession = spark): sql.DataFrame =
      session.readStream.format("kafka").option("kafka.bootstrap.servers", s"${brokers(1)}").option("subscribe", "exec").load()

  // transform function
  def transform(source:sql.DataFrame):sql.DataFrame = {
//    source.select('timestamp.cast(StringType), 'topic.cast(StringType))
    source.withWatermark("timestamp", "1 seconds").withColumn("raw_value", 'value.cast(StringType))
      .where( !'raw_value.contains("memory") and !'raw_value.contains("buff")).withColumn("value", filterRow('raw_value))
      .select('topic,'timestamp alias "time",$"value"(0) alias "r", $"value"(1) alias "b", $"value"(2) alias "swpd", $"value"(3) alias "free", $"value"(4) alias "buff",
        $"value"(5) alias "cache",$"value"(6) alias "si", $"value"(7) alias "so", $"value"(8) alias "bi", $"value"(9) alias "bo", $"value"(10) alias "in_val",
        $"value"(11) alias "cs", $"value"(12) alias "us", $"value"(13) alias "sy", $"value"(14) alias "id", $"value"(15) alias "wa", $"value"(16) alias "st")
  }

  // multi-end load class
  object Load {
    // print to console
    def toConsole(source:sql.DataFrame):Unit=
      source.writeStream.format("console").outputMode("append").start().awaitTermination()

    // write data stream to hdfs storage
    def toHdfs(source:sql.DataFrame, hdfsPath:String, checkpointPath:String, format:String, mode:String, compressionType:String):Unit=
      source.writeStream.format(format).outputMode(mode).option("compression",compressionType).option("path",hdfsPath)
        .option("checkpointLocation", checkpointPath).start().awaitTermination()

    // write data stream to kafka
    def toKafka(source:sql.DataFrame, topic:String, servers:String, extract_func:Row=>String):Unit =
      source.writeStream.foreach(new KafkaWriter(topic, servers, extract_func)).start().awaitTermination()

    // write data stream to hive metastore
    def toHive(source:sql.DataFrame, hiveDataPath:String, checkpiontPath:String, format:String, mode:String):Unit=
      source.writeStream.format(format).outputMode(mode).option("path",hiveDataPath).option("checkpointLocation",checkpiontPath).start().awaitTermination()

    // write data stream to mysql
    def toMysql= {
      ???
    }


    def toMongoDB = ???
    def toS3 = ???
    def toCassandra = ???

  }
}


