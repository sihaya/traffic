#!/bin/bash

mvn clean package assembly:single && \
	docker build -t traffic:1 . && \
	docker save traffic:1 | gzip > target/traffic_1.dock.gz
	
echo "build target/traffic_1.dock.gz"
