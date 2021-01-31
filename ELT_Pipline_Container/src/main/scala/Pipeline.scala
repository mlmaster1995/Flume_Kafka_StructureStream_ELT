import PipelineUtils._
import org.apache.spark.sql

// !!Stream Pipeline ELT Entry Point!!
object Pipeline extends Serializable {

  def main(args: Array[String]): Unit = {
    /*
    - Spark Session
     */
    val spark = getSparkSession

    /*
     - Extract
     - extracFunc is a user-define-function returning a spark dataframe and defined in PipelineUtils class
     - user could extract any stream source feeding to pipeline
     */
    val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)

    /*
     - Transform
     - transformFunc is a user-define-funciton returning a spark dataframe and defined in PipelineUtils class
     - user could define any stream transformation feeding to the source
    */
    val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)

    /*
     - Load
    */
//    ELTComponents.Load.toHdfs(transformedSource,hdfsPath="stream_data/", checkpointPath="checkpoint/", format="parquet", mode="append", compressionType="snappy")
    ELTComponents.Load.toConsole(transformedSource, mode="append")
//    ELTComponents.Load.toKafka(transformedSource, topic ="toHive", servers = s"${brokers(4)}", extract_func = extractRowValueFromVmstatForKafka)
//    ELTComponents.Load.toHive(transformedSource, "/apps/hive/warehouse/chrisy.db/streamhive", "checkpoint/", "parquet", "append")

  }
}
