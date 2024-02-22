# Refactor Source Interface
https://cwiki.apache.org/confluence/display/FLINK/FLIP-27%3A+Refactor+Source+Interface
## Generic enumerator-reader communication mechanism
https://issues.apache.org/jira/browse/FLINK-15099

## Reader Interface and Threading Model
No closed work loop, so it does not need to manage locking 不要一直运行
Non-blocking progress methods, to it supports running in an actor/mailbox/dispatcher style operator 不要阻碍
All methods called by the same on single thread, so implementors need not deal with concurrency
Watermark / Event time handling abstracted to be extensible for split-awareness and alignment (see below sections "Per Split Event-time" and "Event-time Alignment")
All readers should naturally supports state and checkpoints
Watermark generation should be circumvented for batch execution

how to do 
Splits 用来做savepoint和作业分配,分配作业和从状态恢复对于reader是相同的
促进reader不阻赛,返回一个future
建立高级别的higher-level primitives
将event time /water mark 藏在SourceOutput 中,通过不同的 source contexts来区分它们.
The SourceOutput also abstract the per-partition watermark tracking.

The SourceReader will run as a PushingAsyncDataInput which works well with 
the new mailbox threading model in the tasks, similar to the network inputs.
### what is PushingAsyncDataInput
emitNext(DataOutput)
## Base implementation and high-level readers
https://baijiahao.baidu.com/s?id=1717551675940977107&wfr=spider&for=pc
Most  connectors are not asynchronous,We should add a new thread.
We hope  to solve this by building higher level source abstractions that offer simpler interfaces that allow for blocking calls.
These higher level abstractions would also solve the issue of sources that handle multiple splits concurrently, and the per-split event time logic.

categories
One reader single splits. (Some dead simple blocking readers)
One reader multiple splits.
    Sequential Single Split (File, database query, most bounded splits) 顺序
    Multi-split multiplexed (Kafka, Pulsar, Pravega, ...) 多路复用
    Multi-split multi-threaded (Kinesis, ...) 多切片/多线程
splitThread one/many  consume one/many
org.apache.flink.connector.base.source.reader.splitreader.SplitReader 读取数据
org.apache.flink.connector.base.source.reader.RecordsWithSplitIds 返回的接口

RecordEmitter 数据来源于 RecordsWithSplitIds
Convert the raw record type <E> into the eventual record type <T>
Provide an event time timestamp for the record that it processes.

With the base implementation users writing their own source can just focus on:
从外部系统获取数据
Fetch records from external system.
执行记录解析和转换
Perform record parsing and conversion.
Extract timestamps and optionally deal with watermarks. A followup FLIP will provide some default behaviors for users to deal with their watermark.
### flip-126
https://cwiki.apache.org/confluence/display/FLINK/FLIP-126%3A+Unify+%28and+separate%29+Watermark+Assigners

## go on
Some brief explanations:

When a new split is added to the SourceReader by SplitEnumerator, the initial state of that new split is put into a state map maintained by the SourceReaderBase before the split is assigned to a SplitReader.
The records are passed from the the SplitReaders to the RecordEmitter in RecordsBySplitIds. This allows the SplitReader to enqueue records in a batch manner, which benefits performance.
The SourceReaderBase iterates over each records and looks up their corresponding split state. The Record and its corresponding split state is passed to the RecordEmitter.

The SourceReaderBase iterates over each records and looks up their corresponding split state. The Record and its corresponding split state is passed to the RecordEmitter.
## Failover
SourceReader 状态
The assigned splits
The state of the splits (e.g. Kafka offsets, XX file offset, etc)
##  SplitEnumerator 状态 
The unassigned splits 未分配的状态
The splits that have been assigned but not successfully checkpointed yet.
 The assigned but uncheckpointed splits will be associated with each of the checkpoint id they belong to.(看不芯片)

## Top level public interfaces
Source - A factory style class that helps create SplitEnumerator and SourceReader at runtime.
SourceSplit - An interface for all the split types.
SplitEnumerator - Discover the splits and assign them to the SourceReaders
SplitEnumeratorContext - Provide necessary information to the SplitEnumerator to assign splits and send custom events to the the SourceReaders.
SplitAssignment - A container class holding the source split assignment for each subtask.
SourceReader - Read the records from the splits assigned by the SplitEnumerator.
SourceReaderContext - Provide necessary function to the SourceReader to communicate with SplitEnumerator.
SourceOutput - A collector style interface to take the records and timestamps emit by the SourceReader.
WatermarkOutput - An interface for emitting watermark and indicate idleness of the source.
Watermark - A new Watermark class will be created in the package org.apache.flink.api.common.eventtime. This class will eventually replace the existing Watermark in org.apache.flink.streaming.api.watermark. This change allows flink-core to remain independent of other modules. Given that we will eventually put all the watermark generation into the Source, this change will be necessary. Note that this FLIP does not intended to change the existing way that watermark can be overridden in the DataStream after they are emitted by the source.

