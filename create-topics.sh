#!/bin/bash

kafka-topics --create --topic reduce-image-quality --partitions 2 --bootstrap-server kafka:9092
kafka-topics --create --topic generate-thumbnail --partitions 2 --bootstrap-server kafka:9092
kafka-topics --create --topic add-watermark --partitions 2 --bootstrap-server kafka:9092
