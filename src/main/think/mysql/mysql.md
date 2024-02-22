## mysql 查看版本
\s
## binlog 开启
show variables like '%log_bin%';
https://www.cnblogs.com/johnnyzen/p/14738877.html
开启Binlog
https://blog.csdn.net/weixin_43682825/article/details/125166353
https://blog.csdn.net/ljb825802164/article/details/105098174

show master STATUS
show binlog events [IN 'log_name'] [FROM pos] [LIMIT [offset,] row_count];

mysqlbinlog mysql-bin.000001 

## 建表
create database myorder; 

create table myorder(orderno varchar(50),money double,id INT(11) PRIMARY KEY   auto_increment);

