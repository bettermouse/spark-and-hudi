package spark;

import cdc.DataProcess;
import io.debezium.data.Envelope;
import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparkDataProcess extends DataProcess<InternalRow> {

    //准备一下序列化器

    Map<TableId,String> map = new HashMap<>();

    TableChanges.TableChange tableChange;

    @Override
    public InternalRow generateRow(SourceRecord sourceRecord) {
        //获取数据
        Struct value = (Struct)sourceRecord.value();
        //获取before after operation
        String operation = value.getString(Envelope.FieldName.OPERATION);
        Struct before = value.getStruct(Envelope.FieldName.BEFORE);
        Struct after = value.getStruct(Envelope.FieldName.AFTER);
        Struct source = value.getStruct(Envelope.FieldName.SOURCE);
        Long timestamp = value.getInt64(Envelope.FieldName.TIMESTAMP);
        Struct transaction = value.getStruct(Envelope.FieldName.TRANSACTION);
        // source 数据拆解


        //todo first 先用json, second尝试用一些内部类
        Long ts_ms = source.getInt64("ts_ms");
        String db = source.getString("db");
        String table = source.getString("table");
        String gtid = source.getString("gtid");
        String file = source.getString("file");
//      Long pos = source.getInt64("pos");
//      Struct row = source.getStruct("row");
//      Struct thread = source.getStruct("thread");
//      Struct query = source.getStruct("query");
        //在spark jdbc rdd中

        //先全部 取删除后的数据
        int i1 = tableChange.getTable().columns().size() + 5;
        GenericInternalRow row = new GenericInternalRow(i1);
        List<Field> fields = after.schema().fields();
        for(int i =0;i<fields.size();i++){
            Object o = after.get(fields.get(i));
            row.update(i,o);
        }
        return row;
    }

    public static void main(String[] args) {

    }
}
