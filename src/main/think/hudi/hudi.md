# maven插件 方便单元测试
maven heaper

# 引入bundle包,无法debug
https://www.jianshu.com/p/d5de94e916ae
还未解决

https://stackoverflow.com/questions/55309684/does-maven-shade-plugin-work-with-scala-classes
这个似乎是idea的Bug

# maven执行一个main函数
mvn -Dspark3.2 -Dscala-2.12  exec:java -Dexec.mainClass="org.apache.hudi.spark.bundle.Main" -pl packaging/hudi-spark-bund
le

插件中这个方法可以进入 
exec:java -Dexec.mainClass="org.apache.hudi.spark.bundle.Main"


# 类与测试
在不知道要干什么的时候,需要努力的向前.
## hudi-spark-datasource#hudi-spark
### SchemaConverters
org.apache.spark.sql.avro.SchemaConverters
从spark中拷贝出来的,在avro和spark schema中互相转换
可以为null在spark中nullable =true,avro中为UNION 里面有NUll.
#### TestSchemaConverters

### hudi bootstrap 
bootstrap是指将原本的表变为hudi表  
org.apache.hudi.functional.TestBootstrap  
bootstrap的类型有 FULL_RECORD/METADATA_ONLY  
这两种的区别是 hudi skeleton
org.apache.hudi.table.action.bootstrap.SparkBootstrapCommitActionExecutor.metadataBootstrap
```
 updateIndexAndCommitIfNeeded(bootstrapWriteStatuses.map(w -> w), result);
```

#### 测试数据的生成
rowKey 从1~10生成
FLATTENED_AVRO_SCHEMA
```
{
    "type":"record",
    "name":"triprec",
    "fields":[
        {
            "name":"timestamp",
            "type":"long"
        },
        {
            "name":"_row_key",
            "type":"string"
        },
        {
            "name":"partition_path",
            "type":[
                "null",
                "string"
            ],
            "default":null
        },
        {
            "name":"rider",
            "type":"string"
        },
        {
            "name":"driver",
            "type":"string"
        },
        {
            "name":"begin_lat",
            "type":"double"
        },
        {
            "name":"begin_lon",
            "type":"double"
        },
        {
            "name":"end_lat",
            "type":"double"
        },
        {
            "name":"end_lon",
            "type":"double"
        },
        {
            "name":"fare",
            "type":"double"
        },
        {
            "name":"currency",
            "type":"string"
        },
        {
            "name":"_hoodie_is_deleted",
            "type":"boolean",
            "default":false
        }
    ]
}
```

AVRO_SCHEMA
```
{
    "type":"record",
    "name":"triprec",
    "fields":[
        {
            "name":"timestamp",
            "type":"long"
        },
        {
            "name":"_row_key",
            "type":"string"
        },
        {
            "name":"partition_path",
            "type":[
                "null",
                "string"
            ],
            "default":null
        },
        {
            "name":"rider",
            "type":"string"
        },
        {
            "name":"driver",
            "type":"string"
        },
        {
            "name":"begin_lat",
            "type":"double"
        },
        {
            "name":"begin_lon",
            "type":"double"
        },
        {
            "name":"end_lat",
            "type":"double"
        },
        {
            "name":"end_lon",
            "type":"double"
        },
        {
            "name":"distance_in_meters",
            "type":"int"
        },
        {
            "name":"seconds_since_epoch",
            "type":"long"
        },
        {
            "name":"weight",
            "type":"float"
        },
        {
            "name":"nation",
            "type":"bytes"
        },
        {
            "name":"current_date",
            "type":{
                "type":"int",
                "logicalType":"date"
            }
        },
        {
            "name":"current_ts",
            "type":"long"
        },
        {
            "name":"height",
            "type":{
                "type":"fixed",
                "name":"abc",
                "size":5,
                "logicalType":"decimal",
                "precision":10,
                "scale":6
            }
        },
        {
            "name":"city_to_state",
            "type":{
                "type":"map",
                "values":"string"
            }
        },
        {
            "name":"fare",
            "type":{
                "type":"record",
                "name":"fare",
                "fields":[
                    {
                        "name":"amount",
                        "type":"double"
                    },
                    {
                        "name":"currency",
                        "type":"string"
                    }
                ]
            }
        },
        {
            "name":"tip_history",
            "type":{
                "type":"array",
                "items":{
                    "type":"record",
                    "name":"tip_history",
                    "fields":[
                        {
                            "name":"amount",
                            "type":"double"
                        },
                        {
                            "name":"currency",
                            "type":"string"
                        }
                    ],
                    "default":null
                },
                "default":[

                ]
            },
            "default":[

            ]
        },
        {
            "name":"_hoodie_is_deleted",
            "type":"boolean",
            "default":false
        }
    ]
}
```
```
df.write().partitionBy("datestr").format("parquet").mode(SaveMode.Overwrite).save(srcPath);
```
随机选一个文件,通过Footer读取文件的schema
footer = ParquetFileReader.readFooter(FSUtils.getFs(parquetFilePath.toString(), conf).getConf(), parquetFilePath);

