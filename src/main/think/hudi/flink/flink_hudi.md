# Support schema evolution in Flink CDC writer
https://issues.apache.org/jira/browse/HUDI-2577
https://blog.csdn.net/qq_31922231/article/details/121726972

sql,对于schema evolution简直是一个灾难,

# HoodieFlinkStreamer
# flink 中传递的对象
EventTimeAvroPayload
Nishith Agarwal* 2018/11/5 8:03 Serializing the complete payload object instead of serializing just the GenericRecord Removing Converter hierarchy as we now depend purely on JavaSerialization and require the payload to be java serializable
## 算子 bucket_assigner
```
          .transform(
              "bucket_assigner",
              TypeInformation.of(HoodieRecord.class),
              new KeyedProcessOperator<>(new BucketAssignFunction<>(conf)))
          .uid(opUID("bucket_assigner", conf))
          .setParallelism(conf.getInteger(FlinkOptions.BUCKET_ASSIGN_TASKS))
```
## 算子 stream_write
```
          // shuffle by fileId(bucket id)
          .keyBy(record -> record.getCurrentLocation().getFileId())
          .transform(opName("stream_write", conf), TypeInformation.of(Object.class), operatorFactory)
          .uid(opUID("stream_write", conf))
          .setParallelism(conf.getInteger(FlinkOptions.WRITE_TASKS));
```


###  stream_write 具体写数据的操作(新) StreamWriteFunction
Sink function to write the data to the underneath filesystem.
Work Flow
The function firstly buffers the data as a batch of HoodieRecords, It flushes(write) the records batch when the batch size exceeds the configured size FlinkOptions.WRITE_BATCH_SIZE or the total buffer size exceeds the configured size FlinkOptions.WRITE_TASK_MAX_SIZE or a Flink checkpoint starts. After a batch has been written successfully, 
the function notifies its operator coordinator StreamWriteOperatorCoordinator to mark a successful write.
当一批数据写完的时候,通知StreamWriteOperatorCoordinator数据写完了.
The Semantics
The task implements exactly-once semantics by buffering the data between checkpoints. The operator coordinator starts a new instant on the timeline when a checkpoint triggers, the coordinator checkpoints always start before its operator, so when this function starts a checkpoint, a REQUESTED instant already exists.

The function process thread blocks data buffering after the checkpoint thread finishes flushing the existing data buffer until the current checkpoint succeed and the coordinator starts a new instant. Any error triggers the job failure during the metadata committing, when the job recovers from a failure, the write function re-send the write metadata to the coordinator to see if these metadata can re-commit, thus if unexpected error happens during the instant committing, the coordinator would retry to commit when the job recovers.
Fault Tolerance
The operator coordinator checks and commits the last instant then starts a new one after a checkpoint finished successfully. It rolls back any inflight instant before it starts a new instant, this means one hoodie instant only span one checkpoint, the write function blocks data buffer flushing for the configured checkpoint timeout before it throws exception, any checkpoint failure would finally trigger the job failure.
Note: The function task requires the input stream be shuffled by the file IDs.

snapshotState 写所有的数据,并发送给 coordinator 

### StreamWriteOperatorCoordinator
This coordinator starts a new instant when a new checkpoint starts. 
It commits the instant when all the operator tasks write the buffer successfully for a round of checkpoint.
If there is no data for a round of checkpointing, it resets the events buffer and returns early.

当一个新的checkpoint开启,开启一个新的instant




当所有的 operator tasks write the buffer successfully (checkpoint的时候) 提交
如果没有数据,结束这一轮

# web 依赖
```
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-runtime-web_2.12</artifactId>
            <version>${flink.version}</version>
            <scope>test</scope>
        </dependency>
```

# flink 使用了 InputFormatSourceFunction
InputFormat 这种落后的 sourceApi
需要了解一下这个具体的原理 
org.apache.flink.runtime.jobgraph.tasks.InputSplitProvider

InputFormat 仅仅是作者封装了读取的逻辑,其实没什么用

```
} else {
  InputFormatSourceFunction<RowData> func = new InputFormatSourceFunction<>(getInputFormat(), typeInfo);
  DataStreamSource<RowData> source = execEnv.addSource(func, asSummaryString(), typeInfo);
  return source.name(getSourceOperatorName("bounded_source")).setParallelism(conf.getInteger(FlinkOptions.READ_TASKS));
}
```
它没有与InputFormatSourceFunction 一同使用