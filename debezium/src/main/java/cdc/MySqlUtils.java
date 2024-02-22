package cdc;

import io.debezium.relational.TableId;

/** Utils to prepare MySQL SQL statement. */
public class MySqlUtils {

    private MySqlUtils() {}

    public static String quote(String dbOrTableName) {
        return "`" + dbOrTableName + "`";
    }


    public static String quote(TableId tableId) {
        return tableId.toQuotedString('`');
    }
}
