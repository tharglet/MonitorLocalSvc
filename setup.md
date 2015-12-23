mysql> create database MonitorLocal default charset utf8 default collate utf8_general_ci;
mysql> grant all on Monitorlocal.* to 'k-int'@'localhost';
mysql> grant all on Monitorlocal.* to 'k-int'@'localhost.localdomain';
mysql> grant all on Monitorlocal.* to 'k-int'@'%';

