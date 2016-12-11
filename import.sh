#!/bin/bash

./exec_server.sh ~/Downloads/traffic/measurement_* BEGIN_MDP `find /home/sihaya/Downloads/traffic/ -name '*2016-06-0*' | sort -n`
