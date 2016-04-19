#!/bin/bash

# for file in `ls *APC*.csv`

#curl -v --user admin:admin -X POST --form content=@./s.csv --form instname="University of Sheffield" http://localhost:8080/application/apcIngest

 files="*.csv"
 regex="([^\-]+)-APC.+\.csv"
 for f in $files
 do
     if [[ $f =~ $regex ]]
     then
         institution="${BASH_REMATCH[1]}"
         echo "Sending file '${f}' for '${institution}'"
         curl -v --user admin:admin -X POST --form content=@$f --form instname="${institution}" http://localhost:8080/application/apcIngest
     else
         echo "Skipping '$f'"
     fi
 done
