# Synchronize  mysql table to hudi
1.获取新增的表,根据mysql的schema生成对应的hudi表schema(可以只包含路径,分区,主键等)
2.全量获取表数据写入
3.从binlog中获取各个表的数据写入

同步的表必须有主键,对应于hudi中的主键.

## get all table schema,generate hudi table(schema)
hudi-utilities中的SchemaRegistryProvider 需要的都是avro模式的
hudi的schema并不包含主键,分区,preCombine等信息,just about columns;

主键 primary key
分区 需要用户指定 默认通过CREATE_DATE 来获取
preCombine 同  _event_seq,取值为binlog+pos
```
    return flattenedDataset.withColumn(DebeziumConstants.ADDED_SEQ_COL_NAME,
            callUDF(generateUniqueSeqUdfFn, flattenedDataset.col(DebeziumConstants.FLATTENED_FILE_COL_NAME),
                flattenedDataset.col(DebeziumConstants.FLATTENED_POS_COL_NAME)));
```
### 调研一下  hudi-utilities中的 jdbc schema
### spark 中主键 分区 precombine 在那里使用
```
  public static HoodieWriteConfig createHoodieConfig(String schemaStr, String basePath,
      String tblName, Map<String, String> parameters) {
    boolean asyncCompact = Boolean.parseBoolean(parameters.get(DataSourceWriteOptions.ASYNC_COMPACT_ENABLE().key()));
    boolean inlineCompact = !asyncCompact && parameters.get(DataSourceWriteOptions.TABLE_TYPE().key())
        .equals(DataSourceWriteOptions.MOR_TABLE_TYPE_OPT_VAL());
    // insert/bulk-insert combining to be true, if filtering for duplicates
    boolean combineInserts = Boolean.parseBoolean(parameters.get(DataSourceWriteOptions.INSERT_DROP_DUPS().key()));
    HoodieWriteConfig.Builder builder = HoodieWriteConfig.newBuilder()
        .withPath(basePath).withAutoCommit(false).combineInput(combineInserts, true);
    if (schemaStr != null) {
      builder = builder.withSchema(schemaStr);
    }
    //表名
    return builder.forTable(tblName)
        .withCompactionConfig(HoodieCompactionConfig.newBuilder()
            .withInlineCompaction(inlineCompact).build())
        .withPayloadConfig(HoodiePayloadConfig.newBuilder()
            .withPayloadClass(parameters.get(DataSourceWriteOptions.PAYLOAD_CLASS_NAME().key()))
            //preCombine
            .withPayloadOrderingField(parameters.get(DataSourceWriteOptions.PRECOMBINE_FIELD().key()))
            .build())
        // override above with Hoodie configs specified as options.
        .withProps(parameters).build();
  }
```
通过 属性获取主键
```


```
主键在 RECORDKEY_FIELDS
```
  public static final ConfigProperty<String> RECORDKEY_FIELDS = ConfigProperty
      .key("hoodie.table.recordkey.fields")
      .noDefaultValue()
      .withDocumentation("Columns used to uniquely identify the table. Concatenated values of these fields are used as "
          + " the record key component of HoodieKey.");
```

如果配置不同会报错
```
    validateTableConfig(sqlContext.sparkSession, optParams, tableConfig, mode == SaveMode.Overwrite);

```
默认的配置从配置中获取
```
    var tableConfig = getHoodieTableConfig(sparkContext, path, mode, hoodieTableConfigOpt)
```
从row 转换为 GenericRecord (avro)
```
val convert = AvroConversionUtils.createInternalRowToAvroConverter(writerSchema, writerAvroSchema, nullable = nullable)

```


 https://issues.apache.org/jira/browse/HUDI-5665
 we expect users to set every table config along w/ every write 
 operation. for write configs, it makes sense, 
 but for table configs, we should be able to re-use properties from existing hoodie.properties. 
 
 目前设置mandatory fields in every write,例如 record keys, partition path etc.
 这些不能改变,同时设置进了表的配置,后面就不需要显式设置了.
 
SimpleKeyGenerator 
```
  public SimpleKeyGenerator(TypedProperties props) {
    this(props, props.getString(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key()),
        props.getString(KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key()));
  }
```
生成
org.apache.hudi.common.model.HoodieRecord

# 表的生成
TableSchemaResolver
hoodie.table.create.schema={"type"\:"record","name"\:"hudi_cow_nonpcf_tbl_record","namespace"\:"hoodie.hudi_cow_nonpcf_tbl","fields"\:[{"name"\:"_hoodie_commit_time","type"\:["string","null"]},{"name"\:"_hoodie_commit_seqno","type"\:["string","null"]},{"name"\:"_hoodie_record_key","type"\:["string","null"]},{"name"\:"_hoodie_partition_path","type"\:["string","null"]},{"name"\:"_hoodie_file_name","type"\:["string","null"]},{"name"\:"uuid","type"\:["int","null"]},{"name"\:"name","type"\:["string","null"]},{"name"\:"price","type"\:["double","null"]}]}
关于schema的初始化.还要思考,第一个要加入这个吗?如果没有binlog我也需要创建表.方便开发.
```
sql 中增加这个参数,可以生成表的schema
```


setRecordKeyFields(hoodieConfig.getString(RECORDKEY_FIELD))