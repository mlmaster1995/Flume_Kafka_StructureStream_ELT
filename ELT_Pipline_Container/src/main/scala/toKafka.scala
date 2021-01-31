import PipelineUtils.{brokers, extracFunc, extractRowDataForKafkaWriter, getSparkSession, transformFunc}
import org.apache.spark.sql

// write data stream back to kafka with a different topic
object toKafka extends Serializable with App{

  val spark = getSparkSession
  val dataSource:sql.DataFrame = ELTComponents.extract(spark, extracFunc)
  val transformedSource:sql.DataFrame = ELTComponents.transform(spark, dataSource, transformFunc)

  ELTComponents.Load.toKafka(transformedSource, topic ="toHive", servers = s"${brokers(4)}", extract_func = extractRowDataForKafkaWriter)
}
