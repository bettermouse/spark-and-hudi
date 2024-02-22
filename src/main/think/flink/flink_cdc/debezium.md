# StreamingChangeEventSource
A change event source that emits events from a DB log, such as MySQL's binlog or similar.

## MySqlStreamingChangeEventSource
execute 开始执行
数据放入receiver中,receiver放入queue中
```
receiver.changeRecord(tableSchema, Operation.CREATE, newKey, envelope, getOffset(), headers);
```

监听数据在对应的监听器里面处理
```
    /**
     * Generate source records for the supplied event with an {@link WriteRowsEventData}.
     *
     * @param event the database change data event to be processed; may not be null
     * @throws InterruptedException if this thread is interrupted while blocking
     */
    protected void handleInsert(Event event) throws InterruptedException {
        handleChange(event, "insert", WriteRowsEventData.class, x -> taskContext.getSchema().getTableId(x.getTableId()), WriteRowsEventData::getRows,
                (tableId, row) -> eventDispatcher.dispatchDataChangeEvent(tableId, new MySqlChangeRecordEmitter(offsetContext, clock, Operation.CREATE, null, row)));
    }
```
### BinlogChangeEmitter<U> changeEmitter
(tableId, row) -> eventDispatcher.dispatchDataChangeEvent(tableId, new MySqlChangeRecordEmitter(offsetContext, clock, Operation.DELETE, row, null)));


## ChangeEventQueue
在mysql binlog和kafka(flink)之间交换数据

## EventDispatcher
Central dispatcher for data change and schema change events
DatabaseSchema
Queue
## ChangeRecordEmitter
source database 的一个变化,可以发送一个或多个记录(如 修改主键)

EventDispatcher 记录修改
```
    private final class StreamingChangeRecordReceiver implements ChangeRecordEmitter.Receiver {

        @Override
        public void changeRecord(DataCollectionSchema dataCollectionSchema,
                                 Operation operation,
                                 Object key, Struct value,
                                 OffsetContext offsetContext,
                                 ConnectHeaders headers)
                throws InterruptedException {

            Objects.requireNonNull(value, "value must not be null");

            LOGGER.trace("Received change record for {} operation on key {}", operation, key);

            // Truncate events must have null key schema as they are sent to table topics without keys
            Schema keySchema = (key == null && operation == Operation.TRUNCATE) ? null
                    : dataCollectionSchema.keySchema();
            String topicName = topicSelector.topicNameFor((T) dataCollectionSchema.id());

            SourceRecord record = new SourceRecord(offsetContext.getPartition(),
                    offsetContext.getOffset(), topicName, null,
                    keySchema, key,
                    dataCollectionSchema.getEnvelopeSchema().schema(),
                    value,
                    null,
                    headers);

            queue.enqueue(changeEventCreator.createDataChangeEvent(record));

            if (emitTombstonesOnDelete && operation == Operation.DELETE) {
                SourceRecord tombStone = record.newRecord(
                        record.topic(),
                        record.kafkaPartition(),
                        record.keySchema(),
                        record.key(),
                        null, // value schema
                        null, // value
                        record.timestamp(),
                        record.headers());

                queue.enqueue(changeEventCreator.createDataChangeEvent(tombStone));
            }
        }
    }
```
### 方法dispatchSchemaChangeEvent
<<<<<<< HEAD
EventDispatcherImpl 重写了EventDispatcher 中的dispatchSchemaChangeEvent


