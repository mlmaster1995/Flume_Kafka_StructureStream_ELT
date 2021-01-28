#! /usr/bin/bash

clear

spark-submit \
--class DataPipeline \
--master yarn \
--deploy-mode client \
--jars spark-sql-kafka-0-10_2.11-2.1.1.jar, kafka-clients-0.10.2.2.jar \ elt_pipline_container_2.11-0.1.jar

