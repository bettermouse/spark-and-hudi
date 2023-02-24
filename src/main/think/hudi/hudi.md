# maven插件 方便单元测试
maven heaper

# 引入bundle包,无法debug
https://www.jianshu.com/p/d5de94e916ae
还未解决

https://stackoverflow.com/questions/55309684/does-maven-shade-plugin-work-with-scala-classes
这个似乎是idea的Bug

# maven执行一个main函数
mvn -Dspark3.2 -Dscala-2.12  exec:java -Dexec.mainClass="org.apache.hudi.spark.bundle.Main" -pl packaging/hudi-spark-bund
le

插件中这个方法可以进入 
exec:java -Dexec.mainClass="org.apache.hudi.spark.bundle.Main"


# 类与测试
在不知道要干什么的时候,需要努力的向前.
## hudi-spark-datasource#hudi-spark
### SchemaConverters
org.apache.spark.sql.avro.SchemaConverters
从spark中拷贝出来的,在avro和spark schema中互相转换
可以为null在spark中nullable =true,avro中为UNION 里面有NUll.
#### TestSchemaConverters

### hudi bootstrap 
bootstrap是指将原本的表变为hudi表
org.apache.hudi.functional.TestBootstrap
