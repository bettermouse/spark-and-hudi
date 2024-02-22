package hudi;

import io.debezium.connector.mysql.MySqlConnection;
import io.debezium.relational.Table;
import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hudi.AvroConversionUtils;
import org.apache.hudi.common.model.HoodieRecord;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.jdbc.JdbcDialect;
import org.apache.spark.sql.jdbc.JdbcDialects;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.avro.Schema.Field.NULL_DEFAULT_VALUE;
import static org.apache.hudi.avro.AvroSchemaUtils.createNullableSchema;

public class SchemaUtil {

    // INPUT COLUMNS SPECIFIC TO MYSQL
    public static final String INCOMING_SOURCE_FILE_FIELD = "source.file";
    public static final String INCOMING_SOURCE_POS_FIELD = "source.pos";
    public static final String INCOMING_SOURCE_ROW_FIELD = "source.row";
    public static final String INCOMING_SOURCE_ROW_GTID = "source.gtid";


    // OUTPUT COLUMNS SPECIFIC TO MYSQL
    //binlog file
    public static final String TABLE_FILE_COL_NAME = "_event_bin_file";
    //binlog position
    public static final String TABLE_POS_COL_NAME = "_event_pos";
    //Given the row number within a binlog event and the total number of rows in that event
    public static final String TABLE_ROW_COL_NAME = "_event_row";

    //Given the row number within a binlog event and the total number of rows in that event
    public static final String TABLE_GTID_COL_NAME = "_event_gtid";

    //生成唯一的全局id,可以是  bin file + position/ gtid  (换数据库都会有影响)
    public static final String TABLE_SEQ_COL_NAME = "_event_seq";

    //需要增加的元数据
    public static final List<String> MYSQL_META_COLUMNS =Arrays.asList(
            TABLE_FILE_COL_NAME,
            TABLE_POS_COL_NAME,
            TABLE_ROW_COL_NAME,
            TABLE_GTID_COL_NAME,
            TABLE_SEQ_COL_NAME
    );
    // All metadata fields are optional strings.
    public static final Schema METADATA_FIELD_SCHEMA = createNullableSchema(Schema.Type.STRING);

    private static StructType structType;

    /**
     * 根据 debezium中获取的table 获取 avro schema,为
     * @param table
     * @return
     */
    public static Schema createAvroSchemaByMysql(Table table, MySqlConnection mySqlConnection) throws Exception{
        //org.apache.hudi.utilities.UtilHelpers.getJDBCSchema


        //先转成 spark中的dataType,然后通过 转成avro schema
        // AvroConversionUtils.convertStructTypeToAvroSchema(structType, table, "hoodie." + table);
        //org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils.getSchema
        //JdbcUtils.getSchema(rs, dialect, true);
        JdbcDialect dialect = JdbcDialects.get("jdbc:mysql:");
        mySqlConnection.query("SELECT * FROM $table WHERE 1=0".
                replace("$table",table.id().catalog()+"."+table.id().table()),rs->{

            structType = JdbcUtils.getSchema(rs, dialect, false);
        });
        Schema schema = AvroConversionUtils.convertStructTypeToAvroSchema(structType, table.id().table(),
                "hoodie." + table.id().table());
        return schema;
    }


    // ResultSet 转 InternalRow
    //org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils.resultSetToRows

    /**
     * 为表中加入 META_COLUMNS
     * 参考 org.apache.hudi.avro.HoodieAvroUtils#addMetadataFields(org.apache.avro.Schema, boolean)
     * @param schema
     * @return
     */
    public static Schema addMetaSchema(Schema schema){
        List<Schema.Field> fields = schema.getFields();
        List<Schema.Field> parentFields = new ArrayList<>();
        for(Schema.Field field :fields){
            Schema.Field newField = new Schema.Field(field.name(), field.schema(), field.doc(), field.defaultVal());
            for (Map.Entry<String, Object> prop : field.getObjectProps().entrySet()) {
                newField.addProp(prop.getKey(), prop.getValue());
            }
            parentFields.add(newField);
        }
        for(String s:MYSQL_META_COLUMNS){
            parentFields.add(new Schema.Field(s, METADATA_FIELD_SCHEMA, ""));
        }

        Schema mergedSchema = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), false);
        mergedSchema.setFields(parentFields);
        return mergedSchema;

    }

    public static void main(String[] args) {
        Schema.Field commitTimeField =
                // null
                new Schema.Field(HoodieRecord.COMMIT_TIME_METADATA_FIELD, METADATA_FIELD_SCHEMA, "");
        Schema mergedSchema = Schema.createRecord("name", "doc",
              "namespace", false);
        mergedSchema.setFields(Arrays.asList(commitTimeField));
        Object o = commitTimeField.defaultVal();
        System.out.println(mergedSchema.toString());

        GenericRecord user1 = new GenericData.Record(mergedSchema);
        Object o1 = user1.get(0);
        System.out.println(o1);

    }



}
