package cdc;




import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;


/** A factory to construct {@link MySqlSourceConfig}. */
public class MySqlSourceConfigFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private int port = 3306; // default 3306 port
    private String hostname;
    private String username;
    private String password;
    private List<String> databaseList;
    private List<String> tableList;



    /** Creates a new {@link MySqlSourceConfig} for the given subtask {@code subtaskId}. */
    public MySqlSourceConfig createConfig(int subtaskId) {
        Properties props = new Properties();

        // hard code server name, because we don't need to distinguish it, docs:
        // Logical name that identifies and provides a namespace for the particular
        // MySQL database server/cluster being monitored. The logical name should be
        // unique across all other connectors, since it is used as a prefix for all
        // Kafka topic names emanating from this connector.
        // Only alphanumeric characters and underscores should be used.
        // Kafka topic的名称
        props.setProperty("database.server.name", "mysql_binlog_source");
        props.setProperty("database.hostname", checkNotNull(hostname));
        props.setProperty("database.user", checkNotNull(username));
        props.setProperty("database.password", checkNotNull(password));
        props.setProperty("database.port", String.valueOf(port));
      //  props.setProperty("database.fetchSize", String.valueOf(fetchSize));
        props.setProperty("database.responseBuffering", "adaptive");
     //   props.setProperty("database.serverTimezone", serverTimeZone);



        // database history
        props.setProperty(
                "database.history", EmbeddedFlinkDatabaseHistory.class.getCanonicalName());
        props.setProperty(
                "database.history.instance.name", UUID.randomUUID().toString() + "_" + subtaskId);
        props.setProperty("database.history.skip.unparseable.ddl", String.valueOf(true));
        props.setProperty("database.history.refer.ddl", String.valueOf(true));

       // props.setProperty("connect.timeout.ms", String.valueOf(connectTimeout.toMillis()));
        // the underlying debezium reader should always capture the schema changes and forward them.
        // Note: the includeSchemaChanges parameter is used to control emitting the schema record,
        // only DataStream API program need to emit the schema record, the Table API need not
        props.setProperty("include.schema.changes", String.valueOf(true));
        // disable the offset flush totally
        props.setProperty("offset.flush.interval.ms", String.valueOf(Long.MAX_VALUE));
        // disable tombstones
        props.setProperty("tombstones.on.delete", String.valueOf(false));
        //props.setProperty("heartbeat.interval.ms", String.valueOf(heartbeatInterval.toMillis()));
        // debezium use "long" mode to handle unsigned bigint by default,
        // but it'll cause lose of precise when the value is larger than 2^63,
        // so use "precise" mode to avoid it.
        props.put("bigint.unsigned.handling.mode", "precise");

        if (databaseList != null) {
            props.setProperty("database.include.list", String.join(",", databaseList));
        }
        if (tableList != null) {
            props.setProperty("table.include.list", String.join(",", tableList));
        }

        return new MySqlSourceConfig(
                hostname,
                port,
                username,
                password,
                databaseList,
                tableList,
                props
              );
    }

    public MySqlSourceConfigFactory setPort(int port) {
        this.port = port;
        return this;
    }

    public MySqlSourceConfigFactory setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public MySqlSourceConfigFactory setUsername(String username) {
        this.username = username;
        return this;
    }

    public MySqlSourceConfigFactory setPassword(String password) {
        this.password = password;
        return this;
    }

    public MySqlSourceConfigFactory setDatabaseList(List<String> databaseList) {
        this.databaseList = databaseList;
        return this;
    }

    public MySqlSourceConfigFactory setTableList(List<String> tableList) {
        this.tableList = tableList;
        return this;
    }


}
