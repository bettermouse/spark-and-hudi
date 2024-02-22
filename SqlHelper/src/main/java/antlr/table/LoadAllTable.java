package antlr.table;


import antlr.listener.ResolveCreateTable;
import antlr.table.Table;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadAllTable {
    public static List<Table> getAllTable() throws IOException {
        List<Table> tableList = new ArrayList<Table>();

        URL sqlFile = LoadAllTable.class.getResource("/sqlFile");
        File file = new File(sqlFile.getFile());
        File[] files = file.listFiles();
        for(File f:files){
            String s = FileUtils.readLines(f).get(0);
            Table table = ResolveCreateTable.resolveTable(s);
            tableList.add(table);
        }
        return tableList;
    }

    public static void main(String[] args) throws IOException {
        List<Table> tableList = getAllTable();
        System.out.println(tableList);
    }
}
