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
    Dataset<Row> df = sqlContext.read().json(rdd);
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


# HoodieIOHandle

# HoodieExecutor 

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
注意这个handler不是IO handler,将读取数据作为 HoodieExecutor 的生产者,HoodieIOHandle中的读,
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