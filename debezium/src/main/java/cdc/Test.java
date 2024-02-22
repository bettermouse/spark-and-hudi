package cdc;

import com.alibaba.fastjson.JSON;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import io.debezium.connector.base.ChangeEventQueue;
import io.debezium.connector.mysql.MySqlConnection;
import io.debezium.connector.mysql.MySqlStreamingChangeEventSourceMetrics;
import io.debezium.pipeline.DataChangeEvent;
import io.debezium.pipeline.source.spi.ChangeEventSource;
import io.debezium.relational.Table;
import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cdc.BinlogOffset.NO_STOPPING_OFFSET;
import static cdc.DebeziumUtils.createBinaryClient;


public class Test {
    public static void main(String[] args) throws InterruptedException {
        // 构建配置
        ArrayList<String> list = new ArrayList<>();
        list.add("scs_aios");
        ArrayList<String> tableList = new ArrayList<>();
        tableList.add("scs_aios.student1");
        MySqlSourceConfigFactory mySqlSourceConfigFactory = new MySqlSourceConfigFactory();
        MySqlSourceConfigFactory factory = mySqlSourceConfigFactory
                .setHostname("scsdbci-m.dbsit.sfcloud.local")
                .setUsername("bincanal")
                .setPassword("1aasf2bb3cc!")
                .setDatabaseList(list)
                .setTableList(tableList);

        MySqlSourceConfig config = factory.createConfig(0);
        MySqlConnection mySqlConnection = DebeziumUtils.createMySqlConnection(config.getDbzConfiguration());
        //step 1 获取当前配置
        BinlogOffset binlogOffset = DebeziumUtils.currentBinlogOffset(mySqlConnection);
        System.out.println(binlogOffset);
        //step 2 从指定binlog位置 读取binlog
        final BinaryLogClient binaryLogClient =
                createBinaryClient(config.getDbzConfiguration());
        final StatefulTaskContext statefulTaskContext =
                new StatefulTaskContext(config, binaryLogClient, mySqlConnection);


        //设置tableSchema
        Map<TableId, TableChanges.TableChange> tableSchemas
                = TableDiscoveryUtils.discoverCapturedTableSchemas(config, mySqlConnection);
//        Table table = tableSchemas.get("").getTable();
        MySqlBinlogSplit mySqlSplit = new MySqlBinlogSplit(binlogOffset,NO_STOPPING_OFFSET,tableSchemas);

        statefulTaskContext.configure(mySqlSplit);
        MySqlBinlogSplitReadTask binlogSplitReadTask =
                new MySqlBinlogSplitReadTask(
                        statefulTaskContext.getConnectorConfig(),
                        statefulTaskContext.getOffsetContext(),//mySqlOffsetContext
                        statefulTaskContext.getConnection(),
                        statefulTaskContext.getDispatcher(),
                        statefulTaskContext.getErrorHandler(),
                        StatefulTaskContext.getClock(),
                        statefulTaskContext.getTaskContext(),
                        (MySqlStreamingChangeEventSourceMetrics)
                                statefulTaskContext.getStreamingChangeEventSourceMetrics(),
                        statefulTaskContext.getTopicSelector().getPrimaryTopic(),
                        mySqlSplit
                );

        //从队列中实时获取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChangeEventQueue<DataChangeEvent> queue = statefulTaskContext.getQueue();
                DataProcess  dataProcess= new DataProcess();
                while (true){
                    try {
                        List<DataChangeEvent> list = queue.poll();
                        for(DataChangeEvent x:list){
                            SourceRecord record = x.getRecord();
                            dataProcess.processEvent(record);
                            System.out.println(record.value());
                            System.out.println(JSON.toJSONString(record.value()));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        //    currentReader = new BinlogSplitReader(statefulTaskContext, subtaskId);
        binlogSplitReadTask.execute(new ChangeEventSource.ChangeEventSourceContext(){
            @Override
            public boolean isRunning() {
                return true;
            }
        });

        System.out.println(1);
    }
}
