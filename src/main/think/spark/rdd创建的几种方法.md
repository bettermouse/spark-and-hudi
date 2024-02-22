# 创建 dataframe的几种方式
With a SparkSession, 
1 .applications can create DataFrames from an existing RDD, 
2 .from a Hive table, or from Spark data sources.

Dataset<Row> peopleDF = spark.createDataFrame(peopleRDD, Person.class);


## 
```
  /**
   * :: DeveloperApi ::
   * Creates a `DataFrame` from a `JavaRDD` containing [[Row]]s using the given schema.
   * It is important to make sure that the structure of every [[Row]] of the provided RDD matches
   * the provided schema. Otherwise, there will be runtime exception.
   *
   * @since 2.0.0
   */
  @DeveloperApi
  def createDataFrame(rowRDD: JavaRDD[Row], schema: StructType): DataFrame = {
    val replaced = CharVarcharUtils.failIfHasCharVarchar(schema).asInstanceOf[StructType]
    createDataFrame(rowRDD.rdd, replaced)
  }
```

val peopleDF = spark.createDataFrame(rowRDD, schema)
# 创建rdd的几种方式
There are two ways to create RDDs: 
1. parallelizing an existing collection in your driver program, 
```
List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
JavaRDD<Integer> distData = sc.parallelize(data);
```

2. or referencing a dataset in an external storage system, 
such as a shared filesystem, XX, HBase, 
or any data source offering a Hadoop InputFormat.

# spark jdbc rdd
```
org.apache.spark.sql.execution.datasources.DataSourceStrategy.toCatalystRDD
  /**
   * Convert RDD of Row into RDD of InternalRow with objects in catalyst types
   */
  private[sql] def toCatalystRDD(
      relation: BaseRelation,
      output: Seq[other.Attribute],
      rdd: RDD[Row]): RDD[InternalRow] = {
    if (relation.needConversion) {
      val toRow = RowEncoder(StructType.fromAttributes(output), lenient = true).createSerializer()
      rdd.mapPartitions { iterator =>
        iterator.map(toRow)
      }
    } else {
      rdd.asInstanceOf[RDD[InternalRow]]
    }
  }
```