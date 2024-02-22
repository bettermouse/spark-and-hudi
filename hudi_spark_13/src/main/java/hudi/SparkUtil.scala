package hudi

import org.apache.spark.sql.DataFrame

object SparkUtil {
  def setNullableStateForAllColumns( df: DataFrame) : DataFrame = {
    import org.apache.spark.sql.types.{StructField, StructType}
    // get schema
    val schema = df.schema
    val newSchema = StructType(schema.map {
      case StructField( c, d, n, m) =>
        StructField( c, d, true, m)
    })
    // apply new schema
    df.sqlContext.createDataFrame( df.rdd, newSchema )
  }
}
