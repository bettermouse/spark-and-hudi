## HoodieStorageLayout
```
public abstract class HoodieTable<T, I, K, O> implements Serializable {

  private static final Logger LOG = LogManager.getLogger(HoodieTable.class);

  protected final HoodieWriteConfig config;
  protected final HoodieTableMetaClient metaClient;
  protected final HoodieIndex<?, ?> index;
  private SerializableConfiguration hadoopConfiguration;
  protected final TaskContextSupplier taskContextSupplier;
  private final HoodieTableMetadata metadata;
  private final HoodieStorageLayout storageLayout;


/**
 * Storage layout defines how the files are organized within a table.
 */
public abstract class HoodieStorageLayout implements Serializable {
```

## hash索引参数设置
HoodieSimpleBucketIndex
```
    .option(HoodieIndexConfig.INDEX_TYPE.key(), BUCKET.name)
    .option(HoodieIndexConfig.BUCKET_INDEX_NUM_BUCKETS.key(),2)


```

# code
## HoodieIndex
Base class for different types of indexes to determine the mapping from uuid.
查找 fileid
修改索引
## HoodieSimpleBucketIndex
查找一个分区的桶
```
  private Map<Integer, HoodieRecordLocation> loadPartitionBucketIdFileIdMapping(
      HoodieTable hoodieTable,
      String partition) {
    // bucketId -> fileIds
    Map<Integer, HoodieRecordLocation> bucketIdToFileIdMapping = new HashMap<>();
    hoodieTable.getMetaClient().reloadActiveTimeline();
    HoodieIndexUtils
        .getLatestFileSlicesForPartition(partition, hoodieTable)
        .forEach(fileSlice -> {
          String fileId = fileSlice.getFileId();
          String commitTime = fileSlice.getBaseInstantTime();

          int bucketId = BucketIdentifier.bucketIdFromFileId(fileId);
          if (!bucketIdToFileIdMapping.containsKey(bucketId)) {
            bucketIdToFileIdMapping.put(bucketId, new HoodieRecordLocation(commitTime, fileId));
          } else {
            // Check if bucket data is valid
            throw new HoodieIOException("Find multiple files at partition path="
                + partition + " belongs to the same bucket id = " + bucketId);
          }
        });
    return bucketIdToFileIdMapping;
  }

```
## HoodieIndexUtils
Hoodie Index Utilities.