## 使用
org.apache.hudi.io.HoodieWriteHandle
when writing,create a marker file.
```
  /**
   * Creates an empty marker file corresponding to storage writer path.
   *
   * @param partitionPath Partition path
   */
  protected void createMarkerFile(String partitionPath, String dataFileName) {
    WriteMarkersFactory.get(config.getMarkersType(), hoodieTable, instantTime)
        .create(partitionPath, dataFileName, getIOType(), config, fileId, hoodieTable.getMetaClient().getActiveTimeline());
  }
```
find delete by log
[INFO ] 2023-03-16 14:11:35,333 method:org.apache.hudi.table.marker.TimelineServerBasedWriteMarkers.executeRequestToTimelineServer(TimelineServerBasedWriteMarkers.java:168)
Sending request : (http://SF0001407273A.sf.com:55953/v1/hoodie/marker/dir/delete?markerdirpath=file%3A%2FG%3A%2Fhuditmp%2FhudiMOr%2F.hoodie%2F.temp%2F20230316141134281)

find register place
```
    app.post(MarkerOperation.DELETE_MARKER_DIR_URL, new ViewHandler(ctx -> {
      metricsRegistry.add("DELETE_MARKER_DIR", 1);
      boolean success = markerHandler.deleteMarkers(
          ctx.queryParamAsClass(MarkerOperation.MARKER_DIR_PATH_PARAM, String.class).getOrDefault(""));
      writeValueAsString(ctx, success);
    }, false));
```
spark retry task will not delete.


https://issues.apache.org/jira/browse/HUDI-839