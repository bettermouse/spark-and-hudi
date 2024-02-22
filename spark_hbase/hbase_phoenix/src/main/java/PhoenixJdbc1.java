import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PhoenixJdbc1 {

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
            connection = DriverManager.getConnection("jdbc:phoenix:10.0.7.20,10.0.7.23,10.0.7.39:/xxd3rq9o19");
            Statement statement = connection.createStatement();

            statement.executeUpdate(createSql);
            statement.executeUpdate("upsert into user_tb values ('1','张三')");
            statement.executeUpdate("upsert into user_tb values ('2','李四')");
            connection.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}


