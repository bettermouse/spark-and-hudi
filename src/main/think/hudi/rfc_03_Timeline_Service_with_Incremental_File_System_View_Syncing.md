## RFC - 03 : Timeline Service with Incremental File System View Syncing 
https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=113708965

## Abstract

## File System View Bootstrap
partitions  will be used to bootstrap the file-system view in the timeline server
## class
### TableFileSystemView
Interface for viewing the table file system.
### SyncableFileSystemView
### AbstractTableFileSystemView
multiple TableFileSystemView Implementations 的公共安全实现
a.
b.
c.
d.

### IncrementalTimelineSyncFileSystemView
Adds the capability to incrementally sync the changes to file-system view as and when new instants gets completed.
当新的instants完成,添加增量同步变化后文件系统的能力
#### HoodieTableFileSystemView
TableFileSystemView Implementations based on in-memory storage
#### HoodieMetadataFileSystemView
HoodieTableFileSystemView implementation that retrieved partition listings from the Metadata Table.
#### SpillableMapBasedFileSystemView
* Table FileSystemView implementation where view is stored in spillable disk using fixed memory.
从元数据表

#### RocksDbBasedFileSystemView
#### RocksDbBasedFileSystemView


### FileSystemViewManager


## issue
### when compaction create,log file will add to new fileslice
pending compaction
https://github.com/apache/hudi/pull/3703
```
  /**
   * Get the commit + pending-compaction timeline visible for this table. A RT filesystem view is constructed with this
   * timeline so that file-slice after pending compaction-requested instant-time is also considered valid. A RT
   * file-system view for reading must then merge the file-slices before and after pending compaction instant so that
   * all delta-commits are read1.
   */
//获取  commit + pending-compaction timeline可见性对这个表
//
  public HoodieTimeline getCommitsAndCompactionTimeline() {
    switch (this.getTableType()) {
      case COPY_ON_WRITE:
        //
        return getActiveTimeline().getCommitTimeline();
      case MERGE_ON_READ:
        return getActiveTimeline().getWriteTimeline();
      default:
        throw new HoodieException("Unsupported table type :" + this.getTableType());
    }
//cluster bootstrap
```

FileSystemView and Timeline level changes to support Async Compaction
查看这个issue,看hudi如何支持异步 compaction

```
org.apache.hudi.common.table.timeline.HoodieActiveTimeline
  /**
   * Get all instants (commits, delta commits, in-flight/request compaction) that produce new data, in the active
   * timeline *
   * With Async compaction a requested/inflight compaction-instant is a valid baseInstant for a file-slice as there
   * could be delta-commits with that baseInstant.
   */
  public HoodieTimeline getCommitsAndCompactionTimeline() {
    return getTimelineOfActions(
        Sets.newHashSet(COMMIT_ACTION, DELTA_COMMIT_ACTION, COMPACTION_ACTION));
  }

```
批并不会生成compaction
可以通过文档来生成compaction
https://hudi.apache.org/docs/compaction#offline-compaction

#### 总结
org.apache.hudi.common.table.view.AbstractTableFileSystemView
```
  /**
   * Initialize the view.
   */
  protected void init(HoodieTableMetaClient metaClient, HoodieTimeline visibleActiveTimeline) {
    this.metaClient = metaClient;
    refreshTimeline(visibleActiveTimeline);
    //replace
    resetFileGroupsReplaced(visibleCommitsAndCompactionTimeline);
    this.bootstrapIndex =  BootstrapIndex.getBootstrapIndex(metaClient);
    // Load Pending Compaction Operations
    //Pending Compaction
    resetPendingCompactionOperations(CompactionUtils.getAllPendingCompactionOperations(metaClient).values().stream()
        .map(e -> Pair.of(e.getKey(), CompactionOperation.convertFromAvroRecordInstance(e.getValue()))));
    // Load Pending LogCompaction Operations.
    //Pending LogCompaction
    resetPendingLogCompactionOperations(CompactionUtils.getAllPendingLogCompactionOperations(metaClient).values().stream()
        .map(e -> Pair.of(e.getKey(), CompactionOperation.convertFromAvroRecordInstance(e.getValue()))));
    //Bootstrap
    resetBootstrapBaseFileMapping(Stream.empty());
    //PendingClustering
    resetFileGroupsInPendingClustering(ClusteringUtils.getAllFileGroupsInPendingClusteringPlans(metaClient));
  }

getPendingCompactionOperations

List the statuses of the files/directories in the given path if the path is a directory.
Does not guarantee to return the List of files/directories status in a sorted order.
org.apache.hudi.common.fs.HoodieWrapperFileSystem.listStatus(org.apache.hadoop.fs.Path)

Adds the provided statuses into the file system view, and also caches it inside this object.
```
### 使用
SliceViewWithLatestSlice
    /**
     * Stream all the latest file slices in the given partition.
     */
    Stream<FileSlice> getLatestFileSlices(String partitionPath);
