package cdc;

import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges;

import java.util.*;

/** The split to describe the binlog of MySql table(s). */
public class MySqlBinlogSplit extends MySqlSplit {
    private final BinlogOffset startingOffset;
    private final BinlogOffset endingOffset;
    private final Map<TableId, TableChanges.TableChange> tableSchemas;

    public MySqlBinlogSplit(BinlogOffset startingOffset, BinlogOffset endingOffset, Map<TableId, TableChanges.TableChange> tableSchemas) {
        this.startingOffset = startingOffset;
        this.endingOffset = endingOffset;
        this.tableSchemas = tableSchemas;
    }

    @Override
    public Map<TableId, TableChanges.TableChange> getTableSchemas() {
        return tableSchemas;
    }

    @Override
    public BinlogOffset getStartingOffset() {
        return startingOffset;
    }
}