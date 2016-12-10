#!/bin/bash
cp=`mvn dependency:build-classpath | grep -A1 Depen | tail -n1`
java -cp $cp:target/traffic-0.0.1-SNAPSHOT.jar nl.desertspring.traffic.TrafficImportApp $@