// Methods to only access latest version of file-slice for the instant(s) passed.   
org.apache.hudi.common.table.view.AbstractTableFileSystemView
```
  @Override
  public final Stream<FileSlice> getLatestFileSlices(String partitionStr) {
    try {
      readLock.lock();
      String partitionPath = formatPartitionKey(partitionStr);
      ensurePartitionLoadedCorrectly(partitionPath);
      return fetchLatestFileSlices(partitionPath)
          .filter(slice -> !isFileGroupReplaced(slice.getFileGroupId()))
          .flatMap(slice -> this.filterBaseFileAfterPendingCompaction(slice, true))
          .map(this::addBootstrapBaseFileIfPresent);
    } finally {
      readLock.unlock();
    }
  }

```
org.apache.hudi.common.table.view.AbstractTableFileSystemView.ensurePartitionLoadedCorrectly
//获取所有的文件 
FileStatus[] statuses = listPartition(partitionPath);
//用于缓存的结果 
List<HoodieFileGroup> groups = addFilesToView(statuses);

##### 
```
    List<HoodieFileGroup> fileGroups = buildFileGroups(statuses, visibleCommitsAndCompactionTimeline, true);

```


### 远程使用
ViewHandler
org.apache.hudi.timeline.service.RequestHandler.ViewHandler.handle
```
    @Override
    public void handle(@NotNull Context context) throws Exception {
      boolean success = true;
      long beginTs = System.currentTimeMillis();
      boolean synced = false;
      //执行刷新
      boolean refreshCheck = performRefreshCheck && !isRefreshCheckDisabledInQuery(context);
      long refreshCheckTimeTaken = 0;
      long handleTimeTaken = 0;
      long finalCheckTimeTaken = 0;
      try {
        if (refreshCheck) {
          long beginRefreshCheck = System.currentTimeMillis();
          synced = syncIfLocalViewBehind(context);
          long endRefreshCheck = System.currentTimeMillis();
          refreshCheckTimeTaken = endRefreshCheck - beginRefreshCheck;
        }

        long handleBeginMs = System.currentTimeMillis();
        handler.handle(context);
        long handleEndMs = System.currentTimeMillis();
        handleTimeTaken = handleEndMs - handleBeginMs;

        if (refreshCheck) {
          long beginFinalCheck = System.currentTimeMillis();
          if (isLocalViewBehind(context)) {
            String lastKnownInstantFromClient = context.queryParamAsClass(RemoteHoodieTableFileSystemView.LAST_INSTANT_TS, String.class).getOrDefault(HoodieTimeline.INVALID_INSTANT_TS);
            String timelineHashFromClient = context.queryParamAsClass(RemoteHoodieTableFileSystemView.TIMELINE_HASH, String.class).getOrDefault("");
            HoodieTimeline localTimeline =
                viewManager.getFileSystemView(context.queryParam(RemoteHoodieTableFileSystemView.BASEPATH_PARAM)).getTimeline();
            if (shouldThrowExceptionIfLocalViewBehind(localTimeline, timelineHashFromClient)) {
              String errMsg =
                  "Last known instant from client was "
                      + lastKnownInstantFromClient
                      + " but server has the following timeline "
                      + localTimeline.getInstants();
              throw new BadRequestResponse(errMsg);
            }
          }
          long endFinalCheck = System.currentTimeMillis();
          finalCheckTimeTaken = endFinalCheck - beginFinalCheck;
        }
      } catch (RuntimeException re) {
        success = false;
        if (re instanceof BadRequestResponse) {
          LOG.warn("Bad request response due to client view behind server view. " + re.getMessage());
        } else {
          LOG.error("Got runtime exception servicing request " + context.queryString(), re);
        }
        throw re;
      } finally {
        long endTs = System.currentTimeMillis();
        long timeTakenMillis = endTs - beginTs;
        metricsRegistry.add("TOTAL_API_TIME", timeTakenMillis);
        metricsRegistry.add("TOTAL_REFRESH_TIME", refreshCheckTimeTaken);
        metricsRegistry.add("TOTAL_HANDLE_TIME", handleTimeTaken);
        metricsRegistry.add("TOTAL_CHECK_TIME", finalCheckTimeTaken);
        metricsRegistry.add("TOTAL_API_CALLS", 1);

        LOG.debug(String.format(
            "TimeTakenMillis[Total=%d, Refresh=%d, handle=%d, Check=%d], "
                + "Success=%s, Query=%s, Host=%s, synced=%s",
            timeTakenMillis, refreshCheckTimeTaken, handleTimeTaken, finalCheckTimeTaken, success,
            context.queryString(), context.host(), synced));
      }
    }


  /**
   * Determines if local view of table's timeline is behind that of client's view.
   */
  private boolean isLocalViewBehind(Context ctx) {
    String basePath = ctx.queryParam(RemoteHoodieTableFileSystemView.BASEPATH_PARAM);
//""    
String lastKnownInstantFromClient =
        ctx.queryParamAsClass(RemoteHoodieTableFileSystemView.LAST_INSTANT_TS, String.class).getOrDefault(HoodieTimeline.INVALID_INSTANT_TS);
   //""  
    String timelineHashFromClient = ctx.queryParamAsClass(RemoteHoodieTableFileSystemView.TIMELINE_HASH, String.class).getOrDefault("");
    HoodieTimeline localTimeline =
        viewManager.getFileSystemView(basePath).getTimeline().filterCompletedOrMajorOrMinorCompactionInstants();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Client [ LastTs=" + lastKnownInstantFromClient + ", TimelineHash=" + timelineHashFromClient
          + "], localTimeline=" + localTimeline.getInstants());
    }
//假设已经初始化了?
//如果没有instant而且 当前是无效的,返回false
    if ((!localTimeline.getInstantsAsStream().findAny().isPresent())
        && HoodieTimeline.INVALID_INSTANT_TS.equals(lastKnownInstantFromClient)) {
      return false;
    }
    //
    String localTimelineHash = localTimeline.getTimelineHash();
    // refresh if timeline hash mismatches
    if (!localTimelineHash.equals(timelineHashFromClient)) {
      return true;
    }

    // As a safety check, even if hash is same, ensure instant is present
    return !localTimeline.containsOrBeforeTimelineStarts(lastKnownInstantFromClient);
  }
```

table 中包含了FileSystemViewManager
```
public abstract class HoodieTable<T, I, K, O> implements Serializable {

  private static final Logger LOG = LogManager.getLogger(HoodieTable.class);

  protected final HoodieWriteConfig config;
  private synchronized FileSystemViewManager getViewManager() {
    if (null == viewManager) {
      viewManager = FileSystemViewManager.createViewManager(getContext(), config.getMetadataConfig(), config.getViewStorageConfig(), config.getCommonConfig(), () -> metadata);
    }
    return viewManager;
  }
```
#### question 
if a file in partition path,but not have Instant,the view will read1 it ?
https://issues.apache.org/jira/browse/HUDI-1138
question from above.