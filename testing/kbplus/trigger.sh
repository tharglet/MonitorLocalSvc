#!/bin/bash

# for file in `ls *APC*.csv`

curl -v --user admin:admin -X GET http://localhost:8080/application/command/kbplus
