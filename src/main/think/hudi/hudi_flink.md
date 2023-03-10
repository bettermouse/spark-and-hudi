# flink中索引的应用
## FlinkOptions 
Hoodie Flink config options. 
### INDEX_TYPE
```
BLOOM | GLOBAL_BLOOM |SIMPLE | GLOBAL_SIMPLE | INMEMORY | HBASE | BUCKET
```
BLOOM 
GLOBAL_BLOOM 
SIMPLE 
GLOBAL_SIMPLE 
INMEMORY 
HBASE 
BUCKET


# flink issue
##  split_reader don't checkpoint before consuming all splits

https://github.com/apache/hudi/issues/8087

org.apache.hudi.source.StreamReadOperator
```
 private final MailboxExecutorAdapter executor;
```
使用了Mailbox? 需要看一下Flip 27是如何解决这个问题的,一直读取数据会影响checkpoint.
SourceTransformation 
by above class,find SourceOperatorFactory

org.apache.flink.streaming.api.operators.SourceOperator snapshot/emitNext
in operator,we can not find this method,but in task,we can find it run in 
mail box 
org.apache.flink.runtime.taskmanager.Task#run
org.apache.flink.streaming.runtime.tasks.StreamTask#executeInvoke

org.apache.flink.streaming.runtime.tasks.StreamTask#notifyCheckpointOperation

# MailboxExecutor 转到flink里面去

## go on 
StreamReadMonitoringFunction
may be in this class.list file spend a long time ?

## 数据同步问题
https://github.com/apache/hudi/pull/8079
I can not understand now.



### learn StreamReadMonitoringFunction class and TestStreamReadMonitoringFunction

