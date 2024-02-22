import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mysql {
    /**
     * debezium 太过于复杂,需要一个工具能够从指定的 binlog位置启动,可以自己控制提交offset
     * @param args
     */
    public static void main(String[] args) {
        // Define the configuration for the Debezium Engine with MySQL connector...
        final Properties props =new Properties();
        props.setProperty("name", "engine");
        // we specify Debezium’s MySqlConnector class.
        props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "/tmp/offsets6.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        /* begin connector properties */
        props.setProperty("database.hostname", "hostname");
        props.setProperty("database.port", "3306");
        props.setProperty("database.user", "username");
        props.setProperty("database.password", "password");
        //server-id 不要重复,以免
        props.setProperty("database.server.id", "85744");
        props.setProperty("database.server.name", "my-app-connector");
        props.setProperty("database.history",
                "io.debezium.relational.history.FileDatabaseHistory");
        props.setProperty("database.history.file.filename",
                "/path/to/storage/dbhistory6.dat");

        //不要做snapshot快照,报错没有schema
        // props.setProperty("snapshot.mode","never");
        //User does not have the 'LOCK TABLES' privilege required to obtain a consistent snapshot by preventing concurrent writes to tables.
        props.setProperty("snapshot.mode","schema_only");
        // Create the engine with this configuration ...
        props.setProperty("snapshot.locking.mode","none");
        //
        props.setProperty("database.include.list","scs_aios,scs_task");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
// Engine is stopped when the main code is finished
    }
}
