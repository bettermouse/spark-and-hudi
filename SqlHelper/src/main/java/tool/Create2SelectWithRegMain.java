package tool;

import antlr.generate.SqlBaseLexer;
import antlr.generate.SqlBaseParser;
import antlr.generate.UpperCaseCharStreamJava;
import antlr.listener.Create2SelectWithReg;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 *  ID   DATE               PERSON             REASON
 *  1   2022/5/17 14:54     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public class Create2SelectWithRegMain {
    public static void main(String[] args) {
        // create a CharStream that reads from standard input
        String s="CREATE EXTERNAL TABLE `ods_scs_oms2ord.tt_order_outbound_time`( `id` bigint COMMENT 'id', `product_code` string COMMENT '产品代码', `product_name` string COMMENT '产品名称', `warehouser_code` string COMMENT '仓库代码', `warehouser_name` string COMMENT '仓库名称', `customer_base_code` string COMMENT '货主代码', `customer_base_name` string COMMENT '货主名称', `starting_city` string COMMENT '始发城市', `destination_province` string COMMENT '目的地省', `destination_city` string COMMENT '目的城市', `order_start_time` string COMMENT '订单开始时间', `order_end_time` string COMMENT '订单结束时间', `order_should_dispatched` string COMMENT '订单应发班次', `order_outbound_time` string COMMENT '订单对应出库单时间', `creator` string COMMENT '创建人', `create_time` string COMMENT '创建时间', `modifier` string COMMENT '修改人', `modify_time` string COMMENT '修改时间') COMMENT '订单对应出库时间' PARTITIONED BY ( `inc_day` string COMMENT '增量日期') ROW FORMAT SERDE 'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe' STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'";
        System.out.println(s);
        SqlBaseLexer lexer = new SqlBaseLexer(new UpperCaseCharStreamJava(CharStreams.
                fromString(s)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        SqlBaseParser parser = new SqlBaseParser(tokens);

        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        System.out.println("转换后的sql 为");
        Create2SelectWithReg listener = new Create2SelectWithReg();
        parseTreeWalker.walk(listener,parser.singleStatement());
        System.out.println(listener.getSelectRegexp());
    }
}
