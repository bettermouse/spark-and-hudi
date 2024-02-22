package tool;


import antlr.listener.ResolveCreateTable;
import antlr.table.LoadAllTable;
import antlr.table.Table;
import antlr.table.TableHelper;

import java.util.List;

public class HiveTableFillComment {
    public static void main(String[] args) {
        //从bdp中导出临时表的sql  show create table
        String oldTableSql =
                "\n" +
                        "CREATE TABLE `tmp_dm_cold.dm_ly_wh_box_recommend_1d_order_di_tmp_02`(\t\n" +
                        "  `dept_code` string\t \n" +
                        "  ,`dept_name` string\t \n" +
                        "  ,`warehouse_code` string\t \n" +
                        "  ,`warehouse_name` string\t \n" +
                        "  ,`company_code` string\t \n" +
                        "  ,`company_name` string\t \n" +
                        "  ,`wms_outbound_order_no` string\t \n" +
                        "  ,`finish_time` string\t \n" +
                        "  ,`wms_order_status_code` string\t \n" +
                        "  ,`wms_order_status_name` string\t \n" +
                        "  ,`push_time_orig` string\t \n" +
                        "  ,`rec_time_orig` string\t \n" +
                        "  ,`time_gap_s` bigint\t \n" +
                        "  ,`time_gap` double\t \n" +
                        "  ,`is_greate_5min` string\t \n" +
                        "  ,`is_greate_30min` string\t \n" +
                        "  ,`real_box_num` bigint\t \n" +
                        "  ,`recommend_box_num` bigint\t \n" +
                        "  ,`is_same` string\t \n" +
                        "  ,`reason` string)\t\n";
                ;
        //获取old表
        Table oldTable = ResolveCreateTable.resolveTable(oldTableSql);
        Table table = TableHelper.fillTableComment(oldTable);
        String createTableSql = TableHelper.getCreateTableSql(table);
        System.out.println(createTableSql);


    }
}
