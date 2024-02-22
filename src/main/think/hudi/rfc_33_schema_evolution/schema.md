# schema 相关的类
## TableSchemaResolver


## 读取时会自动加上hudi元数据
org.apache.hudi.avro.HoodieAvroUtils.addMetadataFields(org.apache.avro.Schema, boolean)
需要加上的元数据
binlog+position,update_time/modify_time 
gtid


## schema 变更
### 基本的
such as adding a nullable field or promoting a datatype of a field
### RECONCILE_SCHEMA
https://issues.apache.org/jira/browse/HUDI-4588
在0.11的时候报错
https://issues.apache.org/jira/browse/HUDI-4588
Ingestion failing if source column is dropped
如果是子集,不删除列  否则,以新的schema为准
### schema.on.read1 
先不考虑.在发生了schema 变更,如何去组织数据呢
b1-->增加-->b2-->减少-->b3--->b4---


