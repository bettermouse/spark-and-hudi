## RFC - 03 : Timeline Service with Incremental File System View Syncing 
https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=113708965

## Abstract

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
when compaction create,log file will add to new fileslice
pending compaction
https://github.com/apache/hudi/pull/3703
```
  /**
   * Get the commit + pending-compaction timeline visible for this table. A RT filesystem view is constructed with this
   * timeline so that file-slice after pending compaction-requested instant-time is also considered valid. A RT
   * file-system view for reading must then merge the file-slices before and after pending compaction instant so that
   * all delta-commits are read.
   */
//获取  commit + pending-compaction timeline可见性对这个表
//
  public HoodieTimeline getCommitsAndCompactionTimeline() {
    switch (this.getTableType()) {
      case COPY_ON_WRITE:
        return getActiveTimeline().getCommitTimeline();
      case MERGE_ON_READ:
        return getActiveTimeline().getWriteTimeline();
      default:
        throw new HoodieException("Unsupported table type :" + this.getTableType());
    }
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