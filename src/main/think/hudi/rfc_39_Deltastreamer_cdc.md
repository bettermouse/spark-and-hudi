## mysql precombine field
  private static String generateUniqueSequence(String fileId, Long pos) {
    return fileId.substring(fileId.lastIndexOf('.') + 1).concat("." + pos);
  }
### binlog 文件名+pos   是选用的方式
https://blog.csdn.net/Huangjiazhen711/article/details/127596370
### Xid 事务id
不一定是有序列的,后来的事务先提交
https://blog.51cto.com/u_13874232/5457530
MySQL 内部维护了一个全局变量 global_query_id，每次执行语句的时候将它赋值给 Query_id，
然后给这个变量加 1。如果当前语句是这个事务执行的第一条语句，那么 MySQL 还会同时把 Query_id 赋值给这个事务的 Xid。
查询语句没有事务id

但是 MySQL 重启之后会重新生成新的 binlog 文件，这就保证了，同一个 binlog 文件里，Xid 一定是惟一的
###  gid
https://www.jianshu.com/p/5f9b52a8486d
show variables like '%server_uuid%'; 
全局唯一事务号,没什么可说的
UUID+TID

在传统的mysql基于二进制日志的模式复制中，从库需要告知主库 要从哪个二进制日志文件中的那个偏移量进行增量同步，如果指定错误会造成数据的遗漏，从而造成数据的不一致
https://www.modb.pro/db/424397

查看 gtid 是否开启
select@@gtid_mode;
获取执行过的
select @@global.gtid_executed;
https://blog.csdn.net/sinat_36757755/article/details/124049382
 find / -name auto.cnf 
 
  cat /etc/my.cnf
```
gtid_mode=ON
enforce_gtid_consistency=ON
server_id=2
log_bin=mysql-bin
binlog_format=ROW
```

mysql 配置主从同步
https://blog.csdn.net/weixin_44270742/article/details/128188790

stop slave;
start slave;
change master to master_host='node1', master_port=3306,master_user='root', master_password='root',master_auto_position=1
 show slave status \G;
  mysqldump -uroot -proot --master-data  --all-databases  >a.sql
  
  
RESET MASTER和RESET SLAVE使用场景和说明
https://blog.csdn.net/yabingshi_tech/article/details/50736735

## code
org.apache.spark.streaming.kafka010.KafkaTestUtils
对于scala 变参的代码可能更难去调试
```
    zkClient = KafkaZkClient(s"$zkHost:$zkPort", isSecure = false, zkSessionTimeout,
      zkConnectionTimeout, 1, new SystemTime())
```

```
    <dependency>
      <groupId>org.apache.zookeeper</groupId>
      <artifactId>zookeeper</artifactId>
      <version>3.5.7</version>
      <scope>test</scope>
    </dependency>

<kafka.spark3.version>2.8.1</kafka.spark3.version>
 
```

## 测试新思路
通过构造本地kafka来测试.有意思 
```
public abstract class TestAbstractDebeziumSource extends UtilitiesTestBase {

  private final String testTopicName = "hoodie_test_" + UUID.randomUUID();

  private final HoodieIngestionMetrics metrics = mock(HoodieIngestionMetrics.class);
  private static KafkaTestUtils testUtils;
```

