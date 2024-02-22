package cdc;


import io.debezium.jdbc.JdbcConfiguration;
import io.debezium.jdbc.JdbcConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** A factory to create JDBC connection for MySQL. */
public class JdbcConnectionFactory implements JdbcConnection.ConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcConnectionFactory.class);

    private final MySqlSourceConfig sourceConfig;

    public JdbcConnectionFactory(MySqlSourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    @Override
    public Connection connect(JdbcConfiguration config) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection com = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:3306")
                , config.getUser(), config.getPassword());
        return com;
    }
}