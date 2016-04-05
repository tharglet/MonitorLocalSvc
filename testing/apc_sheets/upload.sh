#!/bin/bash

# for file in `ls *APC*.csv`
for file in Sheffield-APC-2015.csv
do
  curl -v --user admin:admin -X POST --form content=@./$file http://localhost:8080/application/apcIngest
done
