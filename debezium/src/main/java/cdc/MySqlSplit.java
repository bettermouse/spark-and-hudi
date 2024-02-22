package cdc;

import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges;

import java.util.Map;

public abstract class MySqlSplit {

    public abstract Map<TableId, TableChanges.TableChange> getTableSchemas();

    public abstract BinlogOffset getStartingOffset();
}
