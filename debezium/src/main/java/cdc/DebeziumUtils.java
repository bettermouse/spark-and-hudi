package cdc;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import io.debezium.config.Configuration;
import io.debezium.connector.mysql.*;
import io.debezium.jdbc.JdbcConnection;
import io.debezium.jdbc.JdbcValueConverters;
import io.debezium.jdbc.TemporalPrecisionMode;
import io.debezium.relational.TableId;
import io.debezium.schema.TopicSelector;
import io.debezium.util.SchemaNameAdjuster;

import java.sql.SQLException;

public class DebeziumUtils {

    /** Creates a new {@link MySqlConnection}, but not open the connection. */
    public static MySqlConnection createMySqlConnection(Configuration dbzConfiguration) {
        return new MySqlConnection(
                new MySqlConnection.MySqlConnectionConfiguration(dbzConfiguration));
    }

    /** Creates a new {@link BinaryLogClient} for consuming mysql binlog. */
    public static BinaryLogClient createBinaryClient(Configuration dbzConfiguration) {
        final MySqlConnectorConfig connectorConfig = new MySqlConnectorConfig(dbzConfiguration);
        return new BinaryLogClient(
                connectorConfig.hostname(),
                connectorConfig.port(),
                connectorConfig.username(),
                connectorConfig.password());
    }

    /** Fetch current binlog offsets in MySql Server. */
    public static BinlogOffset currentBinlogOffset(JdbcConnection jdbc) {
        final String showMasterStmt = "SHOW MASTER STATUS";
        try {
            return jdbc.queryAndMap(
                    showMasterStmt,
                    rs -> {
                        if (rs.next()) {
                            final String binlogFilename = rs.getString(1);
                            final long binlogPosition = rs.getLong(2);
                            final String gtidSet =
                                    rs.getMetaData().getColumnCount() > 4 ? rs.getString(5) : null;
                            return new BinlogOffset(
                                    binlogFilename, binlogPosition, 0L, 0, 0, gtidSet, null);
                        } else {
                            throw new RuntimeException(
                                    "Cannot read the binlog filename and position via '"
                                            + showMasterStmt
                                            + "'. Make sure your server is correctly configured");
                        }
                    });
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Cannot read the binlog filename and position via '"
                            + showMasterStmt
                            + "'. Make sure your server is correctly configured",
                    e);
        }
    }


    /** Creates a new {@link MySqlDatabaseSchema} to monitor the latest MySql database schemas. */
    public static MySqlDatabaseSchema createMySqlDatabaseSchema(
            MySqlConnectorConfig dbzMySqlConfig, boolean isTableIdCaseSensitive) {
        TopicSelector<TableId> topicSelector = MySqlTopicSelector.defaultSelector(dbzMySqlConfig);
        SchemaNameAdjuster schemaNameAdjuster = SchemaNameAdjuster.create();
        MySqlValueConverters valueConverters = getValueConverters(dbzMySqlConfig);
        return new MySqlDatabaseSchema(
                dbzMySqlConfig,
                valueConverters,
                topicSelector,
                schemaNameAdjuster,
                isTableIdCaseSensitive);
    }

    private static MySqlValueConverters getValueConverters(MySqlConnectorConfig dbzMySqlConfig) {
        TemporalPrecisionMode timePrecisionMode = dbzMySqlConfig.getTemporalPrecisionMode();
        JdbcValueConverters.DecimalMode decimalMode = dbzMySqlConfig.getDecimalMode();
        String bigIntUnsignedHandlingModeStr =
                dbzMySqlConfig
                        .getConfig()
                        .getString(MySqlConnectorConfig.BIGINT_UNSIGNED_HANDLING_MODE);
        MySqlConnectorConfig.BigIntUnsignedHandlingMode bigIntUnsignedHandlingMode =
                MySqlConnectorConfig.BigIntUnsignedHandlingMode.parse(
                        bigIntUnsignedHandlingModeStr);
        JdbcValueConverters.BigIntUnsignedMode bigIntUnsignedMode =
                bigIntUnsignedHandlingMode.asBigIntUnsignedMode();

        boolean timeAdjusterEnabled =
                dbzMySqlConfig.getConfig().getBoolean(MySqlConnectorConfig.ENABLE_TIME_ADJUSTER);
        return new MySqlValueConverters(
                decimalMode,
                timePrecisionMode,
                bigIntUnsignedMode,
                dbzMySqlConfig.binaryHandlingMode(),
                timeAdjusterEnabled ? MySqlValueConverters::adjustTemporal : x -> x,
                MySqlValueConverters::defaultParsingErrorHandler);
    }
}
