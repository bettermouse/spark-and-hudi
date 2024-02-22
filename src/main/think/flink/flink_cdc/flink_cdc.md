# 使用最近的offset 新增表 flink 任务不消费
## com.github.shyiko.mysql.binlog.event.EventType
```
    /**
     * This event precedes each row operation event. It maps a table definition to a number, where the table definition
     * consists of database and table names and column definitions. The purpose of this event is to enable replication
     * when a table has different definitions on the master and slave. Row operation events that belong to the same
     * transaction may be grouped into sequences, in which case each such sequence of events begins with a sequence
     * of TABLE_MAP events: one per table used by events in the sequence.
     * Used in case of RBR.
     */
    TABLE_MAP,
```
com.ververica.cdc.connectors.mysql.debezium.task.context.MySqlErrorHandler#setProducerThrowable
```
            if (matcher.find()) {
                String databaseName = matcher.group(1);
                String tableName = matcher.group(2);
                TableId tableId = new TableId(databaseName, null, tableName);
                if (context.getSchema().schemaFor(tableId) == null) {
                    LOG.warn("Schema for table " + tableId + " is null");
                    return;
                }
```
## debug的地方
```
io.debezium.connector.mysql.MySqlStreamingChangeEventSource#handleEvent
(eventHeader instanceof EventHeaderV4)&& (event.getData() instanceof TableMapEventData)&&((TableMapEventData)(event.getData())).getTable().equals("base_area")
```
table map 调用的是这里
eventHandlers.put(EventType.TABLE_MAP, this::handleUpdateTableMetadata);

## debug只能定位到问题的点,还需要正向去查看问题
com.ververica.cdc.connectors.mysql.debezium.task.context.MySqlTaskContextImpl
com.github.shyiko.mysql.binlog.BinaryLogClient
BinaryLogClient 是连接Mysql binlog的地方

com.ververica.cdc.connectors.mysql.source.reader.MySqlSplitReader


## 解决方案
https://github.com/ververica/flink-cdc-connectors/pull/1464

# 源码分析
https://debezium.io/documentation/reference/1.5/development/engine.html
```
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-api</artifactId>
    <version>${version.debezium}</version>
</dependency>
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-embedded</artifactId>
    <version>${version.debezium}</version>
</dependency>

对应的连接器
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-connector-mongodb</artifactId>
    <version>${version.debezium}</version>
</dependency>
```
通常,debezium写数据到不同的kafka,供下游使用,这种方式可以被不同的应用消费.

```
public class TestSchemaMapping {
    public static void main(String[] args) {
        // Define the configuration for the Debezium Engine with MySQL connector...
        final Properties props = config.asProperties();
        props.setProperty("name", "engine");
//offset存储 
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "/tmp/offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        /* begin connector properties */
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "3306");
        props.setProperty("database.user", "mysqluser");
        props.setProperty("database.password", "mysqlpw");
        props.setProperty("database.server.id", "85744");
        props.setProperty("database.server.name", "my-app-connector");
//schema存储 
        props.setProperty("database.history",
                "io.debezium.relational.history.FileDatabaseHistory");
        props.setProperty("database.history.file.filename",
                "/path/to/storage/dbhistory.dat");

// Create the engine with this configuration ...
        try (DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(record -> {
                    System.out.println(record);
                }).build()
        ) {
            // Run the engine asynchronously ...
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(engine);

            // Do something else or wait for a signal or an event
        }
// Engine is stopped when the main code is finished
    }
}

```

