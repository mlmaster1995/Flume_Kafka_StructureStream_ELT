import PipelineUtils._
import org.apache.spark.sql
import org.apache.spark.sql._

object ELTComponents extends Serializable {
  // a global spark session
//  val spark = getSparkSession
//  import spark.implicits._

  /*
   - Extract
   - extract function is a user-defined function in PipelineUtils
  */
  def extract(session: SparkSession, extractFunc:(SparkSession)=>sql.DataFrame): sql.DataFrame = extractFunc(session)

  /*
   - Transform
   - transform function is a user-defined function in PipelineUtils
  */
  def transform(session: SparkSession, source: sql.DataFrame, transformFunc: (sql.DataFrame, SparkSession)=>sql.DataFrame):sql.DataFrame = transformFunc(source, session)


  /*
   - multi-end Load class
   - each function in load class has a specific destination to write the stream to
  */
  object Load {
    // print to console
    def toConsole(source:sql.DataFrame, mode:String):Unit=
      source.writeStream.format("console").outputMode(mode).start().awaitTermination()

    // write data stream to hdfs storage
    def toHdfs(source:sql.DataFrame, hdfsPath:String, checkpointPath:String, format:String, mode:String, compressionType:String):Unit=
      source.writeStream.format(format).outputMode(mode).option("compression",compressionType).option("path",hdfsPath)
        .option("checkpointLocation", checkpointPath).start().awaitTermination()

    // write data stream to kafka
    def toKafka(source:sql.DataFrame, topic:String, servers:String, extract_func:Row=>String):Unit =
      source.writeStream.foreach(new KafkaWriter(topic, servers, extract_func)).start().awaitTermination()

    // write data stream to hive metastore
    def toHive(source:sql.DataFrame, hiveDataPath:String, checkpiontPath:String, format:String, mode:String):Unit=
      source.writeStream.format(format).outputMode(mode).option("path",hiveDataPath).option("checkpointLocation",checkpiontPath).start().awaitTermination()

    // write data stream to mysql
    def toMysql(source:sql.DataFrame, url:String, user:String, password:String, insertQuery:String): Unit= {
      source.writeStream.foreach(???).start().awaitTermination()
    }

    def toHBase = ???
    def toMongoDB = ???
    def toS3 = ???
    def toCassandra = ???

  }
}


