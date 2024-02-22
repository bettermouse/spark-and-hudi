# catalog
对于数据源,sql 总是需要将其转换成内部数据源,转化成内部数据源,需要schema,和将原始数据转化成
row的方法
## flink
AbstractCatalog 
org.apache.flink.connector.jdbc.catalog.MySqlCatalog#getTable
###  CatalogBaseTable
包含了schema 和
props.put(CONNECTOR.key(), IDENTIFIER);

###  DynamicTableSourceFactory DynamicTableSinkFactory
```
@Internal
public class JdbcDynamicTableFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    public static final String IDENTIFIER = "jdbc";
```