## BinaryLogClient
在这里面引入
[mysql] Expose MySqlSource API that supports incremental snapshot (#495)

```
        client.registerEventListener(connectorConfig.bufferSizeForStreamingChangeEventSource() == 0
                ? this::handleEvent
                : (new EventBuffer(connectorConfig.bufferSizeForStreamingChangeEventSource(), this, context))::add);
//设置binlog /gtid

            GtidSet filteredGtidSet = context.filterGtidSet(availableServerGtidSet, purgedServerGtidSet);
            if (filteredGtidSet != null) {
                // We've seen at least some GTIDs, so start reading from the filtered GTID set ...
                logger.info("Registering binlog reader with GTID set: {}", filteredGtidSet);
                String filteredGtidSetStr = filteredGtidSet.toString();
                client.setGtidSet(filteredGtidSetStr);
                source.setCompletedGtidSet(filteredGtidSetStr);
                gtidSet = new com.github.shyiko.mysql.binlog.GtidSet(filteredGtidSetStr);
            }
            else {
                // We've not yet seen any GTIDs, so that means we have to start reading the binlog from the beginning ...
                client.setBinlogFilename(source.binlogFilename());
                client.setBinlogPosition(source.binlogPosition());
                gtidSet = new com.github.shyiko.mysql.binlog.GtidSet("");
            }
```
BinaryLogClient  从mysql消费Binlog
BinaryLogFileReader 离线Binlog 处理
EventDeserializer->( EventHeader, EventType,EventData)
https://github.com/shyiko/mysql-binlog-connector-java
https://blog.csdn.net/m0_69424697/article/details/124947861

### 如何指定高低水位来消费binlog
如果任务执行完了,直接关闭概client
```
            while (context.isRunning()) {
                Thread.sleep(100);
            }
        }
        finally {
            try {
                client.disconnect();
            }
            catch (Exception e) {
                LOGGER.info("Exception while stopping binary log client", e);
            }
        }
```
### MySqlSplitReader implement SplitReader 读取切片数据
### SplitFetcher
```
/** The internal fetcher runnable responsible for polling message from the external system. */
public class SplitFetcher<E, SplitT extends SourceSplit> implements Runnable {
```
一个fetcher包含一个reader
private final BlockingDeque<SplitFetcherTask> taskQueue;

添加split通过任务的方式添加到队列里面
    /**
     * Add splits to the split fetcher. This operation is asynchronous.
     *
     * @param splitsToAdd the splits to add.
     */
    public void addSplits(List<SplitT> splitsToAdd) {
        enqueueTask(new AddSplitsTask<>(splitReader, splitsToAdd, assignedSplits));
        wakeUp(true);
    }
    
run->runOnce
```
    /** Package private method to help unit test. */
    void runOnce() {
        try {
            // The fetch task should run if the split assignment is not empty or there is a split
            // change.
            //
            if (shouldRunFetchTask()) {
                runningTask = fetchTask;
            } else {
                runningTask = taskQueue.take();
            }
            // Now the running task is not null. If wakeUp() is called after this point,
            // task.wakeUp() will be called. On the other hand, if the wakeUp() call was make before
            // this point, the wakeUp flag must have already been set. The code hence checks the
            // wakeUp
            // flag first to avoid an unnecessary task run.
            // Note that the runningTask may still encounter the case that the task is waken up
            // before
            // the it starts running.
            LOG.debug("Prepare to run {}", runningTask);
            if (!wakeUp.get() && runningTask.run()) {
                LOG.debug("Finished running task {}", runningTask);
                // the task has finished running. Set it to null so it won't be enqueued.
                runningTask = null;
                checkAndSetIdle();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                            "SplitFetcher thread %d received unexpected exception while polling the records",
                            id),
                    e);
        }
        // If the task is not null that means this task needs to be re-executed. This only
        // happens when the task is the fetching task or the task was interrupted.
        maybeEnqueueTask(runningTask);
        synchronized (wakeUp) {
            // Set the running task to null. It is necessary for the shutdown method to avoid
            // unnecessarily interrupt the running task.
            runningTask = null;
            // Set the wakeUp flag to false.
            wakeUp.set(false);
            LOG.debug("Cleaned wakeup flag.");
        }
    }
```

SplitFetcherTask(AddSplitsTask/FetchTask),让添加切片,运行切片,两个任务串行执行
####  FetchTask
   @Override
    public boolean run() throws IOException {
        try {
            if (!isWakenUp() && lastRecords == null) {
                lastRecords = splitReader.fetch();
            }
对于mysql,仅将这个task放入queue中,一个个消费.如果是kafka,直接对consumer修改.
用户需要实现 MySqlSplitReader(SplitReader<SourceRecord, MySqlSplit>),
SplitReader会和fetcher task一起使用

```
/**
 * An interface used to read1 from splits. The implementation could either read1 from a single split
 * or from multiple splits.
 *
 * @param <E> the element type.
 * @param <SplitT> the split type.
 */
public interface SplitReader<E, SplitT extends SourceSplit> {

    /**
     * Fetch elements into the blocking queue for the given splits. The fetch call could be blocking
     * but it should get unblocked when {@link #wakeUp()} is invoked. In that case, the
     * implementation may either decide to return without throwing an exception, or it can just
     * throw an interrupted exception. In either case, this method should be reentrant, meaning that
     * the next fetch call should just resume from where the last fetch call was waken up or
     * interrupted.
     *
     * @return the Ids of the finished splits.
     * @throws IOException when encountered IO errors, such as deserialization failures.
     */
    RecordsWithSplitIds<E> fetch() throws IOException;

    /**
     * Handle the split changes. This call should be non-blocking.
     *
     * @param splitsChanges the split changes that the SplitReader needs to handle.
     */
    void handleSplitsChanges(SplitsChange<SplitT> splitsChanges);

    /** Wake up the split reader in case the fetcher thread is blocking in {@link #fetch()}. */
    void wakeUp();

    /**
     * Close the split reader.
     *
     * @throws Exception if closing the split reader failed.
     */
    void close() throws Exception;
}

```
FetchTask,如何与SplitReader
```
    @Override
    public boolean run() throws IOException {
        try {
             //没有被叫醒
            if (!isWakenUp() && lastRecords == null) {
                lastRecords = splitReader.fetch();
            }

            if (!isWakenUp()) {
                // The order matters here. We must first put the last records into the queue.
                // This ensures the handling of the fetched records is atomic to wakeup.
                if (elementsQueue.put(fetcherIndex, lastRecords)) {
                    if (!lastRecords.finishedSplits().isEmpty()) {
                        // The callback does not throw InterruptedException.
                        splitFinishedCallback.accept(lastRecords.finishedSplits());
                    }
                    lastRecords = null;
                }
            }
        } catch (InterruptedException e) {
            // this should only happen on shutdown
            throw new IOException("Source fetch execution was interrupted", e);
        } finally {
            // clean up the potential wakeup effect. It is possible that the fetcher is waken up
            // after the clean up. In that case, either the wakeup flag will be set or the
            // running thread will be interrupted. The next invocation of run() will see that and
            // just skip.
            if (isWakenUp()) {
                wakeup = false;
            }
        }
        // The return value of fetch task does not matter.
        return true;
    }

```

com.ververica.cdc.connectors.mysql.debezium.task.MySqlBinlogSplitReadTask
通过监听器来实现,如果数据不需要发送
```
    @Override
    protected void handleEvent(Event event) {
        super.handleEvent(event);
        // check do we need to stop for read1 binlog for snapshot split.
        if (isBoundedRead()) {
            final BinlogOffset currentBinlogOffset = getBinlogPosition(offsetContext.getOffset());
            // reach the high watermark, the binlog reader should finished
            if (currentBinlogOffset.isAtOrAfter(binlogSplit.getEndingOffset())) {
                // send binlog end event
                try {
                    signalEventDispatcher.dispatchWatermarkEvent(
                            binlogSplit,
                            currentBinlogOffset,
                            SignalEventDispatcher.WatermarkKind.BINLOG_END);
                } catch (InterruptedException e) {
                    LOG.error("Send signal event error.", e);
                    errorHandler.setProducerThrowable(
                            new DebeziumException("Error processing binlog signal event", e));
                }
                // tell reader the binlog task finished
                ((SnapshotBinlogSplitChangeEventSourceContextImpl) context).finished();
            }
        }
    }
```

### 具体任务的执行者
MySqlBinlogSplitReadTask->MySqlStreamingChangeEventSource


# 任务执行太复杂了,单独整理一下
## SplitReader
MySqlSplitReader 
//包含执行者
@Nullable private DebeziumReader<SourceRecord, MySqlSplit> currentReader;

SnapshotSplitReader
BinlogSplitReader

```
    private void checkSplitOrStartNext() throws IOException {
        if (canAssignNextSplit()) {
            MySqlSplit nextSplit = splits.poll();
            if (nextSplit == null) {
                return;
            }

            currentSplitId = nextSplit.splitId();

            if (nextSplit.isSnapshotSplit()) {
                if (currentReader instanceof BinlogSplitReader) {
                    LOG.info(
                            "This is the point from binlog split reading change to snapshot split reading");
                    currentReader.close();
                    currentReader = null;
                }
                if (currentReader == null) {
                    final MySqlConnection jdbcConnection =
                            createMySqlConnection(sourceConfig.getDbzConfiguration());
                    final BinaryLogClient binaryLogClient =
                            createBinaryClient(sourceConfig.getDbzConfiguration());
                    final StatefulTaskContext statefulTaskContext =
                            new StatefulTaskContext(sourceConfig, binaryLogClient, jdbcConnection);
                    currentReader = new SnapshotSplitReader(statefulTaskContext, subtaskId);
                }
            } else {
                // point from snapshot split to binlog split
                if (currentReader != null) {
                    LOG.info("It's turn to read1 binlog split, close current snapshot reader");
                    currentReader.close();
                }
                final MySqlConnection jdbcConnection =
                        createMySqlConnection(sourceConfig.getDbzConfiguration());
                final BinaryLogClient binaryLogClient =
                        createBinaryClient(sourceConfig.getDbzConfiguration());
                final StatefulTaskContext statefulTaskContext =
                        new StatefulTaskContext(sourceConfig, binaryLogClient, jdbcConnection);
                currentReader = new BinlogSplitReader(statefulTaskContext, subtaskId);
                LOG.info("BinlogSplitReader is created.");
            }
            currentReader.submitSplit(nextSplit);
        }
    }
```
### DebeziumReader submitSplit中执行任务.
#### SnapshotSplitReader
// task to read1 snapshot for current split
private MySqlSnapshotSplitReadTask splitSnapshotReadTask;
pollSplitRecords 为入口
```
    @Nullable
    @Override
    public Iterator<SourceRecord> pollSplitRecords() throws InterruptedException {
        checkReadException();

        if (hasNextElement.get()) {
            // data input: [low watermark event][snapshot events][high watermark event][binlog
            // events][binlog-end event]
            // data output: [low watermark event][normalized events][high watermark event]
            boolean reachBinlogEnd = false;
            final List<SourceRecord> sourceRecords = new ArrayList<>();
            while (!reachBinlogEnd) {
                checkReadException();
                List<DataChangeEvent> batch = queue.poll();
                for (DataChangeEvent event : batch) {
                    sourceRecords.add(event.getRecord());
                    if (RecordUtils.isEndWatermarkEvent(event.getRecord())) {
                        reachBinlogEnd = true;
                        break;
                    }
                }
            }
            // snapshot split return its data once
            hasNextElement.set(false);
            return normalizedSplitRecords(currentSnapshotSplit, sourceRecords, nameAdjuster)
                    .iterator();
        }
        // the data has been polled, no more data
        reachEnd.compareAndSet(false, true);
        return null;
    }
```
#### BinlogSplitReader
EventDispatcher 读取binlog,将数据放入EventDispatcher中(queue)

# new start
在Test类中,有了读取binlog的能力,可以范围读取binlog