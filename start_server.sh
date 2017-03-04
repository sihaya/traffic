#!/bin/bash

echo "connecting to database ${TRAFFIC_DB_URL}"

exec java -Dtraffic.db.url=${TRAFFIC_DB_URL} \
    -Dtraffic.db.username=${TRAFFIC_DB_USERNAME} \
    -Dtraffic.db.password=${TRAFFIC_DB_PASSWORD} \
    -jar /opt/traffic-0.0.1-SNAPSHOT-jar-with-dependencies.jar 5678