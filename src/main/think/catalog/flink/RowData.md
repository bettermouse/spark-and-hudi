# RowData
接口,内部数据结构来表示dataType/StructuredType 在表系统中
table api/sql pipeline 都是通过这个接口
RowKind 描述在cdc中
BinaryRowData用MemorySegment 来减少serialization/deserialization 负载
GenericRowData 是用java的object用来高效的创建和修改

GenericRowData是稳定的,如果需要使用这个类即可
## schema DataType
schema 应该是全表的
org.apache.flink.connector.jdbc.table.JdbcDynamicTableSource#getScanRuntimeProvider
``` 
        builder.setQuery(query);
        final RowType rowType = (RowType) physicalSchema.toRowDataType().getLogicalType();
        builder.setRowConverter(dialect.getRowConverter(rowType));
        builder.setRowDataTypeInfo(
                runtimeProviderContext.createTypeInformation(physicalSchema.toRowDataType()));

        return InputFormatProvider.of(builder.build());
    }

```

##  MySQLDialect getRowConverter
```
builder.setRowConverter(dialect.getRowConverter(rowType));

    @Override
    public RowData nextRecord(RowData reuse) throws IOException {
        try {
            if (!hasNext) {
                return null;
            }
            RowData row = rowConverter.toInternal(resultSet);
            // update hasNext after we've read1 the record
            hasNext = resultSet.next();
            return row;
        } catch (SQLException se) {
            throw new IOException("Couldn't read1 data - " + se.getMessage(), se);
        } catch (NullPointerException npe) {
            throw new IOException("Couldn't access resultSet", npe);
        }
    }


    @Override
    public RowData toInternal(ResultSet resultSet) throws SQLException {
        GenericRowData genericRowData = new GenericRowData(rowType.getFieldCount());
        for (int pos = 0; pos < rowType.getFieldCount(); pos++) {
            Object field = resultSet.getObject(pos + 1);
            genericRowData.setField(pos, toInternalConverters[pos].deserialize(field));
        }
        return genericRowData;
    }
```