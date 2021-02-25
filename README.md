## Stream Data Pipeline with Flume Kafka StructureStream 

### About The Project
* This project was conducted in 2020 for the "Big Data Hadoop" training, and it was reorganized and uploaded to
the repo in 2021. All pipelines were tested on the [CloudxLab](https://cloudxlab.com/home) platform. 
* The data source is the computer system information generated by the linux 
command ```$ vmstat [options][delay [count]]```. 
* The data is ingested into Kafka by Flume, transformed by
Spark Structured Streaming. The load destination includes Console, HDFS, Kafka and Hive Metastore. The further upgrade 
of this project is limited by the spark version.

### Pipeline
Pipelines are shown as the following image: 

The pipeline includes: 
* vmstat Data -> Flume -> Kafka -> Structured Stream -> Console
* vmstat Data -> Flume -> Kafka -> Structured Stream -> HDFS
* vmstat Data -> Flume -> Kafka -> Structured Stream -> Kafka
* vmstat Data -> Flume -> Kafka -> Structured Stream -> Hive Metastore

### Built With* 
* [Spark 2.1.1](https://spark.apache.org/docs/2.1.1/)
* [Flume 1.5.2](https://flume.apache.org/releases/1.5.2.html)
* [Kafka 0.10.2](https://kafka.apache.org/0102/documentation.html)

### Usage 
* "vmstat_flume_kafka.conf" is the flume agent file ands kafka servers needs to be modified for further use.
* "submit_spark_applicaiton.sh" is a bash script file to submit the spark applicaiton with all jar files.
* "kafka-clients-0.10.2.2.jar" and "spark-sql-kafka-0-10_2.11-2.1.1.jar" are two jar files for
this proejct. 

### Contact
* C. Young: kyang3@lakeheadu.ca