#  Hudi supports more comprehensive Schema Evolution
## Abstract
 support Hudi's evolution capability more comprehensively.
## Background
## Features
add/drop/rename/update/reorder


## Overall design

# code
https://issues.apache.org/jira/browse/HUDI-2560

// only for schema save
String SCHEMA_COMMIT_ACTION = "schemacommit";
## InternalSchemaMerger


https://issues.apache.org/jira/browse/HUDI-2429
InternalSchema 是用来做schema evolution的实体类
//不允许删除schema
  public static final ConfigProperty<String> SCHEMA_ALLOW_AUTO_EVOLUTION_COLUMN_DROP = ConfigProperty
      .key("hoodie.datasource.write.schema.allow.auto.evolution.column.drop")
      .defaultValue("false")
      .sinceVersion("0.13.0")
      .withDocumentation("Controls whether table's schema is allowed to automatically evolve when "
          + "incoming batch's schema can have any of the columns dropped. By default, Hudi will not "
          + "allow this kind of (auto) schema evolution. Set this config to true to allow table's "
          + "schema to be updated automatically when columns are dropped from the new incoming batch.");