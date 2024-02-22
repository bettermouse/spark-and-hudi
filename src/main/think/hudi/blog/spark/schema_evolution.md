
## configuration
### hoodie.schema.on.read1.enable
  public static final ConfigProperty<Boolean> SCHEMA_EVOLUTION_ENABLE = ConfigProperty
      .key("hoodie.schema.on.read1.enable")
      .defaultValue(false)
      .withDocumentation("Enables support for Schema Evolution feature");
  hoodie.schema.on.read1.enable 就是schema envolution 的意思 
###  hoodie.datasource.write.reconcile.schema
当存在表时,表的schema如何选择.当DISABLED,选择写入的schema
启用 schema will kept the same or extended.(existing column  being dropped in a new batch will not lose data)
默认不开启
### hoodie.avro.schema.validate
  public static final ConfigProperty<String> AVRO_SCHEMA_VALIDATE_ENABLE = ConfigProperty
      .key("hoodie.avro.schema.validate")
      .defaultValue("true")
      .withDocumentation("Validate the schema used for the write against the latest schema, for backwards compatibility.");

org.apache.hudi.HoodieSparkSqlWriter.addSchemaEvolutionParameters
```
  def addSchemaEvolutionParameters(parameters: Map[String, String], internalSchemaOpt: Option[InternalSchema], writeSchemaOpt: Option[Schema] = None): Map[String, String] = {
    //第一次/默认 获取不到数据
    val schemaEvolutionEnable = if (internalSchemaOpt.isDefined) "true" else "false"
    //开启且允许合并的时候 不验证
    val schemaValidateEnable = if (schemaEvolutionEnable.toBoolean && parameters.getOrDefault(DataSourceWriteOptions.RECONCILE_SCHEMA.key(), "false").toBoolean) {
      // force disable schema validate, now we support schema evolution, no need to do validate
      "false"
    } else  {
      parameters.getOrDefault(HoodieWriteConfig.AVRO_SCHEMA_VALIDATE_ENABLE.key(), "true")
    }
    // correct internalSchema, internalSchema should contain hoodie metadata columns.
    val correctInternalSchema = internalSchemaOpt.map { internalSchema =>
      if (internalSchema.findField(HoodieRecord.RECORD_KEY_METADATA_FIELD) == null && writeSchemaOpt.isDefined) {
        val allowOperationMetaDataField = parameters.getOrElse(HoodieWriteConfig.ALLOW_OPERATION_METADATA_FIELD.key(), "false").toBoolean
        AvroInternalSchemaConverter.convert(HoodieAvroUtils.addMetadataFields(writeSchemaOpt.get, allowOperationMetaDataField))
      } else {
        internalSchema
      }
    }
    parameters ++ Map(HoodieWriteConfig.INTERNAL_SCHEMA_STRING.key() -> SerDeHelper.toJson(correctInternalSchema.getOrElse(null)),
      HoodieCommonConfig.SCHEMA_EVOLUTION_ENABLE.key() -> schemaEvolutionEnable,
      HoodieWriteConfig.AVRO_SCHEMA_VALIDATE_ENABLE.key()  -> schemaValidateEnable)
  }

```

  public static final ConfigProperty<Boolean> RECONCILE_SCHEMA = ConfigProperty
      .key("hoodie.datasource.write.reconcile.schema")
      .defaultValue(false)
      .withDocumentation("This config controls how writer's schema will be selected based on the incoming batch's "
          + "schema as well as existing table's one. When schema reconciliation is DISABLED, incoming batch's "
          + "schema will be picked as a writer-schema (therefore updating table's schema). When schema reconciliation "
          + "is ENABLED, writer-schema will be picked such that table's schema (after txn) is either kept the same "
          + "or extended, meaning that we'll always prefer the schema that either adds new columns or stays the same. "
          + "This enables us, to always extend the table's schema during evolution and never lose the data (when, for "
          + "ex, existing column is being dropped in a new batch)");
false,以最新的schema为准.
true 对schema进行扩展