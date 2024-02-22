# StreamExecutionEnvironment

# StreamTransformation

## DataStream
A DataStream represents a stream of elements of the same type. 
A DataStream can be transformed into another DataStream by applying a transformation as for example:
DataStream#map
Transformation

## Transformation
里面有一个operator
### PhysicalTransformation

## StreamOperator  operator
### StreamOperatorFactory
由于operator需要在taskManager端创建,所以通过工场方法创建是必要的
常用的方法是
SimpleOperatorFactory.of(operator)
### CoordinatedOperatorFactory
OperatorEventHandler

如果想自定义operator,可以自定义工场方法.
```
    WriteOperatorFactory<RowData> operatorFactory = AppendWriteOperator.getFactory(conf, rowType);
    return dataStream
        .transform(opName("hoodie_append_write", conf), TypeInformation.of(Object.class), operatorFactory)
        .uid(opUID("hoodie_stream_write", conf))
        .setParallelism(conf.getInteger(FlinkOptions.WRITE_TASKS));
```
operator中可以获取那些组件?
```
    /** Create the operator. Sets access to the context and the output. */
    <T extends StreamOperator<OUT>> T createStreamOperator(
            StreamOperatorParameters<OUT> parameters);
```
AbstractStreamOperatorFactory

#### AbstractStreamOperatorFactoryAdapter
在hudi中,通过 该类实现YieldingOperatorFactory.


## task 生命周期
```
    // 初始化阶段
    OPERATOR::setup
        UDF::setRuntimeContext
    OPERATOR::initializeState
    OPERATOR::open
        UDF::open
    // 处理阶段（对每个 element 或 watermark 调用）
    OPERATOR::processElement
        UDF::run
    OPERATOR::processWatermark
    // checkpointing 阶段（对每个 checkpoint 异步调用）
    OPERATOR::snapshotState
    // 通知 operator 处理记录的过程结束
    OPERATOR::finish
    // 结束阶段
    OPERATOR::close
        UDF::close
```

## 流读文件 
https://issues.apache.org/jira/browse/FLINK-1687
只是想在流中加入有限流.sink的时候通过close方法,可以写入全部数据.
org.apache.flink.api.common.functions.AbstractRichFunction#close