# RowEncoder
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
}

Row 转成InternalRow


# Encoder
Used to convert a JVM object of type T to and from the internal Spark SQL representation.
org.apache.spark.sql.Encoder