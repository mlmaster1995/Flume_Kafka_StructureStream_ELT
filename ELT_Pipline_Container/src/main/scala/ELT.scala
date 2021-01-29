import PipelineUtils.{brokers, filterRow, getSparkSession}
import org.apache.spark.sql
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.streaming.DataStreamWriter

object ELT extends Serializable {
  // a global spark session
  val spark = getSparkSession
  import spark.implicits._

  // extract function
  def extract(session: SparkSession = spark): sql.DataFrame =
      session.readStream.format("kafka").option("kafka.bootstrap.servers", s"${brokers(1)}").option("subscribe", "exec").load()

  // transform function
  def transform(source:sql.DataFrame):sql.DataFrame =
    source.withWatermark("timestamp", "1 seconds").withColumn("raw_value", 'value.cast(StringType))
      .where( !'raw_value.contains("memory") and !'raw_value.contains("buff")).withColumn("value", filterRow('raw_value))
      .select('topic,'timestamp,$"value"(0) alias "r", $"value"(1) alias "b", $"value"(2) alias "swpd", $"value"(3) alias "free", $"value"(4) alias "buff",
        $"value"(5) alias "cache",$"value"(6) alias "si", $"value"(7) alias "so", $"value"(8) alias "bi", $"value"(9) alias "bo", $"value"(10) alias "in",
        $"value"(11) alias "cs", $"value"(12) alias "us", $"value"(13) alias "sy", $"value"(14) alias "id", $"value"(15) alias "wa", $"value"(16) alias "st")

  // multi-end load class
  object Load {
    // print to console
    def to_console(source:sql.DataFrame):Unit=
      source.writeStream.format("console").outputMode("append").start().awaitTermination()

    // save data stream to hdfs
    def to_hdfs(source:sql.DataFrame,hdfsPath:String, checkpointPath:String, format:String, mode:String, compressionType:String):Unit=
      source.writeStream.format(format).outputMode(mode).option("compression",compressionType).option("path",hdfsPath)
        .option("checkpointLocation", checkpointPath).start().awaitTermination()

    def to_hive= ???
    def to_mongodb = ???
    def to_mysql= ???
  }

}
