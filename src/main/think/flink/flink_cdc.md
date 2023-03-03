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