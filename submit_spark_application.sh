#! /usr/bin/bash

clear

spark-submit --class DataPipeline  --master yarn --deploy-mode client --jars /home/kyang35681/spark_jdbc_jar_files/spark-avro_2.11-4.0.0.jar,/home/kyang35681/spark_jdbc_jar_files/mysql-connector-java-5.1.47.jar,/home/kyang35681/spark_jdbc_jar_files/spark-sql-kafka-0-10_2.11-2.1.1.jar,/home/kyang35681/spark_jdbc_jar_files/kafka-clients-0.10.2.2.jar elt_pipline_container_2.11-0.1.jar

