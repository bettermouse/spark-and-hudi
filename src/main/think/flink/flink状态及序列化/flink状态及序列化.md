# flink type 
// TypeInformation可以通过  TypeInformation.of
TypeInformation<HoodieRecord> of = TypeInformation.of(HoodieRecord.class);
// 也可以通过 types
import org.apache.flink.api.common.typeinfo.Types;
TypeInformation<Fvp> pojo1 = Types.POJO(Fvp.class);
```
//这个是我对flink typeinformat的理解
TypeInformation<ColdWaybillDetail> x =null;
TypeSerializer<ColdWaybillDetail> serializer = x.createSerializer(null);
TypeSerializerSnapshot<ColdWaybillDetail> coldWaybillDetailTypeSerializerSnapshot = serializer.snapshotConfiguration();
coldWaybillDetailTypeSerializerSnapshot.resolveSchemaCompatibility()
```

# Data Types & Serialization
 containing its own type descriptors, generic type extraction, and type serialization framework. This document describes the concepts and the rationale behind them.
 
 Java Tuples and Scala Case Classes
 Java POJOs(public,默认构造参数,get/set,字段是支持的序列化类型),using Kryo as configurable fallback,
    AvroTypeInfo 
org.apache.flink.types.PojoTestUtils#assertSerializedAsPojo() from the flink-test-utils.
 If you additionally want to ensure that no field of the POJO will be serialized with Kryo, 
 use assertSerializedAsPojoWithoutKryo() instead.
    
 Primitive Types(Integer, String, and Double.)
 Regular Classes
 Values
 Hadoop Writables
 Special Types