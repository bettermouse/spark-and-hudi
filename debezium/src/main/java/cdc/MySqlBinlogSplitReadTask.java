package cdc;

import com.github.shyiko.mysql.binlog.event.Event;
import io.debezium.connector.mysql.MySqlConnection;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.connector.mysql.MySqlOffsetContext;
import io.debezium.connector.mysql.MySqlStreamingChangeEventSource;
import io.debezium.connector.mysql.MySqlStreamingChangeEventSourceMetrics;
import io.debezium.connector.mysql.MySqlTaskContext;
import io.debezium.pipeline.ErrorHandler;
import io.debezium.relational.TableId;
import io.debezium.util.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cdc.BinlogOffset.NO_STOPPING_OFFSET;

/**
 * Task to read all binlog for table and also supports read bounded (from lowWatermark to
 * highWatermark) binlog.
 *
 * logical
 *        eventHandlers.put(EventType.STOP, this::handleServerStop);
 *         eventHandlers.put(EventType.HEARTBEAT, this::handleServerHeartbeat);
 *         eventHandlers.put(EventType.INCIDENT, this::handleServerIncident);
 *         eventHandlers.put(EventType.ROTATE, this::handleRotateLogsEvent);
 *         eventHandlers.put(EventType.TABLE_MAP, this::handleUpdateTableMetadata);
 *         eventHandlers.put(EventType.QUERY, this::handleQueryEvent);
 * register many listener
 *
 * data add queue
 * 1.schema
 * 2.filter
 *
 */
public class MySqlBinlogSplitReadTask extends MySqlStreamingChangeEventSource {

    private static final Logger LOG = LoggerFactory.getLogger(MySqlBinlogSplitReadTask.class);
    private final MySqlBinlogSplit binlogSplit;
    private final MySqlOffsetContext offsetContext;
    private final EventDispatcherImpl<TableId> eventDispatcher;
    //private final SignalEventDispatcher signalEventDispatcher;
    private final ErrorHandler errorHandler;
    private ChangeEventSourceContext context;

    public MySqlBinlogSplitReadTask(
            MySqlConnectorConfig connectorConfig,
            MySqlOffsetContext offsetContext,
            MySqlConnection connection,
            EventDispatcherImpl<TableId> dispatcher,
            ErrorHandler errorHandler,
            Clock clock,
            MySqlTaskContext taskContext,
            MySqlStreamingChangeEventSourceMetrics metrics,
            String topic,
            MySqlBinlogSplit binlogSplit) {
        super(
                connectorConfig,
                offsetContext,
                connection,
                dispatcher,
                errorHandler,
                clock,
                taskContext,
                metrics);
        this.binlogSplit = binlogSplit;
        this.eventDispatcher = dispatcher;
        this.offsetContext = offsetContext;
        this.errorHandler = errorHandler;
//        this.signalEventDispatcher =
//                new SignalEventDispatcher(
//                        offsetContext.getPartition(), topic, eventDispatcher.getQueue());
    }

    @Override
    public void execute(ChangeEventSourceContext context) throws InterruptedException {
        this.context = context;
        super.execute(context);
    }

    @Override
    protected void handleEvent(Event event) {
        super.handleEvent(event);
//        // check do we need to stop for read binlog for snapshot split.
//        if (isBoundedRead()) {
//            final BinlogOffset currentBinlogOffset = getBinlogPosition(offsetContext.getOffset());
//            // reach the high watermark, the binlog reader should finished
//            if (currentBinlogOffset.isAtOrAfter(binlogSplit.getEndingOffset())) {
//                // send binlog end event
//                try {
//                    signalEventDispatcher.dispatchWatermarkEvent(
//                            binlogSplit,
//                            currentBinlogOffset,
//                            SignalEventDispatcher.WatermarkKind.BINLOG_END);
//                } catch (InterruptedException e) {
//                    LOG.error("Send signal event error.", e);
//                    errorHandler.setProducerThrowable(
//                            new DebeziumException("Error processing binlog signal event", e));
//                }
//                // tell reader the binlog task finished
//                ((SnapshotBinlogSplitChangeEventSourceContextImpl) context).finished();
//            }
//        }
    }

//    private boolean isBoundedRead() {
//        return !NO_STOPPING_OFFSET.equals(binlogSplit.getEndingOffset());
//    }
}
