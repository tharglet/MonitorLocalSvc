#curl -v --user "admin:admin123454321nimad__!!" -X POST --form content=@../testdata/monitor_local_ki_people.csv http://localhost:8080/monitorLocalSvc/internalApi/personIngest
curl -v --user "admin:admin123454321nimad__!!" -X POST --form content=@../testdata/monitor_people.csv http://localhost:8080/monitorLocalSvc/internalApi/personIngest
