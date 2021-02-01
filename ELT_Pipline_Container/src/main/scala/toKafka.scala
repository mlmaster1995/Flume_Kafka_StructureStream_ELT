import PipelineUtils.{brokers, extracFunc, extractRowDataForKafkaWriter, getSparkSession, transformFunc}
import org.apache.spark.sql

// write data stream back to kafka with a different topic
object toKafka extends Serializable with App{
  // build a spark session
  val spark = getSparkSession
  // extract data
  val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)
  // transform data
  val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)
  // load data
  ELTComponents.Load.toKafka(transformedSource, topic ="toHive", servers = s"${brokers(4)}", extract_func = extractRowDataForKafkaWriter)
}
