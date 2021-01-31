import PipelineUtils.{extracFunc, getSparkSession, transformFunc}
import org.apache.spark.sql

// write the stream to the metastore
object toHiveMetaStore extends Serializable with App{
  val spark = getSparkSession
  val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)
  val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)

  ELTComponents.Load.toHive(transformedSource, "/apps/hive/warehouse/chrisy.db/streamhive", "checkpoint/", "parquet", "append")

}