```
    JavaRDD rdd = jsc.parallelize(records);
    Dataset<Row> df = sqlContext.read1().json(rdd);
```


```

 public static final ConfigProperty<String> FULL_BOOTSTRAP_INPUT_PROVIDER_CLASS_NAME = ConfigProperty
     .key("hoodie.bootstrap.full.input.provider")
     .defaultValue("org.apache.hudi.bootstrap.SparkParquetBootstrapDataProvider")
     .sinceVersion("0.6.0")
     .withDocumentation("Class to use for reading the bootstrap dataset partitions/files, for Bootstrap mode FULL_RECORD");

  public static final ConfigProperty<String> MODE_SELECTOR_CLASS_NAME = ConfigProperty
      .key("hoodie.bootstrap.mode.selector")
      .defaultValue(MetadataOnlyBootstrapModeSelector.class.getCanonicalName())
      .sinceVersion("0.6.0")
      .withDocumentation("Selects the mode in which each file/partition in the bootstrapped dataset gets bootstrapped");

```

Hoodieclient 中的
protected final HoodieWriteConfig config;
//这个在windows下会报错.
public RocksDBDAO(String basePath, String rocksDBBasePath) {
  this.rocksDBBasePath =
      String.format("%s/%s/%s", rocksDBBasePath, basePath.split(":")[1].replace("/", "_"), UUID.randomUUID().toString());
  init();
  totalBytesWritten = 0L;
}

```
org.apache.hudi.common.util.collection.RocksDBDAO.RocksDBDAO

```

#### spark 中的partitionBy很有意思
```
df.write().partitionBy("datestr").format("parquet").mode(SaveMode.Overwrite).save(srcPath);
```
并行度为4,分区为3,所以一个分区有四个小文件 ?

# HoodieConsumer 
## BootstrapRecordConsumer(有时候也叫hander,ParquetBootstrapMetadataHandler是一个不同的东西)
Consumer that dequeues records from queue and sends to Merge Handle for writing.
## CopyOnWriteInsertHandler
子类 CopyOnWriteInsertHandler
Consumes stream of hoodie records from in-memory queue and writes to one or more create-handles.
# HoodieProducer
生产者

# HoodieIOHandle

## HoodieWriteHandle
子类 HoodieWriteHandle
 Base class for all write operations logically performed at the file group level.
CopyOnWriteInsertHandler 中包含多个 HoodieWriteHandle,一个handler处理一个分区


逻辑执行在在flie group级别的 所有的写操作 的基类
# HoodieExecutor 
 HoodieExecutor which orchestrates concurrent producers and consumers communicating.
 
这两个的区别和关联是啥
## org.apache.hudi.table.action.bootstrap.ParquetBootstrapMetadataHandler
```
  @Override
  protected void executeBootstrap(HoodieBootstrapHandle<?, ?, ?, ?> bootstrapHandle,
                                  Path sourceFilePath,
                                  KeyGeneratorInterface keyGenerator,
                                  String partitionPath,
                                  Schema schema) throws Exception {
    BoundedInMemoryExecutor<HoodieRecord, HoodieRecord, Void> wrapper = null;
    HoodieRecordMerger recordMerger = table.getConfig().getRecordMerger();

    HoodieFileReader reader = HoodieFileReaderFactory.getReaderFactory(recordMerger.getRecordType())
            .getFileReader(table.getHadoopConf(), sourceFilePath);
    try {
      Function<HoodieRecord, HoodieRecord> transformer = record -> {
        String recordKey = record.getRecordKey(schema, Option.of(keyGenerator));
        return createNewMetadataBootstrapRecord(recordKey, partitionPath, recordMerger.getRecordType())
            // NOTE: Record have to be cloned here to make sure if it holds low-level engine-specific
            //       payload pointing into a shared, mutable (underlying) buffer we get a clean copy of
            //       it since these records will be inserted into the queue later.
            .copy();
      };

      wrapper = new BoundedInMemoryExecutor<HoodieRecord, HoodieRecord, Void>(config.getWriteBufferLimitBytes(),
          reader.getRecordIterator(schema), new BootstrapRecordConsumer(bootstrapHandle), transformer, table.getPreExecuteRunnable());

      wrapper.execute();
    } catch (Exception e) {
      throw new HoodieException(e);
    } finally {
      reader.close();
      if (null != wrapper) {
        wrapper.shutdownNow();
        wrapper.awaitTermination();
      }
      bootstrapHandle.close();
    }
  }
```
注意这个handler(BootstrapMetadataHandler)不是IO handler,将读取数据作为 HoodieExecutor 的生产者,HoodieIOHandle中的读,
HoodieIOHandle中的写当作消费者

