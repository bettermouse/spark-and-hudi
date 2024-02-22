## 17张图带你彻底理解Hudi Upsert原理
https://zhuanlan.zhihu.com/p/380943820

1.通过SparkSqlWriter 开始处理数据
1.1 开始提交
```
-- 通过配置创建  HoodieWriteConfig
  public static HoodieWriteConfig createHoodieConfig(String schemaStr, String basePath,
      String tblName, Map<String, String> parameters) {
    boolean asyncCompact = Boolean.parseBoolean(parameters.get(DataSourceWriteOptions.ASYNC_COMPACT_ENABLE().key()));
    boolean inlineCompact = !asyncCompact && parameters.get(DataSourceWriteOptions.TABLE_TYPE().key())
        .equals(DataSourceWriteOptions.MOR_TABLE_TYPE_OPT_VAL());
    // insert/bulk-insert combining to be true, if filtering for duplicates
    boolean combineInserts = Boolean.parseBoolean(parameters.get(DataSourceWriteOptions.INSERT_DROP_DUPS().key()));
    HoodieWriteConfig.Builder builder = HoodieWriteConfig.newBuilder()
//禁用自动提交
        .withPath(basePath).withAutoCommit(false).combineInput(combineInserts, true);
    if (schemaStr != null) {
      builder = builder.withSchema(schemaStr);
    }

    return builder.forTable(tblName)
        .withCompactionConfig(HoodieCompactionConfig.newBuilder()
            .withInlineCompaction(inlineCompact).build())
        .withPayloadConfig(HoodiePayloadConfig.newBuilder()
            .withPayloadClass(parameters.get(DataSourceWriteOptions.PAYLOAD_CLASS_NAME().key()))
            .withPayloadOrderingField(parameters.get(DataSourceWriteOptions.PRECOMBINE_FIELD().key()))
            .build())
        // override above with Hoodie configs specified as options.
        .withProps(parameters).build();
  }
-- 获取一个SparkRDDWriteClient[_]
val client = hoodieWriteClient.getOrElse(DataSourceUtils.createHoodieClient(jsc,
  null, path, tblName,
  mapAsJavaMap(addSchemaEvolutionParameters(parameters, internalSchemaOpt) - HoodieWriteConfig.AUTO_COMMIT_ENABLE.key)))
  .asInstanceOf[SparkRDDWriteClient[_]]
```
org.apache.hudi.table.action.commit.BaseCommitActionExecutor.commitOnAutoCommit
spark中不自动提交,这个提交的是什么东西?
```
  protected void commitOnAutoCommit(HoodieWriteMetadata result) {
    // validate commit action before committing result
    runPrecommitValidators(result);
    if (config.shouldAutoCommit()) {
      LOG.info("Auto commit enabled: Committing " + instantTime);
      autoCommit(extraMetadata, result);
    } else {
      LOG.info("Auto commit disabled for " + instantTime);
    }
  }
```
1.1.1 数据回滚
1.2 构造HoodieRecord Rdd 对象

```

// Convert to RDD[HoodieRecord]
//first row ->GenericRecord(avro)
org.apache.hudi.HoodieSparkUtils$.createRdd(org.apache.spark.sql.Dataset<org.apache.spark.sql.Row>, java.lang.String, java.lang.String, boolean, org.apache.hudi.common.util.Option<org.apache.avro.Schema>)
val writerAvroSchema = AvroConversionUtils.convertStructTypeToAvroSchema(writerSchema, structName, recordNamespace)
val convert = AvroConversionUtils.createInternalRowToAvroConverter(writerSchema, writerAvroSchema, nullable = nullable)
rows.map { ir => transform(convert(ir)) }
//second  GenericRecord->HoodieRecord(HoodieAvroRecord)
包含HoodieKey(key 分区路径),  HoodieRecordPayload 包含数据,preCombine

  public static HoodieRecord createHoodieRecord(GenericRecord gr, Comparable orderingVal, HoodieKey hKey,
      String payloadClass) throws IOException {
    HoodieRecordPayload payload = DataSourceUtils.createPayload(payloadClass, gr, orderingVal);
    return new HoodieAvroRecord<>(hKey, payload);


HoodieRecord 可以做数据合并
比如在数据去重中
 T reducedData = (T) rec2.getData().preCombine(rec1.getData());
      HoodieKey reducedKey = rec1.getData().equals(reducedData) ? rec1.getKey() : rec2.getKey();

HoodieRecord 还有location (current,new)

在写数据的时候,可能还需要这个,一起来看一下
org.apache.hudi.io.HoodieMergeHandle
 if (combinedAvroRecord.isPresent() && combinedAvroRecord.get().equals(IGNORE_RECORD)) {
          // If it is an IGNORE_RECORD, just copy the old record, and do not update the new record.
          copyOldRecord = true;
        } else if (writeUpdateRecord(hoodieRecord, oldRecord, combinedAvroRecord)) {
```
HoodieRecord 的功能 key,payload(数据,合并,去重)

preCombine
combineAndGetUpdateValue 这两个功能在目前看来应该没有什么本质的区别

