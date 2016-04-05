#!/bin/bash

# for file in `ls *APC*.csv`

curl -v --user admin:admin -X POST --form content=@./Sheffield-APC-2015.csv --form instname="University of Sheffield" http://localhost:8080/application/apcIngest
