## RFC - 22 : Snapshot Isolation using Optimistic Concurrency Control for multi-writers
https://cwiki.apache.org/confluence/display/HUDI/RFC+-+22+%3A+Snapshot+Isolation+using+Optimistic+Concurrency+Control+for+multi-writers
https://issues.apache.org/jira/browse/HUDI-845

## Abstract
Since there is no “external server” component to managing these tables,it also allows for significantly less operational burden
Hudi currently supports ACID guarantees between writer and readers
Currently, Hudi supports Snapshot Isolation level and allows only a single client writer to
 mutate the table on the DFS.
  This RFC proposes to introduce the ability for concurrent writers to Hudi, 
  allowing file level concurrency and providing Serializable Snapshot Isolation using optimistic concurrency control.
  
目前,hudi支持 Snapshot Isolation level,允许仅一个单一的client写数据,
介绍并发写,允许文件级别的并发.
## Background
### Motivation
A classic example cited is backfill.
### Fundamentals
Hudi currently supports a single writer model and uses MVCC for concurrently updating a table via tables services such as clustering, compaction, cleaning, thus allowing then to run asynchronously without blocking writers

Update conflicts are generally solved in different ways as follows
更新冲突用下面的几种方法来解决


## Implementation
The following section describes what those features are and how we plan to alter them to support multi writer. 
### Inline rollback

   
   
# code
https://issues.apache.org/jira/browse/HUDI-1456
## 
```
org.apache.hudi.client.SparkRDDWriteClient.commit
  /**
   * Complete changes performed at the given instantTime marker with specified action.
   */
  @Override
  public boolean commit(String instantTime, JavaRDD<WriteStatus> writeStatuses, Option<Map<String, String>> extraMetadata,
                        String commitActionType, Map<String, List<String>> partitionToReplacedFileIds,
                        Option<BiConsumer<HoodieTableMetaClient, HoodieCommitMetadata>> extraPreCommitFunc) {
    context.setJobStatus(this.getClass().getSimpleName(), "Committing stats: " + config.getTableName());
    List<HoodieWriteStat> writeStats = writeStatuses.map(WriteStatus::getStat).collect();
    return commitStats(instantTime, writeStats, extraMetadata, commitActionType, partitionToReplacedFileIds, extraPreCommitFunc);
  }

org.apache.hudi.client.BaseHoodieWriteClient.commitStats(java.lang.String, java.util.List<org.apache.hudi.common.model.HoodieWriteStat>, org.apache.hudi.common.util.Option<java.util.Map<java.lang.String,java.lang.String>>, java.lang.String, java.util.Map<java.lang.String,java.util.List<java.lang.String>>, org.apache.hudi.common.util.Option<java.util.function.BiConsumer<org.apache.hudi.common.table.HoodieTableMetaClient,org.apache.hudi.common.model.HoodieCommitMetadata>>)
  public boolean commitStats(String instantTime, List<HoodieWriteStat> stats, Option<Map<String, String>> extraMetadata,
                             String commitActionType, Map<String, List<String>> partitionToReplaceFileIds,
                             Option<BiConsumer<HoodieTableMetaClient, HoodieCommitMetadata>> extraPreCommitFunc) {
    // Skip the empty commit if not allowed
    if (!config.allowEmptyCommit() && stats.isEmpty()) {
      return true;
    }
    LOG.info("Committing " + instantTime + " action " + commitActionType);
    // Create a Hoodie table which encapsulated the commits and files visible
    HoodieTable table = createTable(config, hadoopConf);
    HoodieCommitMetadata metadata = CommitUtils.buildMetadata(stats, partitionToReplaceFileIds,
        extraMetadata, operationType, config.getWriteSchema(), commitActionType);
    HoodieInstant inflightInstant = new HoodieInstant(State.INFLIGHT, table.getMetaClient().getCommitActionType(), instantTime);
    HeartbeatUtils.abortIfHeartbeatExpired(instantTime, table, heartbeatClient, config);
    this.txnManager.beginTransaction(Option.of(inflightInstant),
        lastCompletedTxnAndMetadata.isPresent() ? Option.of(lastCompletedTxnAndMetadata.get().getLeft()) : Option.empty());
    try {
      preCommit(inflightInstant, metadata);
      if (extraPreCommitFunc.isPresent()) {
        extraPreCommitFunc.get().accept(table.getMetaClient(), metadata);
      }
      commit(table, commitActionType, instantTime, metadata, stats);
      postCommit(table, metadata, instantTime, extraMetadata);
      LOG.info("Committed " + instantTime);
      releaseResources(instantTime);
    } catch (IOException e) {
      throw new HoodieCommitException("Failed to complete commit " + config.getBasePath() + " at time " + instantTime, e);
    } finally {
      this.txnManager.endTransaction(Option.of(inflightInstant));
    }

    // trigger clean and archival.
    // Each internal call should ensure to lock if required.
    mayBeCleanAndArchive(table);
    // We don't want to fail the commit if hoodie.fail.writes.on.inline.table.service.exception is false. We catch warn if false
    try {
      // do this outside of lock since compaction, clustering can be time taking and we don't need a lock for the entire execution period
      runTableServicesInline(table, metadata, extraMetadata);
    } catch (Exception e) {
      if (config.isFailOnInlineTableServiceExceptionEnabled()) {
        throw e;
      }
      LOG.warn("Inline compaction or clustering failed with exception: " + e.getMessage()
          + ". Moving further since \"hoodie.fail.writes.on.inline.table.service.exception\" is set to false.");
    }

    emitCommitMetrics(instantTime, metadata, commitActionType);

    // callback if needed.
    if (config.writeCommitCallbackOn()) {
      if (null == commitCallback) {
        commitCallback = HoodieCommitCallbackFactory.create(config);
      }
      commitCallback.call(new HoodieWriteCommitCallbackMessage(instantTime, config.getTableName(), config.getBasePath(), stats));
    }
    return true;
  }

```

# 具体冲突的判断
HoodieWriteConflictException
org.apache.hudi.exception.HoodieWriteConflictException

## TransactionUtils
org.apache.hudi.client.utils.TransactionUtils
```
  @Override
  protected void preCommit(HoodieInstant inflightInstant, HoodieCommitMetadata metadata) {
//

    // Create a Hoodie table after startTxn which encapsulated the commits and files visible.
    // Important to create this after the lock to ensure the latest commits show up in the timeline without need for reload
    HoodieTable table = createTable(config, hadoopConf);
    resolveWriteConflict(table, metadata, this.pendingInflightAndRequestedInstants);
  }

```