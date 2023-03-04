## Optimize Record Payload handling
https://github.com/apache/hudi/blob/master/rfc/rfc-46/rfc-46.md

### abstract
都要转成avro. during merge, column value extractions, writing into storage, etc

它带来了方便,引起了不必要的序列化,反序列化

### 实现
将record作业一个不透明的物体( as an opaque object),
state
To achieve stated goals 为了实现既定目标
暴露某些API来访问关键数据(预组合、主键、分区键等)，但不提供对原始有效负载的访问。


#### HoodieRecord  促使HoodieRecord 成为和单一数据交互的标准API
1.替换所有来自HoodieRecordPayload的访问?
2.接口和实现
3.Implementing new standardized record-level APIs (like getPartitionKey , getRecordKey, etc)
4.Staying internal component, that will NOT contain any user-defined semantic (like merging)
#### 抽取单独的mergeAPI
1.抽象为无状态对象
#### HoodieRecordPayload 最终将被移除
减少了Java的反射
#### org.apache.hudi.common.model.HoodieRecordPayload
preCombine  是同一批的数据合并
combineAndGetUpdateValue 将存储的数据和本数据合并

## Record Merge API
``
interface HoodieRecordMerger {

   /**
    * The kind of merging strategy this recordMerger belongs to. A UUID represents merging strategy.
    */
   String getMergingStrategyId();
  
   // This method converges combineAndGetUpdateValue and precombine from HoodiePayload. 
   // It'd be associative operation: f(a, f(b, c)) = f(f(a, b), c) (which we can translate as having 3 versions A, B, C of the single record, both orders of operations applications have to yield the same result)
   Option<HoodieRecord> merge(HoodieRecord older, HoodieRecord newer, Schema schema, Properties props) throws IOException;
   
   // The record type handled by the current merger
   // SPARK, AVRO, FLINK
   HoodieRecordType getRecordType();
}

/**
 * Spark-specific implementation 
 */
class HoodieSparkRecordMerger implements HoodieRecordMerger {

  @Override
  public String getMergingStrategyId() {
    return LATEST_RECORD_MERGING_STRATEGY;
  }
  
   @Override
   Option<HoodieRecord> merge(HoodieRecord older, HoodieRecord newer, Schema schema, Properties props) throws IOException {
     // Implements particular merging semantic natively for Spark row representation encapsulated wrapped around in HoodieSparkRecord.
   }

   @Override
   HoodieRecordType getRecordType() {
     return HoodieRecordType.SPARK;
   }
}
   
/**
 * Flink-specific implementation 
 */
class HoodieFlinkRecordMerger implements HoodieRecordMerger {

   @Override
   public String getMergingStrategyId() {
      return LATEST_RECORD_MERGING_STRATEGY;
   }
  
   @Override
   Option<HoodieRecord> merge(HoodieRecord older, HoodieRecord newer, Schema schema, Properties props) throws IOException {
      // Implements particular merging semantic natively for Flink row representation encapsulated wrapped around in HoodieFlinkRecord.
   }

   @Override
   HoodieRecordType getRecordType() {
      return HoodieRecordType.FLINK;
   }
}
``
HoodieWriteConfig.RECORD_MERGER_IMPLS RecordMerger class name
HoodieWriteConfig.RECORD_MERGER_STRATEGY

## Migration from HoodieRecordPayload to HoodieRecordMerger

```
Previously, we used to have separate methods for merging:

preCombine was used to either deduplicate records in a batch or merge ones coming from delta-logs, while
combineAndGetUpdateValue was used to combine incoming record w/ the one persisted in storage
```

## HoodieInternalRow extends InternalRow
Hudi internal implementation of the InternalRow allowing to extend arbitrary
 InternalRow overlaying Hudi-internal meta-fields on top of it. 
 Capable of overlaying meta-fields in both cases: 
 whether original sourceRow contains meta columns or not. 
 This allows to handle following use-cases allowing to avoid any manipulation (reshuffling) of the source row, by simply creating new instance of HoodieInternalRow with all the meta-values provided
When meta-fields need to be prepended to the source InternalRow
When meta-fields need to be updated w/in the source InternalRow (UnsafeRow currently does not allow in-place updates due to its memory layout)


# 问题
## 1
kyro序列化InternalRow为什么没有写,肯定是在spark中已经加了
```
  protected final void writeRecordPayload(InternalRow payload, Kryo kryo, Output output) {
    // NOTE: [[payload]] could be null if record has already been deflated
    UnsafeRow unsafeRow = convertToUnsafeRow(payload, schema);

    kryo.writeObjectOrNull(output, unsafeRow, UnsafeRow.class);
  }
```
//todo
没前没有发现,需要在新版本中debug一下,看它的序列化是否花费了多余的数据
//这个写文档了吗
put("spark.kryo.registrator", "org.apache.spark.HoodieSparkKryoRegistrar")

## 2.