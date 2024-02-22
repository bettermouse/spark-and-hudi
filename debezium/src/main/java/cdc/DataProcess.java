package cdc;

import io.debezium.data.Envelope;
import io.debezium.relational.TableId;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cdc.RecordUtils.SCHEMA_CHANGE_EVENT_KEY_NAME;
import static io.debezium.connector.AbstractSourceInfo.DATABASE_NAME_KEY;
import static io.debezium.connector.AbstractSourceInfo.TABLE_NAME_KEY;

public class DataProcess<T> {
    private static final Logger LOG = LoggerFactory.getLogger(DataProcess.class);



    private Map<TableId,List<BatchUpdate<T>>> resultMap = new HashMap<>();


    /**
     * 处理事件
     */
    public void processEvent(SourceRecord sourceRecord){

        //schema变更
        if(isSchemaChangeEvent(sourceRecord)){
            //获取ddl语句
        }else if(isDataChangeRecord(sourceRecord)){
            //获取数据
            Struct value = (Struct)sourceRecord.value();
            Struct source = value.getStruct(Envelope.FieldName.SOURCE);
            String db = source.getString("db");
            String table = source.getString("table");

            TableId tableId = new TableId(db, null, table);
            List<BatchUpdate<T>> batchUpdates = resultMap.get(tableId);
            if(batchUpdates==null){
                batchUpdates = new ArrayList<>();
                resultMap.put(tableId,batchUpdates);
            }
            //获取最后一个是数据的批
            BatchUpdate<T> addList = batchUpdates.get(batchUpdates.size() - 1);
            if(addList.getType()== BatchUpdate.UPDATE_TYPE.SQL){

            }else{
                //如何把数据添加进去
                //如何将这一堆变量传过去呢
               // addList.add();
            }


            System.out.println(1);
        } else {
            // unknown element
            LOG.info("Meet unknown element {}, just skip.", sourceRecord);
        }

    }

    public static boolean isSchemaChangeEvent(SourceRecord sourceRecord) {
        Schema keySchema = sourceRecord.keySchema();
        if (keySchema != null && SCHEMA_CHANGE_EVENT_KEY_NAME.equalsIgnoreCase(keySchema.name())) {
            return true;
        }
        return false;
    }

    public static boolean isDataChangeRecord(SourceRecord record) {
        Schema valueSchema = record.valueSchema();
        Struct value = (Struct) record.value();
        return valueSchema.field(Envelope.FieldName.OPERATION) != null
                && value.getString(Envelope.FieldName.OPERATION) != null;
    }


    public T generateRow(SourceRecord sourceRecord){
        return null;
    }

    //根据
    public static void generateGetCode(Struct source,String s){
        List<Field> fields = source.schema().fields();
        for(Field f :fields){
            if(f.schema().equals(Schema.STRING_SCHEMA)){
                System.out.println(String.format("String %s = %s.getString(\"%s\")",f.name(),
                        s,f.name()));
            }else if(f.schema().equals(Schema.INT64_SCHEMA)){
                System.out.println(String.format("Long %s = %s.getInt64(\"%s\")",f.name(),
                        s,f.name()));
            }else{
                System.out.println(String.format("Struct %s = %s.getStruct(\"%s\")",f.name(),
                        s,f.name()));

            }

        }
    }

}
