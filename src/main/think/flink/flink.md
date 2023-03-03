# flink 本地模式设置 savepoint
## 方法一
```
jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath("G:\\flinkState\\savepoint-829d9b-06c0b51011ef",true));
```
## 方法二
https://blog.csdn.net/yy8623977/article/details/125659249
CliFrontend

使用PackagedProgram封装Flink程序,然后构建JobGraph,提交Flink集群
https://blog.csdn.net/czladamling/article/details/125204087
```
//{rest.port=9999, execution.target=local, execution.attached=true, parallelism.default=12}
//这个是flink 本地的默认端口
  def main(args: Array[String]): Unit = {
    //设置必要的属性,包括taskManager数量,slot之类
    val flinkConfig = new Configuration()
    //如果绑定端口0,其实就是随便绑定一个端口
    flinkConfig.setInteger(RestOptions.PORT, 9999);
    val miniClusterConfig: MiniClusterConfiguration =
      new MiniClusterConfiguration.Builder()
        .setConfiguration(flinkConfig)
        .setNumTaskManagers(3)
        .setNumSlotsPerTaskManager(1)
        .build()
    val cluster = new MiniCluster(miniClusterConfig)
    cluster.start()
  }
```


# other

## StreamTask
org.apache.flink.streaming.runtime.tasks.StreamTask
org.apache.flink.runtime.operators.BatchTask

```
org.apache.flink.streaming.runtime.tasks.StreamTask#executeRestore
这里面有状态恢复的代码
org.apache.flink.streaming.runtime.tasks.StreamTask#restoreGates
```

## org.apache.flink.runtime.taskmanager.Task