## 数据流的转换
1.string->avro
2.avro->row
3.row -> row
转成需要的格式
### 具体执行
```
//Debezium kafka payload有一个嵌套的类型
这个函数扁平这个嵌套的结构,抽取元数据

  /**
   * Debezium Kafka Payload has a nested structure (see https://debezium.io/documentation/reference/1.4/connectors/mysql.html).
   * This function flattens this nested structure for the Mysql data, and also extracts a subset of Debezium metadata fields.
   *
   * @param rowDataset Dataset containing Debezium Payloads
   * @return New dataset with flattened columns
   */
  @Override
  protected Dataset<Row> processDataset(Dataset<Row> rowDataset) {
    Dataset<Row> flattenedDataset = rowDataset;
    if (rowDataset.columns().length > 0) {
      // Only flatten for non-empty schemas
      Dataset<Row> insertedOrUpdatedData = rowDataset
          .selectExpr(
              String.format("%s as %s", DebeziumConstants.INCOMING_OP_FIELD, DebeziumConstants.FLATTENED_OP_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_TS_MS_FIELD, DebeziumConstants.UPSTREAM_PROCESSING_TS_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_NAME_FIELD, DebeziumConstants.FLATTENED_SHARD_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_TS_MS_FIELD, DebeziumConstants.FLATTENED_TS_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_FILE_FIELD, DebeziumConstants.FLATTENED_FILE_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_POS_FIELD, DebeziumConstants.FLATTENED_POS_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_ROW_FIELD, DebeziumConstants.FLATTENED_ROW_COL_NAME),
              String.format("%s.*", DebeziumConstants.INCOMING_AFTER_FIELD)
          )
          .filter(rowDataset.col(DebeziumConstants.INCOMING_OP_FIELD).notEqual(DebeziumConstants.DELETE_OP));

      Dataset<Row> deletedData = rowDataset
          .selectExpr(
              String.format("%s as %s", DebeziumConstants.INCOMING_OP_FIELD, DebeziumConstants.FLATTENED_OP_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_TS_MS_FIELD, DebeziumConstants.UPSTREAM_PROCESSING_TS_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_NAME_FIELD, DebeziumConstants.FLATTENED_SHARD_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_TS_MS_FIELD, DebeziumConstants.FLATTENED_TS_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_FILE_FIELD, DebeziumConstants.FLATTENED_FILE_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_POS_FIELD, DebeziumConstants.FLATTENED_POS_COL_NAME),
              String.format("%s as %s", DebeziumConstants.INCOMING_SOURCE_ROW_FIELD, DebeziumConstants.FLATTENED_ROW_COL_NAME),
              String.format("%s.*", DebeziumConstants.INCOMING_BEFORE_FIELD)
          )
          .filter(rowDataset.col(DebeziumConstants.INCOMING_OP_FIELD).equalTo(DebeziumConstants.DELETE_OP));

      flattenedDataset = insertedOrUpdatedData.union(deletedData);
    }

    return flattenedDataset.withColumn(DebeziumConstants.ADDED_SEQ_COL_NAME,
            callUDF(generateUniqueSeqUdfFn, flattenedDataset.col(DebeziumConstants.FLATTENED_FILE_COL_NAME),
                flattenedDataset.col(DebeziumConstants.FLATTENED_POS_COL_NAME)));
  }
```

4. 不知道的转换
```
    // Some required transformations to ensure debezium data types are converted to spark supported types.
    return convertArrayColumnsToString(convertColumnToNullable(sparkSession,
        convertDateColumns(debeziumDataset, new Schema.Parser().parse(schemaStr))));
```
##### common
https://ververica.github.io/flink-cdc-connectors/master/content/about.html#deserialization

ts_ms	连接器处理时间
Optional field that displays the time at which the connector processed the event. The time is based on the system clock in the JVM running the Kafka Connect task.

source.name
binlog name where the event was recorded

source.ts_ms
Timestamp for when the change was made in the database

op _change_operation_type
ts_ms _upstream_event_processed_ts_ms 
source.name db_shard_source_partition
source.ts_ms _event_origin_ts_ms
source.file _event_bin_file
source.pos _event_pos
source.row _event_row (Row within the event)

_event_seq 通过binlog+pos生成.
gtid_mode = on
enforce_gtid_consistency = on
log-slave-updates = 1

###### insert
 .filter(rowDataset.col(DebeziumConstants.INCOMING_OP_FIELD).notEqual(DebeziumConstants.DELETE_OP));
after.*
###### delete
before.*
### AvroConvertor
Convert a variety of datum into Avro GenericRecords. Has a bunch of lazy fields to circumvent issues around serializing these objects from driver to executors
### AvroConversionUtils
org.apache.hudi.AvroConversionUtils.createDataFrame
转成row 格式

```
  def createDataFrame(rdd: RDD[GenericRecord], schemaStr: String, ss: SparkSession): Dataset[Row] = {
    if (rdd.isEmpty()) {
      ss.emptyDataFrame
    } else {
      ss.createDataFrame(rdd.mapPartitions { records =>
        if (records.isEmpty) Iterator.empty
        else {
          val schema = new Schema.Parser().parse(schemaStr)
          val dataType = convertAvroSchemaToStructType(schema)
          val converter = createConverterToRow(schema, dataType)
          records.map { r => converter(r) }
        }
      }, convertAvroSchemaToStructType(new Schema.Parser().parse(schemaStr)))
    }
  }

```
# 主要类
## DebeziumSource
source 部分仅生成了数据源
## DeltaSync
Sync's one batch of data to hoodie table.****