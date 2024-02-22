package spark;

import cdc.BatchUpdate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.sql.catalyst.InternalRow;

public class SparkBatchUpdate implements BatchUpdate<InternalRow> {
    List list = new LinkedList<InternalRow>();
    @Override
    public void add(InternalRow one) {
        list.add(one);
    }

    @Override
    public List<InternalRow> getList() {
        return list;
    }

    @Override
    public UPDATE_TYPE getType() {
        return UPDATE_TYPE.DATA;
    }

    @Override
    public String getSql() {
        return null;
    }


}
