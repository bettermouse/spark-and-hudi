# 文档
https://ververica.github.io/flink-cdc-connectors/master/content/connectors/mysql-cdc%28ZH%29.html

 因此，如果不同的作业共享相同的 Server id， 则可能导致从错误的 binlog 位置读取数据。
  因此，建议通过为每个 Reader 设置不同的 Server id SQL Hints, 假设 Source 并行度为 4, 
  我们可以使用 SELECT * FROM source_table /*+ OPTIONS('server-id'='5401-5404') */ ; 来为 4 个 Source readers 中的每一个分配唯一的 Server id。
  
  	增量快照是一种读取表快照的新机制，与旧的快照机制相比， 增量快照有许多优点，包括： （1）在快照读取期间，Source 支持并发读取， （2）在快照读取期间，Source 支持进行 chunk 粒度的 checkpoint， （3）在快照读取之前，Source 不需要数据库锁权限。 如果希望 Source 并行运行，则每个并行 Readers 都应该具有唯一的 Server id，所以 Server id 必须是类似 `5400-6400` 的范围，并且该范围必须大于并行度。 请查阅 增量快照读取 章节了解更多详细信息。
  	
  	
  	
增量快照读取

（1）在快照读取期间，Source 支持并发读取，
（2）在快照读取期间，Source 支持进行 chunk 粒度的 checkpoint，
（3）在快照读取之前，Source 不需要数据库锁权限。


如果希望 source 并行运行，则每个并行 reader 都应该具有唯一的 server id，因此server id的范围必须类似于 5400-6400， 且范围必须大于并行度。在增量快照读取过程中，MySQL CDC Source 首先通过表的主键将表划分成多个块（chunk）， 然后 MySQL CDC Source 将多个块分配给多个 reader 以并行读取表的数据。

MySQL高可用性支持

MySQL 集群中你监控的服务器出现故障后, 你只需将受监视的服务器地址更改为其他可用服务器，然后从最新的检查点/保存点重新启动作业, 作业将从 checkpoint/savepoint 恢复，不会丢失任何记录。
建议为 MySQL 集群配置 DNS（域名服务）或 VIP（虚拟 IP 地址）， 使用mysql cdc连接器的 DNS 或 VIP 地址， DNS或VIP将自动将网络请求路由到活动MySQL服务器。 这样，你就不再需要修改地址和重新启动管道。


读取可以分为 5 个阶段：

SourceReader 读取表数据之前先记录当前的 Binlog 位置信息记为低位点；
SourceReader 将自身区间内的数据查询出来并放置在 buffer 中；
查询完成之后记录当前的 Binlog 位置信息记为高位点；
在增量部分消费从低位点到高位点的 Binlog；
根据主键，对 buffer 中的数据进行修正并输出。

原文链接：https://blog.csdn.net/yang_shibiao/article/details/122781363