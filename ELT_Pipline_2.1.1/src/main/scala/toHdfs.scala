import PipelineUtils.{extracFunc, getSparkSession, transformFunc}
import org.apache.spark.sql

// write the stream data to hdfs
class toHdfs extends Serializable with App{
  // build a spark session
  val spark = getSparkSession
  // extract data
  val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)
  // transform data
  val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)
  // load data
  ELTComponents.Load.toHdfs(transformedSource,hdfsPath="stream_data/", checkpointPath="checkpoint/", format="parquet", mode="append", compressionType="snappy")
}
