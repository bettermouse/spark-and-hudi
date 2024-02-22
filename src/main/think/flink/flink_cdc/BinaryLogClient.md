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

## flink用了debezium ->debezium 用了 
io.debezium.connector.mysql.MySqlStreamingChangeEventSource#MySqlStreamingChangeEventSource
### 增加序列化
```
       // id 到table的映射
       // Set up the event deserializer with additional type(s) ...
        final Map<Long, TableMapEventData> tableMapEventByTableId = new HashMap<Long, TableMapEventData>();
        EventDeserializer eventDeserializer = new EventDeserializer() {
            @Override
            public Event nextEvent(ByteArrayInputStream inputStream) throws IOException {
                try {
                    // Delegate to the superclass ...
                    Event event = super.nextEvent(inputStream);

                    // We have to record the most recent TableMapEventData for each table number for our custom deserializers ...
                    if (event.getHeader().getEventType() == EventType.TABLE_MAP) {
                        TableMapEventData tableMapEvent = event.getData();
                        tableMapEventByTableId.put(tableMapEvent.getTableId(), tableMapEvent);
                    }
                    return event;
                }
                // DBZ-217 In case an event couldn't be read1 we create a pseudo-event for the sake of logging
                catch (EventDataDeserializationException edde) {
                    // DBZ-3095 As of Java 15, when reaching EOF in the binlog stream, the polling loop in
                    // BinaryLogClient#listenForEventPackets() keeps returning values != -1 from peek();
                    // this causes the loop to never finish
                    // Propagating the exception (either EOF or socket closed) causes the loop to be aborted
                    // in this case
                    if (edde.getCause() instanceof IOException) {
                        throw edde;
                    }

                    EventHeaderV4 header = new EventHeaderV4();
                    header.setEventType(EventType.INCIDENT);
                    header.setTimestamp(edde.getEventHeader().getTimestamp());
                    header.setServerId(edde.getEventHeader().getServerId());

                    if (edde.getEventHeader() instanceof EventHeaderV4) {
                        header.setEventLength(((EventHeaderV4) edde.getEventHeader()).getEventLength());
                        header.setNextPosition(((EventHeaderV4) edde.getEventHeader()).getNextPosition());
                        header.setFlags(((EventHeaderV4) edde.getEventHeader()).getFlags());
                    }

                    EventData data = new EventDataDeserializationExceptionData(edde);
                    return new Event(header, data);
                }
            }
        };
```
### 增加 监听器
```
        client.registerEventListener(connectorConfig.bufferSizeForStreamingChangeEventSource() == 0
                ? this::handleEvent
                : (new EventBuffer(connectorConfig.bufferSizeForStreamingChangeEventSource(), this, context))::add);

        client.registerLifecycleListener(new ReaderThreadLifecycleListener());
        client.registerEventListener(this::onEvent);
        if (LOGGER.isDebugEnabled()) {
            client.registerEventListener(this::logEvent);
        }
__
 // MySQL has seconds resolution but mysql-binlog-connector-java returns
 // a value in milliseconds
 long eventTs = event.getHeader().getTimestamp();
```

对于更新可以获取before after的值,但没有schema
头部有tableId