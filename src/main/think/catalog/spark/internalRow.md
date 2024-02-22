# InternalRow
## UnsafeRow
[null-tracking bit set] [values] [variable length portion]

### Generic row

# DatasourceV1
```

trait RelationProvider {
  /**
   * Returns a new base relation with the given parameters.
   *
   * @note The parameters' keywords are case insensitive and this insensitivity is enforced
   * by the Map that is passed to the function.
   */
  def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation
}

@Stable
trait PrunedFilteredScan {
  def buildScan(requiredColumns: Array[String], filters: Array[other.Filter]): RDD[Row]
}


/**
 * @since 1.3.0
 */
@Stable
trait CreatableRelationProvider {
  /**
   * Saves a DataFrame to a destination (using data source-specific parameters)
   *
   * @param sqlContext SQLContext
   * @param mode specifies what happens when the destination already exists
   * @param parameters data source-specific parameters
   * @param data DataFrame to save (i.e. the rows after executing the query)
   * @return Relation with a known schema
   *
   * @since 1.3.0
   */
  def createRelation(
      sqlContext: SQLContext,
      mode: SaveMode,
      parameters: Map[String, String],
      data: DataFrame): BaseRelation
}
```

## SpecificInternalRow
## GenericInternalRow


# 在spark 世界里面对象
java 对象
InternalRow
Row
Rdd 
## RowEncoder
RowEncoder
A factory for constructing encoders that convert external row 
to/from the Spark SQL internal binary representation.