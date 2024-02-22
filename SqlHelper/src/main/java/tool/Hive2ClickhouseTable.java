package tool;

import antlr.listener.Create2SelectWithReg;
import antlr.listener.ResolveCreateTable;
import antlr.table.ClickhouseType;
import antlr.table.Table;

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 *  ID   DATE               PERSON             REASON
 *  1   2022/6/24 16:56     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public class Hive2ClickhouseTable {
    public static void main(String[] args) {
        String s="create table `dm_cold.dm_ly_wh_box_recommend_1d_order_di` (\n" +
                "  `dept_code` string comment '网点代码 ',\n" +
                "  `dept_name` string comment '网点名称 ',\n" +
                "  `warehouse_code` string comment '仓库CODE',\n" +
                "  `warehouse_name` string comment '仓库名称 ',\n" +
                "  `company_code` string comment '货主代码 ',\n" +
                "  `company_name` string comment '客户名称 ',\n" +
                "  `wms_outbound_order_no` string comment 'wms出库单号 ',\n" +
                "  `finish_time` string comment '订单完成时间 ',\n" +
                "  `wms_order_status_code` string comment 'WMS订单状态代码 ',\n" +
                "  `wms_order_status_name` string comment 'WMS订单状态名称 ',\n" +
                "  `push_time_orig` string comment '推送时间',\n" +
                "  `rec_time_orig` string comment '返回时间',\n" +
                "  `time_gap_s` bigint comment '时间间隔s',\n" +
                "  `time_gap` double comment '时间间隔',\n" +
                "  `is_greate_5min` string comment '是否大于5分钟',\n" +
                "  `is_greate_30min` string comment '是否大于30分钟',\n" +
                "  `real_box_num` bigint comment '实际总箱数',\n" +
                "  `recommend_box_num` bigint comment '推荐总箱数',\n" +
                "  `is_same` string comment '是否一致',\n" +
                "  `reason` string comment '原因'\n" +
                ") PARTITIONED BY (`inc_day` string) stored as parquet;";
        Table table = ResolveCreateTable.resolveTable(s);
        String chTableSql = ClickhouseType.getChTableSql(table);
        System.out.println(chTableSql);
    }
}
