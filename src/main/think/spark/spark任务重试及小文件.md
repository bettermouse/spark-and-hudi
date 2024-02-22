## 参数
spark.task.maxFailures
任务重试次数,本地的默认为1
### 结合hudi 来看
spark 重试生成的文件在hudi任务成功后并不会被删除
org.apache.hudi.common.table.view.AbstractTableFileSystemView.buildFileGroups(org.apache.hadoop.fs.FileStatus[], org.apache.hudi.common.table.timeline.HoodieTimeline, boolean)

file group (file slices with instant)
HoodieFileGroup
```
  /**
   * Slices of files in this group, sorted with greater commit first.
   */
  private final TreeMap<String, FileSlice> fileSlices;

```

spark.stage.maxConsecutiveAttempts	
