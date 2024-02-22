# HoodieRecord
HoodieAvroRecord 是Hudi的默认处理方式,sparK row 如何转成对应的数据呢?
```
    recordType match {
      case HoodieRecord.HoodieRecordType.AVRO =>
        val avroRecords: RDD[GenericRecord] = HoodieSparkUtils.createRdd(df, recordName, recordNameSpace,
          Some(writerSchema))

        avroRecords.mapPartitions(it => {
          val dataFileSchema = new Schema.Parser().parse(dataFileSchemaStr)
          val consistentLogicalTimestampEnabled = parameters.getOrElse(
            DataSourceWriteOptions.KEYGENERATOR_CONSISTENT_LOGICAL_TIMESTAMP_ENABLED.key(),
            DataSourceWriteOptions.KEYGENERATOR_CONSISTENT_LOGICAL_TIMESTAMP_ENABLED.defaultValue()).toBoolean

          it.map { avroRecord =>
            val processedRecord = if (shouldDropPartitionColumns) {
              HoodieAvroUtils.rewriteRecord(avroRecord, dataFileSchema)
            } else {
              avroRecord
            }
            val hoodieRecord = if (shouldCombine) {
              val orderingVal = HoodieAvroUtils.getNestedFieldVal(avroRecord, config.getString(PRECOMBINE_FIELD),
                false, consistentLogicalTimestampEnabled).asInstanceOf[Comparable[_]]
              DataSourceUtils.createHoodieRecord(processedRecord, orderingVal, keyGenerator.getKey(avroRecord),
                config.getString(PAYLOAD_CLASS_NAME))
            } else {
              DataSourceUtils.createHoodieRecord(processedRecord, keyGenerator.getKey(avroRecord),
                config.getString(PAYLOAD_CLASS_NAME))
            }
            hoodieRecord
          }
        }).toJavaRDD()
```

1.转成GenericRecord
2.生成HoodieRecord