# flink 在每个partition内,执行的代码
BoundedInMemoryExecutor
通过BoundedInMemoryQueue,协调并发产(多个)和消费的执行器,这个类将 大小限制,队列生产者,消费者,转换器
作为输入,同时,暴露API来编排这些参与者的并发执行通过一个中央的有界队列
```
//速率,输入,消费者,转换器
  public BoundedInMemoryExecutor(final long bufferLimitInBytes, final Iterator<I> inputItr,
                                 HoodieConsumer<O, E> consumer, Function<I, O> transformFunction, Runnable preExecuteRunnable) {
    this(bufferLimitInBytes, Collections.singletonList(new IteratorBasedQueueProducer<>(inputItr)),
        Option.of(consumer), transformFunction, new DefaultSizeEstimator<>(), preExecuteRunnable);
  }
```
https://stackoverflow.com/questions/6934738/scala-object-module


# clinet table execu
## BaseHoodieClient
### BaseHoodieWriteClient
spark/java/flink implement
```
  public List<WriteStatus> insert(List<HoodieRecord<T>> records, String instantTime) {
    HoodieTable<T, List<HoodieRecord<T>>, List<HoodieKey>, List<WriteStatus>> table =
        initTable(WriteOperationType.INSERT, Option.ofNullable(instantTime));
    table.validateInsertSchema();
    preWrite(instantTime, WriteOperationType.INSERT, table.getMetaClient());
    // create the write handle if not exists
    HoodieWriteMetadata<List<WriteStatus>> result;
    try (AutoCloseableWriteHandle closeableHandle = new AutoCloseableWriteHandle(records, instantTime, table)) {
      result = ((HoodieFlinkTable<T>) table).insert(context, closeableHandle.getWriteHandle(), instantTime, records);
    }
    if (result.getIndexLookupDuration().isPresent()) {
      metrics.updateIndexMetrics(LOOKUP_STR, result.getIndexLookupDuration().get().toMillis());
    }
    return postWrite(result, instantTime, table);
  }
```
in execute method,this menthod will 
1.get HoodieTable
2.preWrite
3.table.doSomething
4.postWrite
#### table.doSomething
BaseCommitActionExecutor
FlinkWriteHelper.newInstance().write in this,
executor.execute(inputRecords);


```
  public HoodieWriteMetadata<List<WriteStatus>> execute() {
    return FlinkWriteHelper.newInstance().write(instantTime, inputRecords, context, table,
        config.shouldCombineBeforeInsert(), config.getInsertShuffleParallelism(), this, operationType);
  }
```
## table 
### HoodieTable
flink/spark/java
```
/**
 * Abstract implementation of a HoodieTable.
 *
 * @param <T> Sub type of HoodieRecordPayload   hoodie payload 子类
 * @param <I> Type of inputs
 * @param <K> Type of keys
 * @param <O> Type of outputs
 */
public abstract class HoodieTable<T, I, K, O> implements Serializable {


public abstract class HoodieSparkTable<T>
    extends HoodieTable<T, HoodieData<HoodieRecord<T>>, HoodieData<HoodieKey>, HoodieData<WriteStatus>> {
```
## executor
### BaseActionExecutor

##  EmbeddedTimelineService
### EmbeddedTimelineService
Timeline Service that runs as part of write client.
### RemoteHoodieTableFileSystemView
A proxy for table file-system view which translates local View API calls to 
REST calls to remote timeline service.
### TimelineService
A standalone timeline service exposing File-System View interfaces to clients.
exposing interface by Javalin ,and process request by RequestHandler 
#### RequestHandler
Main REST Handler class that handles and delegates calls to timeline relevant handlers.

why need to list file in a partition? just read1 from the metadata?
Probably in a long time,The file groups are large ?


