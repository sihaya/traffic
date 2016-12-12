#!/bin/bash
cp=`mvn dependency:build-classpath | grep -A1 Depen | tail -n1`
java -cp $cp:target/traffic-0.0.1-SNAPSHOT.jar nl.desertspring.traffic.TrafficRestApp $@

echo "please import mst's"

echo "zcat ~/Downloads/traffic/measurement_2016-12-09T13\:00\:01+01\:00.xml.gz | curl -X POST -d @- http://localhost:4567/importmeasurementpoints"
