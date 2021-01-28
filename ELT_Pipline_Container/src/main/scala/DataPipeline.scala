import PipelineUtils._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object DataPipeline extends Serializable {
  def main(args: Array[String]): Unit = {
    // create a spark session
    val spark = getSparkSession
    import spark.implicits._

    // Extract
    val vmSource = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers",s"${brokers(2)},${brokers(3)},${brokers(4)}")
      .option("subscribe","exec").load()

    // Transform
    val vmSourceTrans = vmSource
      .withWatermark("timestamp", "1 seconds")
      .withColumn("raw_value", 'value.cast(StringType))
      .where( !'raw_value.contains("memory") and !'raw_value.contains("buff"))
      .withColumn("value", filterRow('raw_value))
      .select('topic,'timestamp,$"value"(0) alias "r", $"value"(1) alias "b", $"value"(2) alias "swpd", $"value"(3) alias "free", $"value"(4) alias "buff", $"value"(5) alias "cache",
        $"value"(6) alias "si", $"value"(7) alias "so", $"value"(8) alias "bi", $"value"(9) alias "bo", $"value"(10) alias "in", $"value"(11) alias "cs",
        $"value"(12) alias "us", $"value"(13) alias "sy", $"value"(14) alias "id", $"value"(15) alias "wa", $"value"(16) alias "st")


    // Load the stream
    val vmSink = vmSourceTrans.writeStream.format("parquet").outputMode("append").option("compression","snappy").option("path","stream_data/").option("checkpointLocation", "checkpoint/").start()
    vmSink.awaitTermination()
  }
}
