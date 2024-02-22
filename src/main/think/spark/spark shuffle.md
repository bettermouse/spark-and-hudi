## spark shuffle
https://blog.csdn.net/better_mouse/article/details/92795282
https://blog.csdn.net/better_mouse/article/details/92787558

### SortShuffleManager
1.如果分区数小于200,且不做map端聚合 
2.大于200,小于16777215,不做map端聚合,支持序列化对象重定位 SerializedShuffleHandle
3.BaseShuffleHandle
BypassMergeSortShuffleHandle
## spark rdd
在spark中,最基本的是rdd,rdd操作如果涉及shuffle,需要对对象进行序列化和反序列化,
这个操作十分耗时
## spark dataframe/dataset
type DataFrame = Dataset[Row]

a Dataset represents a logical plan that describes
  the computation required to produce the data

Dataset代表的是一系列的logical plan,会被spark优化.
spark.internalCreateDataFrame(rdd, schema)