对avro的特殊处理,在序列化上加速
  /**
   * A runtime config pass to the {@link HoodieRecordPayload#getInsertValue(Schema, Properties)}
   * to tell if the current record is a update record or insert record for mor table.
   */
  public static final String PAYLOAD_IS_UPDATE_RECORD_FOR_MOR = "hoodie.is.update.record.for.mor";
  
  注意观察,在spark中,avro序列化是如何转换的,schema是如何转换的
```
 */
public abstract class BaseAvroPayload implements Serializable {
  /**
   * Avro data extracted from the source converted to bytes.
   */
  protected final byte[] recordBytes;
需要看一下,flink是如何处理数据的.在flink 数据转换的过程中,

```

1.3 数据去重
spark client upsert
->construct  hudi spark cw/mr table 
preWrite
table.upsert()
```
org.apache.hudi.table.action.BaseActionExecutor.execute 所有的action都有
org.apache.hudi.table.action.commit.BaseCommitActionExecutor.execute CommitAction有的方法
 public abstract HoodieWriteMetadata<O> execute(I inputRecords);
```
postWrite

1.4 数据位置信息索引查找
```
布隆索引（BloomIndex）
全局布隆索引（GlobalBloomIndex）
简易索引（SimpleIndex）
简易全局索引（GlobalSimpleIndex）
全局HBase 索引(HbaseIndex)
内存索引(InMemoryHashIndex)。
```

1.5 数据合并




### 数据回滚
debug
1.write data without commit

2.
client.startCommitWithTime(instantTime, commitActionType)
每个文件名都包含instant,所以可以从instant推测出每个commit influence files.
hoodie.cleaner.policy.failed.writes=eager

HoodieInstant 从文件中读取,instant最后的状态
org.apache.hudi.common.table.HoodieTableMetaClient.scanHoodieInstantsFromFileSystem(org.apache.hadoop.fs.Path, java.util.Set<java.lang.String>, boolean)
@param applyLayoutVersionFilters Depending on Timeline layout version, if there are multiple states for the same
   * action instant, only include the highest state
仅包含最高的state

rollbackRequests.addAll(getRollbackStrategy().getRollbackRequests(instantToRollback));

```
  private BaseRollbackPlanActionExecutor.RollbackStrategy getRollbackStrategy() {
    if (shouldRollbackUsingMarkers) {
      return new MarkerBasedRollbackStrategy(table, context, config, instantTime);
    } else {
      return new ListingBasedRollbackStrategy(table, context, config, instantTime);
    }
  }
```


## spark 逻辑封装
```
  @Override
  public HoodieWriteMetadata<HoodieData<WriteStatus>> execute() {
    return HoodieWriteHelper.newInstance().write(instantTime, inputRecordsRDD, context, table,
        config.shouldCombineBeforeUpsert(), config.getUpsertShuffleParallelism(),this, operationType);
  }
```

 public abstract HoodieWriteMetadata<O> execute(I inputRecords);
 
 
## 对逻辑的梳理 spark
1.构造client 如 SparkRDDWriteClient,rdd
2.client.upsert 调用对应的engine对应的table类型 
```
  @Override
  public HoodieWriteMetadata<HoodieData<WriteStatus>> upsert(HoodieEngineContext context, String instantTime, HoodieData<HoodieRecord<T>> records) {
    return new SparkUpsertCommitActionExecutor<>((HoodieSparkEngineContext) context, config, this, instantTime, records).execute();
  }
```
3.调用对应的 executor 去执行获取到 tagRdd,执行对应的操作
```
  public HoodieWriteMetadata<O> write(String instantTime,
                                      I inputRecords,
                                      HoodieEngineContext context,
                                      HoodieTable<T, I, K, O> table,
                                      boolean shouldCombine,
                                      int configuredShuffleParallelism,
                                      BaseCommitActionExecutor<T, I, K, O, R> executor,
                                      WriteOperationType operationType) {
    try {
      int targetParallelism =
          deduceShuffleParallelism(inputRecords, configuredShuffleParallelism);

      // De-dupe/merge if needed
      I dedupedRecords =
          combineOnCondition(shouldCombine, inputRecords, targetParallelism, table);

      Instant lookupBegin = Instant.now();
      I taggedRecords = dedupedRecords;
      if (table.getIndex().requiresTagging(operationType)) {
        // perform index loop up to get existing location of records
        context.setJobStatus(this.getClass().getSimpleName(), "Tagging: " + table.getConfig().getTableName());
        taggedRecords = tag(dedupedRecords, context, table);
      }
      Duration indexLookupDuration = Duration.between(lookupBegin, Instant.now());

      HoodieWriteMetadata<O> result = executor.execute(taggedRecords);
      result.setIndexLookupDuration(indexLookupDuration);
      return result;
    } catch (Throwable e) {
      if (e instanceof HoodieUpsertException) {
        throw (HoodieUpsertException) e;
      }
      throw new HoodieUpsertException("Failed to upsert for commit time " + instantTime, e);
    }
  }

```