# Concepts
## Write Operations
### Writing path
这是对Hudi写入路径的内部了解,写入过程中发生的事件序列。
#### 1.去重
hoodie.datasource.write.insert.drop.duplicates:false
默认同一批数据是否需要去重
#### 2.Index Lookup
通过索引查找数据
#### 3.File Sizing
根据上一次的commit,分配文件大小
#### 4 分区

#### 5 Write I/O
#### 6 Update Index
#### 7 Commit
#### 8 Clean (if needed)
#### 9 Compaction
#### 10 Archive

### 类中的实现 org.apache.hudi.table.action.commit.BaseWriteHelper
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

## Key Generation
hudi的主键 由 分区+record key组成
KeyGenerator
在spark 的包中
org.apache.hudi.keygen.TimestampBasedKeyGenerator