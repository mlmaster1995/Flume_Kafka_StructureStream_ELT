import PipelineUtils.{extracFunc, getSparkSession, transformFunc}
import org.apache.spark.sql

// write the stream data to hdfs
class toHdfs extends Serializable with App{

  val spark = getSparkSession
  val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)
  val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)

  ELTComponents.Load.toHdfs(transformedSource,hdfsPath="stream_data/", checkpointPath="checkpoint/", format="parquet", mode="append", compressionType="snappy")
}
