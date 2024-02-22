package cdc;

import io.debezium.DebeziumException;
import io.debezium.connector.base.ChangeEventQueue;
import io.debezium.connector.mysql.MySqlConnector;
import io.debezium.connector.mysql.MySqlTaskContext;
import io.debezium.pipeline.ErrorHandler;
import io.debezium.relational.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A subclass implementation of {@link ErrorHandler} which filter some {@link DebeziumException}, we
 * use this class instead of {@link io.debezium.connector.mysql.MySqlErrorHandler}.
 */
public class MySqlErrorHandler extends ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MySqlErrorHandler.class);
    private static final Pattern NOT_FOUND_TABLE_MSG_PATTERN =
            Pattern.compile(
                    "Encountered change event for table (.+)\\.(.+) whose schema isn't known to this connector");

    MySqlTaskContext context;

    public MySqlErrorHandler(
            String logicalName, ChangeEventQueue<?> queue, MySqlTaskContext context) {
        super(MySqlConnector.class, logicalName, queue);
        this.context = context;
    }

    @Override
    protected boolean isRetriable(Throwable throwable) {
        return false;
    }

    @Override
    public void setProducerThrowable(Throwable producerThrowable) {
        if (producerThrowable.getCause() instanceof DebeziumException) {
            DebeziumException e = (DebeziumException) producerThrowable.getCause();
            String detailMessage = e.getMessage();
            Matcher matcher = NOT_FOUND_TABLE_MSG_PATTERN.matcher(detailMessage);
            if (matcher.find()) {
                String databaseName = matcher.group(1);
                String tableName = matcher.group(2);
                TableId tableId = new TableId(databaseName, null, tableName);
                if (context.getSchema().schemaFor(tableId) == null) {
                    LOG.warn("Schema for table " + tableId + " is null");
                    return;
                }
            }
        }
        super.setProducerThrowable(producerThrowable);
    }
}