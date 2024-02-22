import java.sql.*;
import java.util.Arrays;
import java.util.Random;

public class PhoenixJdbc2 {

    static {
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String createSql = "create table if not exists user_tb(id VARCHAR primary key,uname VARCHAR)";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:phoenix:10.0.2.71:/xxhfhllj9p");

            String tableName = "sql_table_" + new Random().nextInt(1000);
//创建表
            try (Statement statement = connection.createStatement()) {
                String sql = "create table if not exists " + tableName + "(id VARCHAR primary key , name VARCHAR)";
                int ret = statement.executeUpdate(sql);
                System.out.println(ret);
            }

//插入数据
            String upsertSql = "upsert into " + tableName + "(id,name) values(?,?)";
            try (PreparedStatement ps = connection.prepareStatement(upsertSql)) {
                int batchSize = 100;
                for (int i = 0; i < batchSize; i++) {
                    ps.setString(1, "\'aa\'" + i);
                    ps.setString(2, "\'bb\'" + i);
                    //加入批次
                    ps.addBatch();
                }
                //执行全部批次的写入。
                //出于性能和稳定性考量，添加的批次数量不宜过大。
                //建议一次executeBatch()写入的批次数最多在百级别。
                int[] ret = ps.executeBatch();
                System.out.println(Arrays.toString(ret));
            }

//查询数据
            String querySql = "select * from " + tableName + " where id=?";
            try (PreparedStatement ps = connection.prepareStatement(querySql)) {
                ps.setString(1, "aa1");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String id = rs.getString(1);
                    String name = rs.getString(2);
                    System.out.println("id=" + id);
                    System.out.println("name=" + name);
                }
            }

//删除数据
            String deleteSql = "delete from " + tableName + " where id=?";
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setString(1, "aa1");
                ps.executeUpdate();
            }

//关闭连接，当结束操作时要确保连接被关闭，否则会造成连接泄漏。
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}