# 元数据
## HoodieTableMetaClient
HoodieTableMetaClient allows to access meta-data about a hoodie table It returns meta-data about commits, savepoints, compactions, cleanups as a HoodieTimeline Create an instance of the HoodieTableMetaClient with FileSystem and basePath to start getting the meta-data.
All the timelines are computed lazily, once computed the timeline is cached and never refreshed. Use the HoodieTimeline.reload() to refresh timelines.
```
metaClient.getBasePathV2()

HoodieInstant instant = new HoodieInstant(true, HoodieTimeline.COMMIT_ACTION, "1");
commitTimeline.createNewInstant(instant);
commitTimeline.saveAsComplete(instant, Option.of("test-detail".getBytes()));

timestamp_action_state 是在文件名上的内容
### state
  public enum State {
    // Requested State (valid state for Compaction)
    REQUESTED,
    // Inflight instant
    INFLIGHT,
    // Committed instant
    COMPLETED,
    // Invalid instant
    NIL
  }

meta 元数据文件
.schema  

org.apache.hudi.common.table.timeline.HoodieInstant#getFileName
commit 
    instantTime.inflight
    instantTime.commit.requested
    instantTime.commit
clean 
    instantTime.clean.inflight
    instantTime.clean.requested
    instantTime.clean
deltacommit
    instantTime.deltacommit.inflight
    instantTime.deltacommit.requested
    instantTime.deltacommit
```
```
      val tableMetaClient = if (tableExists) {
        HoodieTableMetaClient.builder
          .setConf(sparkContext.hadoopConfiguration)
          .setBasePath(path)
          .build()
或者
org.apache.hudi.common.table.HoodieTableMetaClient.initTableAndGetMetaClient
```
## HoodieTimeline
HoodieTimeline is a view of meta-data instants in the hoodie table. Instants are specific points in time represented as HoodieInstant.
Timelines are immutable once created and operations create new instance of timelines which filter on the instants and this can be chained.

 metaClient.getActiveTimeline()
 
 ## 方便调试
 关闭hudi的元数据表 
 HoodieMetadataConfig
 
 
 
 
 
 # 写代码配置
 org.apache.hudi.client.functional.TestRemoteFileSystemViewWithMetadataTable#createWriteClient
```

  private SparkRDDWriteClient createWriteClient(Option<TimelineService> timelineService) {
    HoodieWriteConfig writeConfig = HoodieWriteConfig.newBuilder()
        .withPath(basePath)
        .withSchema(HoodieTestDataGenerator.TRIP_EXAMPLE_SCHEMA)
        .withParallelism(2, 2)
        .withBulkInsertParallelism(2)
        .withFinalizeWriteParallelism(2)
        .withDeleteParallelism(2)
        .withTimelineLayoutVersion(TimelineLayoutVersion.CURR_VERSION)
        .withMergeSmallFileGroupCandidatesLimit(0)
            .withMetadataConfig(HoodieMetadataConfig.newBuilder().enable(false).build())
        .withCompactionConfig(HoodieCompactionConfig.newBuilder()
            .withMaxNumDeltaCommitsBeforeCompaction(3)
            .build())
        .withFileSystemViewConfig(FileSystemViewStorageConfig.newBuilder()
            .withStorageType(FileSystemViewStorageType.REMOTE_ONLY)
            .withRemoteServerPort(timelineService.isPresent()
                ? timelineService.get().getServerPort() : REMOTE_PORT_NUM.defaultValue())
            .build())
        .withAutoCommit(false)
        .forTable("test_mor_table")
        .build();
    return new SparkRDDWriteClient(context, writeConfig, timelineService);
  }
```

# hudi marker机制
Re-implement marker files via timeline server

# hudi compile
mvn clean package install -DskipTests -Dspark3.2 -Dscala-2.12 -T10

# hudi 记录 和负载
## BaseAvroPayload 
## HoodieRecord
里面有负载,key,位置 信息
### 什么时候这个类变成 abstract
https://issues.apache.org/jira/browse/HUDI-2656
Make HoodieRecord abstract and use HoodieAvroRecord for the implementation for extensibility
Make HoodieIndex independent of HoodieRecordPayload

recode中生成了负载,负载的时候放置了 compare 字段?
旧版本如何生成HoodieRecord的


# keygenerator
```
  val KEYGENERATOR_CLASS_NAME: ConfigProperty[String] = ConfigProperty
    .key("hoodie.datasource.write.keygenerator.class")
    .defaultValue(classOf[SimpleKeyGenerator].getName)
    .withInferFunction(keyGeneratorInferFunc)
    .withDocumentation("Key generator class, that implements `org.apache.hudi.keygen.KeyGenerator`")
```
hoodie 设置了很多默认值
org.apache.hudi.HoodieWriterUtils.parametersWithWriteDefaults

hoodie.datasource.write.recordkey.field
hoodie.table.recordkey.fields
.setRecordKeyFields(hoodieConfig.getString(RECORDKEY_FIELD))



# 分区设置

