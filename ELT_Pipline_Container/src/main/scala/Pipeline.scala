// Implement ELT Pipeline
object Pipeline extends Serializable {

  def main(args: Array[String]): Unit = {
    // Extract
    val dataSource = ELT.extract()
    // Transform
    val transformedSource = ELT.transform(dataSource)
    // Load
    ELT.Load.to_hdfs(transformedSource,hdfsPath="stream_data/", checkpointPath="checkpoint/", format="parquet", mode="append", compressionType="snappy")
//    ELT.Load.to_console(transformedSource)
  }
}
