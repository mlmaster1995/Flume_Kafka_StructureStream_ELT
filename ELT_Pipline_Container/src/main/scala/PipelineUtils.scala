import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

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

  val filterRow = udf {x:String=>x.split("\\W").filter(y=>y.length>0) }

  val brokers = Map[Int, String](
    1 -> "cxln1.c.thelab-240901.internal:6667",
    2 -> "cxln2.c.thelab-240901.internal:6667",
    3 -> "cxln3.c.thelab-240901.internal:6667",
    4 -> "cxln4.c.thelab-240901.internal:6667"
  )


}
