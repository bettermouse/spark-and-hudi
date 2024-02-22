import org.apache.avro.Schema;
import org.apache.hudi.utilities.UtilHelpers;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestSchemaMapping {
    
    @Test
    public void test(){
        Map<String, String> map  = new HashMap<>();
        map.put(JDBCOptions.JDBC_URL(),"jdbc:mysql://scsdbci-m.dbsit.sfcloud.local:3306/scs_aios");
        map.put(JDBCOptions.JDBC_DRIVER_CLASS(),"com.mysql.jdbc.Driver");
        map.put("user","scs_dev");
        map.put("password","sf123456");
        map.put(JDBCOptions.JDBC_QUERY_TIMEOUT(),"1000");
        map.put(JDBCOptions.JDBC_TABLE_NAME(),"student");
        try {
            Schema jdbcSchema = UtilHelpers.getJDBCSchema(map);
            System.out.printf(jdbcSchema.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
