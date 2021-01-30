import PipelineUtils.{brokers, extractRowValueFromVmstat}

// Implement ELT Pipeline
object Pipeline extends Serializable {

  def main(args: Array[String]): Unit = {
    // Extract
    val dataSource = ELT.extract()
    // Transform
    val transformedSource = ELT.transform(dataSource)
    // Load
//    ELT.Load.toHdfs(transformedSource,hdfsPath="stream_data/", checkpointPath="checkpoint/", format="parquet", mode="append", compressionType="snappy")
//    ELT.Load.toConsole(transformedSource)
//    ELT.Load.toKafka(transformedSource, topic ="toHive", servers = s"${brokers(4)}", extract_func = extractRowValueFromVmstat)
//    ELT.Load.toHive(transformedSource, "/apps/hive/warehouse/chrisy.db/streamhive", "checkpoint/", "parquet", "append")

  }
}
