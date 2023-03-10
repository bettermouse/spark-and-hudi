#  RFC 15, RFC 27, RFC 45
## HoodieTableMetadata
Interface that supports querying various pieces of metadata about a hudi table.
```

  static HoodieTableMetadata create(HoodieEngineContext engineContext, HoodieMetadataConfig metadataConfig, String datasetBasePath,
                                    String spillableMapPath, boolean reuse) {
    if (metadataConfig.enabled()) {
      return createHoodieBackedTableMetadata(engineContext, metadataConfig, datasetBasePath, spillableMapPath, reuse);
    } else {
      return createFSBackedTableMetadata(engineContext, metadataConfig, datasetBasePath);
    }
  }
```
### FileSystemBackedTableMetadata
基于文件系统的
mplementation of HoodieTableMetadata based file-system-backed table metadata.
### HoodieBackedTableMetadata
Table metadata provided by an internal DFS backed Hudi metadata table.

https://issues.apache.org/jira/browse/HUDI-2013
Fallback to file listing may lead to data loss
```
  /**
   * Return the list of files in a partition.
   * <p>
   * If the Metadata Table is enabled, the listing is retrieved from the stored metadata. Otherwise, the list of
   * partitions is retrieved directly from the underlying {@code FileSystem}.
   * <p>
   * On any errors retrieving the listing from the metadata, defaults to using the file system listings.
   *
   * @param partitionPath The absolute path of the partition to list
   */
  @Override
  public FileStatus[] getAllFilesInPartition(Path partitionPath)
      throws IOException {
    if (isMetadataTableEnabled) {
      try {
        return fetchAllFilesInPartition(partitionPath);
      } catch (Exception e) {
        throw new HoodieMetadataException("Failed to retrieve files in partition " + partitionPath + " from metadata", e);
      }
    }

    FileSystemBackedTableMetadata fileSystemBackedTableMetadata =
        createFileSystemBackedTableMetadata();
    return fileSystemBackedTableMetadata.getAllFilesInPartition(partitionPath);
  }
```



## HoodieTableFileSystemView
### HoodieMetadataFileSystemView
https://github.com/apache/hudi/pull/8079
IncrementalTimelineSyncFileSystemView
// This is the visible active timeline used only for incremental view syncing
private HoodieTimeline visibleActiveTimeline;
where to update  visibleActiveTimeline

reflush timeline,but not refulsh the file.

hoodie.compact.inline.max.delta.commits