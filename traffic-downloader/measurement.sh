#!/bin/bash

cd /home/sihaya/Downloads/traffic
filename=measurement_`date -Is`.xml.gz

wget http://opendata.ndw.nu/measurement.xml.gz -a log -O $filename

zcat $filename | curl -s -X POST -d @- http://localhost:4567/importmeasurementpoints > $filename.import