# embadding api
The format in which you want to receive the message, e.g. JSON, Avro or as Kafka Connect SourceRecord (see output message

SHOW DATABASES
查看数据库权限
"SHOW FULL TABLES IN " + quote(dbName) + " where Table_Type = 'BASE TABLE'"

## TopicSelector
Implementations return names for Kafka topics (data and meta-data).

## RelationalDatabaseConnectorConfig
配置数据库

## KafkaDatabaseHistory (DatabaseHistory)
DatabaseHistory 记录offset和history

## MySqlDatabaseSchema
MySQL database server 的历史schema.
change by applyDdl
loadHistory


```
仅加入schema中的表
public void recover(OffsetContext offset) {
    if (!databaseHistory.exists()) {
        String msg = "The db history topic or its content is fully or partially missing. Please check database history topic configuration and re-execute the snapshot.";
        throw new DebeziumException(msg);
    }
    databaseHistory.recover(offset.getPartition(), offset.getOffset(), tables(), getDdlParser());
    recoveredTables = !tableIds().isEmpty();
    for (TableId tableId : tableIds()) {
        buildAndRegisterSchema(tableFor(tableId));
    }
}

protected void buildAndRegisterSchema(Table table) {
    if (tableFilter.isIncluded(table.id())) {
        TableSchema schema = schemaBuilder.create(schemaPrefix, getEnvelopeSchemaName(table), table, columnFilter, columnMappers, customKeysMapper);
        schemasByTableId.put(table.id(), schema);
    }
}

//从 history中恢复
this.offsetContext =
        loadStartingOffsetState(new MySqlOffsetContext.Loader(connectorConfig), mySqlSplit);
validateAndLoadDatabaseHistory(offsetContext, databaseSchema);
```
通过 SHOW CREATE TABLE
### DatabaseHistory
实现 EmbeddedFlinkDatabaseHistory,MySqlDatabaseSchema 会从历史数据中恢复
### RelationalTableFilters

## Struct
org.apache.kafka.connect.data.Struct
名字和值的结构化记录

# 分析 消息如何转变
```
        if (!skippedOperations.contains(Operation.CREATE)) {
            eventHandlers.put(EventType.WRITE_ROWS, this::handleInsert);
            eventHandlers.put(EventType.EXT_WRITE_ROWS, this::handleInsert);
        }

        if (!skippedOperations.contains(Operation.UPDATE)) {
            eventHandlers.put(EventType.UPDATE_ROWS, this::handleUpdate);
            eventHandlers.put(EventType.EXT_UPDATE_ROWS, this::handleUpdate);
        }

        if (!skippedOperations.contains(Operation.DELETE)) {
            eventHandlers.put(EventType.DELETE_ROWS, this::handleDelete);
            eventHandlers.put(EventType.EXT_DELETE_ROWS, this::handleDelete);
        }
```

table number / database+tableName= tableId

schema 变更如何捕获
eventHandlers.put(EventType.QUERY, this::handleQueryEvent);

```
/**
 * Emits one or more change records - specific to a given {@link DataCollectionSchema}.
 *
 * @author Gunnar Morling
 */
public interface SchemaChangeEventEmitter {

    void emitSchemaChangeEvent(Receiver receiver) throws InterruptedException;

    public interface Receiver {
        void schemaChangeEvent(SchemaChangeEvent event) throws InterruptedException;
    }
}

```
接口+内部类 可以做到参数传递+逻辑封装

## schema 修改
传回来的只有sql语句
ConnectTableChangeSerializer,由于只能传递序列化后的table change?
增加了代码的复杂度?

## 增加  insert
SourceRecord source

```
protected void handleInsert(Event event) throws InterruptedException {
    handleChange(event, "insert", WriteRowsEventData.class, x -> taskContext.getSchema().getTableId(x.getTableId()), WriteRowsEventData::getRows,
            (tableId, row) -> eventDispatcher.dispatchDataChangeEvent(tableId, new MySqlChangeRecordEmitter(offsetContext, clock, Operation.CREATE, null, row)));
}
TableIdProvider<T> tableIdProvider,
RowsProvider<T, U> rowsProvider,
BinlogChangeEmitter<U> changeEmitter


@FunctionalInterface
private static interface BinlogChangeEmitter<T> {
    void emit(TableId tableId, T data) throws InterruptedException;
}

keySchema primary key
keyValue 主键值

封装 envolop
 "op" -> {Field@12245} "Field{name=op, index=3, schema=Schema{STRING}}"
 "after" -> {Field@12247} "Field{name=after, index=1, schema=Schema{mysql_binlog_source.scs_aios.student1.Value:STRUCT}}"
 "source" -> {Field@12249} "Field{name=source, index=2, schema=Schema{io.debezium.connector.mysql.Source:STRUCT}}"
 "before" -> {Field@12251} "Field{name=before, index=0, schema=Schema{mysql_binlog_source.scs_aios.student1.Value:STRUCT}}"
 "ts_ms" -> {Field@12253} "Field{name=ts_ms, index=4, schema=Schema{INT64}}"
 "transaction" -> {Field@12255} "Field{name=transaction, index=5, schema=Schema{STRUCT}}"

schema 帮忙生成序列化器
op/before/after 

insert before 为null

io.debezium.data.Envelope.FieldName
里面有所有的key
```

## 修改
### 主键修改
io.debezium.data.Envelope.Operation.UPDATE
// PK update -> emit as delete and re-insert with new key
如果是主键修改,发送两次数据,先删除,再创建
```
else {
    ConnectHeaders headers = new ConnectHeaders();
    headers.add(PK_UPDATE_NEWKEY_FIELD, newKey, tableSchema.keySchema());

    Struct envelope = tableSchema.getEnvelopeSchema().delete(oldValue, getOffset().getSourceInfo(), getClock().currentTimeAsInstant());
    receiver.changeRecord(tableSchema, Operation.DELETE, oldKey, envelope, getOffset(), headers);

    headers = new ConnectHeaders();
    headers.add(PK_UPDATE_OLDKEY_FIELD, oldKey, tableSchema.keySchema());

    envelope = tableSchema.getEnvelopeSchema().create(newValue, getOffset().getSourceInfo(), getClock().currentTimeAsInstant());
    receiver.changeRecord(tableSchema, Operation.CREATE, newKey, envelope, getOffset(), headers);
}
```
io.debezium.connector.mysql.legacy.SourceInfo
io.debezium.connector.AbstractSourceInfo

source 的枚举值
=======
EventDispatcherImpl 重写了EventDispatcher 中的dispatchSchemaChangeEvent
>>>>>>> parent of 067ca1b (add)
