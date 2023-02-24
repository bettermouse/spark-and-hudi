# rdd 和 kyro 序列化
https://spark.apache.org/docs/latest/tuning.html
The only reason Kryo is not the default is because of the custom registration requirement

在spark sql中
org.apache.spark.rdd.ShuffledRDD.getDependencies
这里可以看到序列化是自己去获取的
```
  override def getDependencies: Seq[Dependency[_]] = {
    val serializer = userSpecifiedSerializer.getOrElse {
      val serializerManager = SparkEnv.get.serializerManager
      if (mapSideCombine) {
        serializerManager.getSerializer(implicitly[ClassTag[K]], implicitly[ClassTag[C]])
      } else {
        serializerManager.getSerializer(implicitly[ClassTag[K]], implicitly[ClassTag[V]])
      }
    }
    List(new ShuffleDependency(prev, part, serializer, keyOrdering, aggregator, mapSideCombine))
  }
```

```
  /**
   * A [[ShuffleDependency]] that will partition rows of its child based on
   * the partitioning scheme defined in `newPartitioning`. Those partitions of
   * the returned ShuffleDependency will be the input of shuffle.
   */
  @transient
  lazy val shuffleDependency : ShuffleDependency[Int, InternalRow, InternalRow] = {
    val dep = ShuffleExchangeExec.prepareShuffleDependency(
      inputRDD,
      child.output,
      outputPartitioning,
      serializer,
      writeMetrics)
    metrics("numPartitions").set(dep.partitioner.numPartitions)
    val executionId = sparkContext.getLocalProperty(SQLExecution.EXECUTION_ID_KEY)
    SQLMetrics.postDriverMetricUpdates(
      sparkContext, executionId, metrics("numPartitions") :: Nil)
    dep
  }

```
在spark rdd中使用  
sparkContext.getConf.registerKryoClasses(
        Array(classOf[GenericData],
          classOf[Schema]))
          
          
## spark中的组件 
### SerializerManager
默认为java 序列化

 --conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' 