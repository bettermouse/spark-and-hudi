## DeltaStreamer
从不同的源获取数据
kafka
json,avro
Manage checkpoints, rollback & recovery
Leverage Avro schemas from DFS or Confluent schema registry.
Support for plugging in transformations


## MultiTableDeltaStreamer
对MultiTableDeltaStreamer的包装,同时写多个表,目前仅支持表的顺序处理
```
  * --config-folder
    the path to the folder which contains all the table wise config files
    包含所有的表配置
    --base-path-prefix
    this is added to enable users to create all the hudi datasets for related tables under one path in FS. The datasets are then created under the path - <base_path_prefix>/<database>/<table_to_be_ingested>. However you can override the paths for every table by setting the property hoodie.deltastreamer.ingestion.targetBasePath

```

## org.apache.hudi.utilities.sources.Source
Represents a source from which we can tail data. Assumes a constructor that takes properties.

org.apache.hudi.utilities.sources.Source#fetchNewData
```

  @PublicAPIMethod(maturity = ApiMaturityLevel.STABLE)
  protected abstract InputBatch<T> fetchNewData(Option<String> lastCkptStr, long sourceLimit);

  /**
   * Main API called by Hoodie Delta Streamer to fetch records.
   * 
    获取新的数据
   * @param lastCkptStr Last Checkpoint
   * @param sourceLimit Source Limit
   * @return
   */
  public final InputBatch<T> fetchNext(Option<String> lastCkptStr, long sourceLimit) {
    InputBatch<T> batch = fetchNewData(lastCkptStr, sourceLimit);
    // If overriddenSchemaProvider is passed in CLI, use it
    return overriddenSchemaProvider == null ? batch
        : new InputBatch<>(batch.getBatch(), batch.getCheckpointForNextBatch(), overriddenSchemaProvider);
  }
```
### JDBC Source