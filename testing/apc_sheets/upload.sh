#!/bin/bash

# for file in `ls *APC*.csv`

curl -v --user admin:admin -X POST --form content=@./s.csv --form instname="University of Sheffield" http://localhost:8080/application/apcIngest