SourceOutput
SourceReader emits records by  SourceOutput(ReaderOutput)
可以发送watermark/record 到下游处理
The interface provided by Flink task to the {@link SourceReader} to emit records to downstream operators for message processing.

ReaderOutput 是一个SourceOutput,建议仅处理一个split,比如批处理
大部分source 是split-specific outputs

## 感受到了 接口编程的魅力,通过一组接口
ReaderOutput 比如 
StreamingReaderOutput
An implementation of TimestampsAndWatermarks where all watermarking/event-time operations are no-ops. This should be used in execution contexts where no watermarks are needed, for example in BATCH execution mode.
# other

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
can be retrieved so that each source can adjust their partition read1 rates 
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


# code
## SourceReader
public interface SourceReader<T, SplitT extends SourceSplit>
        extends AutoCloseable, CheckpointListener {
## SourceReaderBase
```
/**
 * An abstract implementation of {@link SourceReader} which provides some sychronization between the
 * mail box main thread and the SourceReader internal threads. This class allows user to just
 * provide a {@link SplitReader} and snapshot the split state.
 *
 * @param <E> The rich element type that contains information for split state update or timestamp
 *     extraction.
 * @param <T> The final element type to emit.
 * @param <SplitT> the immutable split type.
 * @param <SplitStateT> the mutable type of split state.
 */
public abstract class SourceReaderBase<E, T, SplitT extends SourceSplit, SplitStateT>
        implements SourceReader<T, SplitT> {
//快照的时候使用
    /** The state of the splits. */
    private final Map<String, SplitContext<T, SplitStateT>> splitStates;
```
### RecordsWithSplitIds
/** An interface for the elements passed from the fetchers to the source reader. */

    /** The latest fetched batch of records-by-split from the split reader. */
    @Nullable private RecordsWithSplitIds<E> currentFetch;
### SplitFetcherManager
```
/**
 * A class responsible for starting the {@link SplitFetcher} and manage the life cycles of them.
 * This class works with the {@link SourceReaderBase}.
 *
 * <p>The split fetcher manager could be used to support different threading models by implementing
 * the {@link #addSplits(List)} method differently. For example, a single thread split fetcher
 * manager would only start a single fetcher and assign all the splits to it. A one-thread-per-split
 * fetcher may spawn a new thread every time a new split is assigned.
 */
public abstract class SplitFetcherManager<E, SplitT extends SourceSplit> {

    /**
     * Synchronize method to ensure no fetcher is created after the split fetcher manager has
     * closed.
     *
     * @return the created split fetcher.
     * @throws IllegalStateException if the split fetcher manager has closed.
     */
    protected synchronized SplitFetcher<E, SplitT> createSplitFetcher() {
```

#### SingleThreadFetcherManager
```

/**
 * A Fetcher Manager with a single fetching thread (I/O thread) that handles all splits
 * concurrently.
 *
 * <p>This pattern is, for example, useful for connectors like File Readers, Apache Kafka Readers,
 * etc. In the example of Kafka, there is a single thread that reads all splits (topic partitions)
 * via the same client. In the example of the file source, there is a single thread that reads the
 * files after another.
 */
public class SingleThreadFetcherManager<E, SplitT extends SourceSplit>
        extends SplitFetcherManager<E, SplitT> {
//通过仅有一个fetcher来实现
    @Override
    public void addSplits(List<SplitT> splitsToAdd) {
        SplitFetcher<E, SplitT> fetcher = getRunningFetcher();
        if (fetcher == null) {
            fetcher = createSplitFetcher();
            // Add the splits to the fetchers.
            fetcher.addSplits(splitsToAdd);
            startFetcher(fetcher);
        } else {
            fetcher.addSplits(splitsToAdd);
        }
    }

```
#### Supplier
```
    /** A supplier to provide split readers. */
    private final Supplier<SplitReader<E, SplitT>> splitReaderFactory;
```
##### SplitReader
```
/**
 * An interface used to read1 from splits. The implementation could either read1 from a single split
 * or from multiple splits.
 *
 * @param <E> the element type.
 * @param <SplitT> the split type.
 */
public interface SplitReader<E, SplitT extends SourceSplit> {

```
## FutureCompletingBlockingQueue 队列
## RecordEmitter



## Integrate Operator Coordinators with Checkpoints
The operator coordinators are stateful and hence need to store state in checkpoints.
The initial implementation approach is to trigger coordinator checkpoints first, and when all coordinator checkpoints are done, then the source checkpoint barriers will be injected.

Note: This functionality will eventually replace the checkpoint master hooks.


