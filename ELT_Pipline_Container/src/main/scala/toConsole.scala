import PipelineUtils.{extracFunc, getSparkSession, transformFunc}
import org.apache.spark.sql

// print the stream data to console
object toConsole extends Serializable with App {

  val spark = getSparkSession
  val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)
  val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)

  ELTComponents.Load.toConsole(transformedSource, mode="append")
}
