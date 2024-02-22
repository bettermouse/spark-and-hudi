import cdc.*;
import hudi.SchemaUtil;
import hudi.SparkTableUtil;
import io.debezium.connector.base.ChangeEventQueue;
import io.debezium.connector.mysql.MySqlConnection;
import io.debezium.pipeline.DataChangeEvent;
import io.debezium.relational.Table;
import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges;
import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.WriteHudiTable;

import java.util.ArrayList;
import java.util.Map;

public class TestCreateTable {

    private ChangeEventQueue<DataChangeEvent> queue;

    private static final Logger LOG = LoggerFactory.getLogger(TestCreateTable.class);

    public static void main(String[] args) {
        // 构建配置
        ArrayList<String> list = new ArrayList<>();
        list.add("scs_aios");
        ArrayList<String> tableList = new ArrayList<>();
        tableList.add("scs_aios.t_pass_no_send_detail_log");
        MySqlSourceConfigFactory mySqlSourceConfigFactory = new MySqlSourceConfigFactory();
        MySqlSourceConfigFactory factory = mySqlSourceConfigFactory
                .setHostname("scsdbci-m.dbsit.sfcloud.local")
                .setUsername("bincanal")
                .setPassword("1aasf2bb3cc!")
                .setDatabaseList(list)
                .setTableList(tableList)
                ;

        MySqlSourceConfig config = factory.createConfig(0);
        MySqlConnection mySqlConnection = DebeziumUtils.createMySqlConnection(config.getDbzConfiguration());
        BinlogOffset initBinlogOffset = DebeziumUtils.currentBinlogOffset(mySqlConnection);

        //设置tableSchema
        Map<TableId, TableChanges.TableChange> tableSchemas
                = TableDiscoveryUtils.discoverCapturedTableSchemas(config, mySqlConnection);
        for(Map.Entry<TableId, TableChanges.TableChange> entry: tableSchemas.entrySet()){
            TableChanges.TableChange value = entry.getValue();
            TableId tableId = value.getId();
            String database = tableId.catalog();
            String tableName = tableId.table();
            Table table = value.getTable();
            try {
                if(table.primaryKeyColumnNames().size() > 0 && table.columnWithName("modify_time")!=null
                && table.columnWithName("create_time")!=null){
                        Schema avroSchemaByMysql = SchemaUtil.createAvroSchemaByMysql(table, mySqlConnection);
                        SparkTableUtil.createTable(database,tableName,table,avroSchemaByMysql);
                    tableList.add(String.format("%s.%s",database,tableName));
                }else{
                    LOG.info("table %s does not have primary key");
                }

            } catch (SchemaParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                    e.printStackTrace();
            }
        }
        factory.setTableList(tableList);
        config = factory.createConfig(0);
        //init all tables
        WriteHudiTable writeHudiTable = new WriteHudiTable(tableSchemas, config);
        writeHudiTable.loadAllTable(initBinlogOffset);
    }



}
