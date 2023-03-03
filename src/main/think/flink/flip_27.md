# Refactor Source Interface
https://cwiki.apache.org/confluence/display/FLINK/FLIP-27%3A+Refactor+Source+Interface
## 实现协调者
https://issues.apache.org/jira/browse/FLINK-15099
## 状态恢复
https://issues.apache.org/jira/browse/FLINK-20396

## flink source 有中文文档
https://nightlies.apache.org/flink/flink-docs-release-1.16/zh/docs/dev/datastream/sources/

一个数据 source 包括三个核心组件：分片（Splits）、分片枚举器（SplitEnumerator） 以及 源阅读器（SourceReader）
SourceSplit
SplitEnumerator 在jobManager上运行
SourceReader 运行在SourceOperators 上

## flink中的DataStream
DataStream
SingleOutputStreamOperator(里面有transformation)
## flink中的operator
StreamOperator
AbstractStreamOperator

Basic interface for stream operators. Implementers would implement one of OneInputStreamOperator or TwoInputStreamOperator to create operators that process elements.
The class AbstractStreamOperator offers default implementation for the lifecycle and properties methods.
Methods of StreamOperator are guaranteed not to be called concurrently. Also, if using the timer service, timer callbacks are also guaranteed not to be called concurrently with methods on StreamOperator.

## flink中的 transformation 
org.apache.flink.api.dag.Transformation
OneInputTransformation(有operator)
TwoInputTransformation
UnionTransformation(无operator)


##  GlobalAggregateManager 聚合器的作用
  private transient GlobalAggregateManager aggregateManager;

https://issues.apache.org/jira/browse/FLINK-10887
We need to add a new RPC to the JobMaster such that the current watermark for every source 
sub-task can be reported and the current global minimum/maximum watermark 
can be retrieved so that each source can adjust their partition read rates 
in an attempt to keep sources roughly aligned in event time.

step 1 
job master add method 
```
    private final Map<String, Object> accumulators;
```
JobMasterGateway  add method
RpcGlobalAggregateManager encapsulate 封装了taskmanager对jobManager的调用 
GlobalAggregateManager just a interface
用这个做watermark的优先级
https://issues.apache.org/jira/browse/FLINK-31183?jql=project%20%3D%20FLINK%20AND%20component%20%3D%20%22Connectors%20%2F%20Kinesis%22

in taskExecutor,we need get this Manager,pass it to operator.
in org.apache.flink.runtime.taskmanager.Task ,we get this manager,then construct RuntimeEnvironment,
 
 
when use it,We must use enviroment.so let us see how to use it 
in test class GlobalAggregateITCase
```
    public GlobalAggregateManager getGlobalAggregateManager() {
        return taskEnvironment.getGlobalAggregateManager();
    }
```
