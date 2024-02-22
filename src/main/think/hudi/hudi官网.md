## concept
### Timeline
Key actions performed include
写数据
COMMITS - A commit denotes an atomic write of a batch of records into a table.
清理数据
CLEANS - Background activity that gets rid of older versions of files in the table, that are no longer needed.
增量写
DELTA_COMMIT - A delta commit refers to an atomic write of a batch of records into a MergeOnRead type table, where some/all of the data could be just written to delta logs.
压缩
COMPACTION - Background activity to reconcile differential data structures within Hudi e.g: moving updates from row based log files to columnar formats. Internally, compaction manifests as a special commit on the timeline
回滚
ROLLBACK - Indicates that a commit/delta commit was unsuccessful & rolled back, removing any partial files produced during such a write

SAVEPOINT - Marks certain file groups as "saved", such that cleaner will not delete them. It helps restore the table to a point on the timeline, in case of disaster/data recovery scenarios.

### Metadata Table
Metadata Configs
org.apache.hudi.common.config.HoodieMetadataConfig
hoodie.metadata.enable

Enable the internal metadata table which serves table metadata like level file listings
Default Value: true (Optional)
Config Param: ENABLE
Since Version: 0.7.0
### 并发控制

#### Supported Concurrency Controls
##### mvcc
##### OPTIMISTIC CONCURRENCY
## services
### Bootstrapping
Hoodieinstant


## Configurations
### Basic Configurations
####  org.apache.hudi.DataSourceOptions.scala
#### org.apache.hudi.keygen.constant.KeyGeneratorOptions
Key Generator Options
Hudi maintains keys (record key + partition path) for uniquely identifying a particular record.
 This config allows developers to setup the Key generator class that will extract these out of incoming records.
#### Write Client Configs



#### other from code
##### hoodie.datasource.write.reconcile.schema
当存在表时,表的schema如何选择.当DISABLED,选择写入的schema
启用 schema will kept the same or extended.(existing column  being dropped in a new batch will not lose data)

### indexing
Bloom Index (default): Employs bloom filters built out of the record keys, optionally also pruning candidate files using record key ranges.
Simple Index: Performs a lean join of the incoming update/delete records against keys extracted from the table on storage.
HBase Index: Manages the index mapping in an external Apache HBase table.
Bring your own implementation: You can extend this public API to implement custom indexing.


#### first look udf index to know the detail of index
### Schema Evolution
#### Out-of-the-box Schema Evolution
Schema Evolution 对数据的管理是十分重要的,hudi支持通用的schema evolution.
adding a nullable field or promoting a datatype of a field

## services
### cleaning
 manage how much history you keep to balance your costs.
自动清理是开启的,
It's recommended to leave this enabled to ensure metadata and data storage growth is bounded.
hoodie.clean.automatic
#### Cleaning Retention Policies
KEEP_LATEST_COMMITS:
hoodie.cleaner.commits.retained
#### Run Independently
.
