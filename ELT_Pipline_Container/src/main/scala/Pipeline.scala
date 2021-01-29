// Perform ELT
object Pipeline extends Serializable {

  def main(args: Array[String]): Unit = {
    // Extract
    val dataSource = ELT.extract()
    // Transform
    val transformedSource = ELT.transform(dataSource)
    // Load
    ELT.Load.to_hdfs(transformedSource, "parquet", "append", "snappy", "stream_data/", "checkpoint/").start().awaitTermination()
  }
}
