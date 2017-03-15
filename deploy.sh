#!/bin/bash

if [ -z "$TRAFFIC_HOST" ]; then
	echo "please set TRAFFIC_HOST"
	exit 1
fi

scp target/traffic_1.dock.gz $TRAFFIC_HOST:deploy
ssh TRAFFIC_HOST cd deploy && zcat traffic_1.dock.gz | docker load && docker-compose up -d

echo "zcat ~/Downloads/traffic/measurement.xml.gz | curl -s -X POST -d @- http://localhost:5678/importmeasurementpoints"
