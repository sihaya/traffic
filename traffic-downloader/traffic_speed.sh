#!/bin/bash

cd /home/sihaya/Downloads/traffic
filename=traffic_speed_`date -Is`.xml.gz

wget http://opendata.ndw.nu/trafficspeed.xml.gz -a log -O $filename 

zcat $filename | curl -s -X POST -d @- http://localhost:4567/importmeasurements > $filename.import
