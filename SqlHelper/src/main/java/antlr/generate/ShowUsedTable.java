package antlr.generate;

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
 *  1   2022/3/28 15:42     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public class ShowUsedTable   extends SqlBaseBaseListener{
    @Override
    public void enterTableName(SqlBaseParser.TableNameContext ctx) {
        System.out.println(ctx.getText());
        super.enterTableName(ctx);
    }



    public static void main(String[] args) {
        // create a CharStream that reads from standard input
        String s="-- 冷运订单宽表开发\n" +
                "-- 数据源 ods_scs_oms2order.t_order   dwd.dwd_pub_order_dtl_di\n" +
                "-- 结果表 dwd_ly.dwd_ly_pub_order_dtl_di\n" +
                "\n" +
                "set tez.queue.name=colddata;\n" +
                "set hive.tez.exec.print.summary=true;\n" +
                "set hive.execution.engine=tez;\n" +
                "\n" +
                "set hive.exec.dynamic.partition.mode=nonstrict;\n" +
                "set hive.optimize.sort.dynamic.partition=true;\n" +
                "set hive.exec.max.dynamic.partitions=1000;\n" +
                "set hive.exec.max.dynamic.partitions.pernode=1000;\n" +
                "\n" +
                "-- 获取冷运opt订单信息\n" +
                "drop table if exists  tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp01;\n" +
                "create table tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp01 stored as parquet as \n" +
                "select \n" +
                "\to.order_no\n" +
                "\t,o.waybill_no\n" +
                "\t,o.erp_no\n" +
                "\t,o.product_code\n" +
                "\t,o.product_name\n" +
                "\t,o.sender_province_code\n" +
                "\t,o.sender_province_name\n" +
                "\t,o.sender_city_code\n" +
                "\t,o.sender_city_name\n" +
                "\t,o.sender_area_code\n" +
                "\t,o.sender_area_name\n" +
                "\t,o.sender_city_number\n" +
                "\t,o.sender_detail_address\n" +
                "\t,o.sender_website_code\n" +
                "\t,o.sender_website_name\n" +
                "\t,o.pickup_courier_name\n" +
                "\t,o.pickup_courier_code\n" +
                "\t,o.pickup_courier_mobile\n" +
                "\t,o.sender_company\n" +
                "\t,o.sender_name\n" +
                "\t,o.sender_mobile\n" +
                "\t,o.require_pickup_start\n" +
                "\t,o.require_pickup_end\n" +
                "\t,o.monthly_account\n" +
                "\t,o.monthly_account_name\n" +
                "\t,o.payment_type_code\n" +
                "\t,o.payment_type\n" +
                "\t,o.settlement_type\n" +
                "\t,o.receiver_province_code\n" +
                "\t,o.receiver_province_name\n" +
                "\t,o.receiver_city_code\n" +
                "\t,o.receiver_city_name\n" +
                "\t,o.receiver_area_code\n" +
                "\t,o.receiver_area_name\n" +
                "\t,o.receiver_city_number\n" +
                "\t,o.receiver_detail_address\n" +
                "\t,o.receiver_website_code\n" +
                "\t,o.receiver_website_name\n" +
                "\t,o.deliverer_code\n" +
                "\t,o.deliverer_name\n" +
                "\t,o.deliverer_mobile\n" +
                "\t,o.receiver_company\n" +
                "\t,o.receiver_name\n" +
                "\t,o.receiver_mobile\n" +
                "\t,o.require_distribution_start\n" +
                "\t,o.require_distribution_end\n" +
                "\t,o.pickup_type\n" +
                "\t,o.distribution_type\n" +
                "\t,o.is_special_warehousing\n" +
                "\t,o.order_source_code\n" +
                "\t,o.source_code\n" +
                "\t,o.order_status\n" +
                "\t,o.pay_status\n" +
                "\t,o.sender_pay_status\n" +
                "\t,o.receiver_pay_status\n" +
                "\t,o.transport_status\n" +
                "\t,o.fee_status\n" +
                "\t,o.fee_status_remark\n" +
                "\t,o.batch_no\n" +
                "\t,o.order_type\n" +
                "\t,o.out_of_range\n" +
                "\t,o.user_id\n" +
                "\t,o.customer_id\n" +
                "\t,o.business_employee_no\n" +
                "\t,o.recommend_no\n" +
                "\t,o.is_special_order\n" +
                "\t,o.freight_amt\n" +
                "\t,o.fee_total\n" +
                "\t,o.fix_price\n" +
                "\t,o.meterage_weight\n" +
                "\t,o.dimensional_weight\n" +
                "\t,o.order_remark\n" +
                "\t,o.transport_type\n" +
                "\t,o.waybill_count\n" +
                "\t,g.quantity   as goods_quantity\n" +
                "\t,o.weighing_method\n" +
                "\t,o.confirm_time\n" +
                "\t,o.sign_time\n" +
                "\t,o.cancel_reason\n" +
                "\t,o.special_address_no\n" +
                "\t,o.creator\n" +
                "\t,o.create_time\n" +
                "\t,o.modifier\n" +
                "\t,o.modify_time\n" +
                "\t,o.sender_unit_area_code\n" +
                "\t,o.receiver_unit_area_code\n" +
                "\t,o.sender_unit_area_name\n" +
                "\t,o.receiver_unit_area_name\n" +
                "\t,o.temperature_level_name\n" +
                "\t,o.intercept_batch\n" +
                "\t,o.intercept_time\n" +
                "\t,o.delivery_batch\n" +
                "\t,o.gross_weight\n" +
                "\t,o.animal_inspection_batch_no\n" +
                "\t,o.estimated_weight\n" +
                "\t,o.estimate_fee\n" +
                "\t,o.client_pickup_start\n" +
                "\t,o.client_pickup_end\n" +
                "\t,o.return_waybill_no\n" +
                "\t,o.special_flag\n" +
                "\t,o.imported_flag\n" +
                "\t,o.hidden_flag\n" +
                "\t,o.product_alias_code\n" +
                "\t,o.sender_dept_code\n" +
                "\t,o.receiver_dept_code\n" +
                "\t,g.sku_name as cargo_name\n" +
                "\t,g.sku_no as cargo_type\n" +
                "\t,get_json_object(s.extension_info,'$.declareValue') as declared_value_amt\n" +
                "\t,ext.discount_ticket_no\n" +
                "\t,ext.driver_no\n" +
                "\t,ext.focus_code\n" +
                "\t,ext.carton_number\n" +
                "\t,ext.print_number\n" +
                "\t,ext.board_print_flag\n" +
                "\t,ext.bucket_flag\t\t\n" +
                "\t,'1' as data_source_flag\n" +
                "from \n" +
                "(select \n" +
                "\torder_no\n" +
                "\t,waybill_no\n" +
                "\t,erp_no\n" +
                "\t,product_code\n" +
                "\t,product_name\n" +
                "\t,sender_province_code\n" +
                "\t,sender_province_name\n" +
                "\t,sender_city_code\n" +
                "\t,sender_city_name\n" +
                "\t,sender_area_code\n" +
                "\t,sender_area_name\n" +
                "\t,sender_city_number\n" +
                "\t,sender_detail_address\n" +
                "\t,sender_website_code\n" +
                "\t,sender_website_name\n" +
                "\t,pickup_courier_name\n" +
                "\t,pickup_courier_code\n" +
                "\t,pickup_courier_mobile\n" +
                "\t,sender_company\n" +
                "\t,sender_name\n" +
                "\t,sender_mobile\n" +
                "\t,require_pickup_start\n" +
                "\t,require_pickup_end\n" +
                "\t,monthly_account\n" +
                "\t,monthly_account_name\n" +
                "\t,payment_type_code\n" +
                "\t,payment_type\n" +
                "\t,settlement_type\n" +
                "\t,receiver_province_code\n" +
                "\t,receiver_province_name\n" +
                "\t,receiver_city_code\n" +
                "\t,receiver_city_name\n" +
                "\t,receiver_area_code\n" +
                "\t,receiver_area_name\n" +
                "\t,receiver_city_number\n" +
                "\t,receiver_detail_address\n" +
                "\t,receiver_website_code\n" +
                "\t,receiver_website_name\n" +
                "\t,deliverer_code\n" +
                "\t,deliverer_name\n" +
                "\t,deliverer_mobile\n" +
                "\t,receiver_company\n" +
                "\t,receiver_name\n" +
                "\t,receiver_mobile\n" +
                "\t,require_distribution_start\n" +
                "\t,require_distribution_end\n" +
                "\t,pickup_type\n" +
                "\t,distribution_type\n" +
                "\t,is_special_warehousing\n" +
                "\t,'opt' as order_source_code\n" +
                "\t,source_code\n" +
                "\t,order_status\n" +
                "\t,pay_status\n" +
                "\t,sender_pay_status\n" +
                "\t,receiver_pay_status\n" +
                "\t,transport_status\n" +
                "\t,fee_status\n" +
                "\t,fee_status_remark\n" +
                "\t,batch_no\n" +
                "\t,order_type\n" +
                "\t,out_of_range\n" +
                "\t,user_id\n" +
                "\t,customer_id\n" +
                "\t,business_employee_no\n" +
                "\t,recommend_no\n" +
                "\t,is_special_order\n" +
                "\t,freight_amt\n" +
                "\t,fee_total\n" +
                "\t,fix_price\n" +
                "\t,meterage_weight\n" +
                "\t,dimensional_weight\n" +
                "\t,order_remark\n" +
                "\t,transport_type\n" +
                "\t,waybill_count\n" +
                "\t,weighing_method\n" +
                "\t,confirm_time\n" +
                "\t,sign_time\n" +
                "\t,cancel_reason\n" +
                "\t,special_address_no\n" +
                "\t,creator\n" +
                "\t,create_time\n" +
                "\t,modifier\n" +
                "\t,modify_time\n" +
                "\t,sender_unit_area_code\n" +
                "\t,receiver_unit_area_code\n" +
                "\t,sender_unit_area_name\n" +
                "\t,receiver_unit_area_name\n" +
                "\t,temperature_level_name\n" +
                "\t,intercept_batch\n" +
                "\t,intercept_time\n" +
                "\t,delivery_batch\n" +
                "\t,gross_weight\n" +
                "\t,animal_inspection_batch_no\n" +
                "\t,estimated_weight\n" +
                "\t,estimate_fee\n" +
                "\t,client_pickup_start\n" +
                "\t,client_pickup_end\n" +
                "\t,return_waybill_no\n" +
                "\t,special_flag\n" +
                "\t,imported_flag\n" +
                "\t,hidden_flag\n" +
                "\t,product_alias_code\n" +
                "\t,sender_dept_code\n" +
                "\t,receiver_dept_code\n" +
                "\t,row_number()over(partition by order_no order by inc_day desc) rn\n" +
                "from ods_scs_oms2order.t_order where inc_day between '${day_before_30d}' and '${day_before_1d}' \n" +
                "\tand regexp_replace(substr(create_time,0,10),'-','') between '${day_before_30d}' and '${day_before_1d}') o \n" +
                "left join (select * from (select *,row_number()over(partition by order_no order by inc_day  desc) rn from ods_scs_oms2order.t_order_goods where inc_day between '${day_before_30d}' and '${day_before_1d}')cur where cur.rn=1) g on o.order_no=g.order_no\n" +
                "left join (select * from (select *,row_number()over(partition by order_no order by inc_day  desc) rn from ods_scs_oms2order.t_order_ext where inc_day between '${day_before_30d}' and '${day_before_1d}')cur where cur.rn=1)ext on o.order_no=ext.order_no\n" +
                "left join (select * from (select *,row_number()over(partition by order_no order by inc_day  desc) rn from ods_scs_oms2order.t_order_service where inc_day between '${day_before_30d}' and '${day_before_1d}' and service_code='VA0021')cur where cur.rn=1)s on o.order_no=s.order_no \n" +
                "where o.rn=1\n" +
                "union all  -- 获取大网订单 \n" +
                "select \n" +
                "\to2.order_no\n" +
                "\t,o2.waybill_no\n" +
                "\t,eo.logistic_no as erp_no\n" +
                "\t,nvl(eo.carrier_service_type,o2.product_code) as product_code\n" +
                "\t,nvl(eo.carrier_service_type_name,o2.product_name) as product_name\n" +
                "\t,eo.shipper_province_code as sender_province_code\n" +
                "\t,o2.sender_province_name\n" +
                "\t,nvl(eo.shipper_city_code,o2.sender_city_code) sender_city_code\n" +
                "\t,o2.sender_city_name\n" +
                "\t,eo.shipper_district_code as sender_area_code\n" +
                "\t,o2.sender_area_name\n" +
                "\t,nvl(eo.original_place,o2.sender_city_number) as sender_city_number\n" +
                "\t,o2.sender_detail_address\n" +
                "\t,nvl(eo.station_code,o2.sender_website_code) as sender_website_code\n" +
                "\t,eo.station_name as sender_website_name\n" +
                "\t,o2.pickup_courier_name\n" +
                "\t,o2.pickup_courier_code\n" +
                "\t,o2.pickup_courier_mobile\n" +
                "\t,o2.sender_company\n" +
                "\t,o2.sender_name\n" +
                "\t,o2.sender_mobile\n" +
                "\t,o2.require_pickup_start\n" +
                "\t,o2.require_pickup_end\n" +
                "\t,nvl(eo.monthly_account,o2.monthly_account) as monthly_account\n" +
                "\t,nvl(eo.customer_name,o2.monthly_account_name) as monthly_account_name\n" +
                "\t,o2.payment_type_code\n" +
                "\t,o2.payment_type\n" +
                "\t,o2.settlement_type\n" +
                "\t,eo.consignee_province_code as receiver_province_code\n" +
                "\t,o2.receiver_province_name\n" +
                "\t,o2.receiver_city_code\n" +
                "\t,o2.receiver_city_name\n" +
                "\t,eo.consignee_district_code as receiver_area_code\n" +
                "\t,o2.receiver_area_name\n" +
                "\t,nvl(eo.website_area_number,o2.receiver_city_number) as  receiver_city_number\n" +
                "\t,o2.receiver_detail_address\n" +
                "\t,eo.website_code as receiver_website_code\n" +
                "\t,eo.website_name as receiver_website_name\n" +
                "\t,o2.deliverer_code\n" +
                "\t,o2.deliverer_name\n" +
                "\t,o2.deliverer_mobile\n" +
                "\t,o2.receiver_company\n" +
                "\t,o2.receiver_name\n" +
                "\t,o2.receiver_mobile\n" +
                "\t,o2.require_distribution_start\n" +
                "\t,o2.require_distribution_end\n" +
                "\t,nvl(eo.pick_up_type,o2.pickup_type) as pickup_type\n" +
                "\t,eo.distribution_type as distribution_type\n" +
                "\t,o2.is_special_warehousing\n" +
                "\t,o2.order_source_code\n" +
                "\t,nvl(order_type.order_type_desc,o2.source_code) as source_code\n" +
                "\t,nvl(st.order_status_desc,o2.order_status) as order_status\n" +
                "\t,o2.pay_status\n" +
                "\t,o2.sender_pay_status\n" +
                "\t,o2.receiver_pay_status\n" +
                "\t,o2.transport_status\n" +
                "\t,o2.fee_status\n" +
                "\t,o2.fee_status_remark\n" +
                "\t,o2.batch_no\n" +
                "\t,o2.order_type\n" +
                "\t,o2.out_of_range\n" +
                "\t,o2.user_id\n" +
                "\t,o2.customer_id\n" +
                "\t,o2.business_employee_no\n" +
                "\t,o2.recommend_no\n" +
                "\t,o2.is_special_order\n" +
                "\t,o2.freight_amt\n" +
                "\t,o2.fee_total\n" +
                "\t,o2.fix_price\n" +
                "\t,eo.charged_weight as meterage_weight\n" +
                "\t,o2.dimensional_weight\n" +
                "\t,o2.order_remark\n" +
                "\t,o2.transport_type\n" +
                "\t,eo.package_quantity as waybill_count\n" +
                "\t,eo.quantity as goods_quantity\n" +
                "\t,o2.weighing_method\n" +
                "\t,o2.confirm_time\n" +
                "\t,o2.sign_time\n" +
                "\t,o2.cancel_reason as cancel_reason\n" +
                "\t,o2.special_address_no\n" +
                "\t,o2.creator\n" +
                "\t,nvl(eo.create_time,o2.create_time) as create_time\n" +
                "\t,o2.modifier\n" +
                "\t,o2.modify_time\n" +
                "\t,o2.sender_unit_area_code\n" +
                "\t,o2.receiver_unit_area_code\n" +
                "\t,o2.sender_unit_area_name\n" +
                "\t,o2.receiver_unit_area_name\n" +
                "\t,o2.temperature_level_name\n" +
                "\t,o2.intercept_batch\n" +
                "\t,o2.intercept_time\n" +
                "\t,o2.delivery_batch\n" +
                "\t,o2.gross_weight\n" +
                "\t,o2.animal_inspection_batch_no\n" +
                "\t,o2.estimated_weight\n" +
                "\t,o2.estimate_fee\n" +
                "\t,o2.client_pickup_start\n" +
                "\t,o2.client_pickup_end\n" +
                "\t,eo.return_order_no as return_waybill_no\n" +
                "\t,o2.special_flag\n" +
                "\t,o2.imported_flag\n" +
                "\t,o2.hidden_flag\n" +
                "\t,o2.product_alias_code\n" +
                "\t,o2.sender_dept_code\n" +
                "\t,o2.receiver_dept_code\n" +
                "\t,o2.cargo_name\n" +
                "\t,o2.cargo_type\n" +
                "\t,o2.declared_value_amt\n" +
                "\t,o2.discount_ticket_no\n" +
                "\t,o2.driver_no\n" +
                "\t,o2.focus_code\n" +
                "\t,o2.carton_number\n" +
                "\t,o2.print_number\n" +
                "\t,o2.board_print_flag\n" +
                "\t,o2.bucket_flag\t\t\n" +
                "\t,o2.data_source_flag\n" +
                "from \n" +
                "(select \n" +
                "\to.inner_order_no as order_no\n" +
                "\t,waybill_no[0] as waybill_no\n" +
                "\t,null as erp_no\n" +
                "\t,o.limit_type_code as product_code\n" +
                "\t,o.limit_type_name as product_name\n" +
                "\t,null as sender_province_code\n" +
                "\t,o.sender_province_name as sender_province_name\n" +
                "\t,o.sender_city_code\n" +
                "\t,o.sender_city_name\n" +
                "\t,null as sender_area_code\n" +
                "\t,o.sender_area_name\n" +
                "\t,o.sender_city_code as sender_city_number\n" +
                "\t,o.sender_address as sender_detail_address\n" +
                "\t,o.dept_code as sender_website_code\n" +
                "\t,null as sender_website_name\n" +
                "\t,null as pickup_courier_name\n" +
                "\t,o.pickup_emp_code as pickup_courier_code\n" +
                "\t,null as pickup_courier_mobile\n" +
                "\t,o.sender_company\n" +
                "\t,o.sender_name\n" +
                "\t,o.sender_tel as sender_mobile\n" +
                "\t,null as require_pickup_start\n" +
                "\t,null as require_pickup_end\n" +
                "\t,o.monthly_card_no as monthly_account\n" +
                "\t,null as monthly_account_name\n" +
                "\t,o.pay_type_code as payment_type_code\n" +
                "\t,o.pay_type_code as payment_type\n" +
                "\t,null as settlement_type\n" +
                "\t,null as receiver_province_code\n" +
                "\t,o.receiver_province_name\n" +
                "\t,o.receiver_city_code\n" +
                "\t,o.receiver_city_name\n" +
                "\t,null as receiver_area_code\n" +
                "\t,o.receiver_area_name\n" +
                "\t,o.receiver_city_code as receiver_city_number\n" +
                "\t,o.receiver_address as receiver_detail_address\n" +
                "\t,null as receiver_website_code\n" +
                "\t,null as receiver_website_name\n" +
                "\t,null as deliverer_code\n" +
                "\t,null as deliverer_name\n" +
                "\t,null as deliverer_mobile\n" +
                "\t,o.receiver_company\n" +
                "\t,o.receiver_name\n" +
                "\t,o.receiver_tel as receiver_mobile\n" +
                "\t,null as require_distribution_start\n" +
                "\t,null as require_distribution_end\n" +
                "\t,o.pickup_type_code as pickup_type\n" +
                "\t,null as distribution_type\n" +
                "\t,null as is_special_warehousing\n" +
                "\t,o.src_subsys_code as order_source_code\n" +
                "\t,o.order_type_code as source_code\n" +
                "\t,o.order_status_code as order_status\n" +
                "\t,null as pay_status\n" +
                "\t,null as sender_pay_status\n" +
                "\t,null as receiver_pay_status\n" +
                "\t,null as transport_status\n" +
                "\t,null as fee_status\n" +
                "\t,null as fee_status_remark\n" +
                "\t,null as batch_no\n" +
                "\t,null as order_type\n" +
                "\t,null as out_of_range\n" +
                "\t,null as user_id\n" +
                "\t,null as customer_id\n" +
                "\t,null as business_employee_no\n" +
                "\t,null as recommend_no\n" +
                "\t,null as is_special_order\n" +
                "\t,null as freight_amt\n" +
                "\t,null as fee_total\n" +
                "\t,null as fix_price\n" +
                "\t,null as meterage_weight\n" +
                "\t,null as dimensional_weight\n" +
                "\t,null as order_remark\n" +
                "\t,null as transport_type\n" +
                "\t,null as waybill_count\n" +
                "\t,null as goods_quantity\n" +
                "\t,null as weighing_method\n" +
                "\t,null as confirm_time\n" +
                "\t,null as sign_time\n" +
                "\t,o.cancel_reason as cancel_reason\n" +
                "\t,null as special_address_no\n" +
                "\t,null as creator\n" +
                "\t,nvl(o.order_tm,o.create_tm) as create_time\n" +
                "\t,null as modifier\n" +
                "\t,o.modify_tm as modify_time\n" +
                "\t,o.unitarea_code as sender_unit_area_code\n" +
                "\t,null as receiver_unit_area_code\n" +
                "\t,null as sender_unit_area_name\n" +
                "\t,null as receiver_unit_area_name\n" +
                "\t,null as temperature_level_name\n" +
                "\t,null as intercept_batch\n" +
                "\t,null as intercept_time\n" +
                "\t,null as delivery_batch\n" +
                "\t,null as gross_weight\n" +
                "\t,null as animal_inspection_batch_no\n" +
                "\t,o.est_weight as estimated_weight\n" +
                "\t,o.est_price as estimate_fee\n" +
                "\t,o.appoint_start_tm as client_pickup_start\n" +
                "\t,o.appoint_finish_tm as client_pickup_end\n" +
                "\t,null as return_waybill_no\n" +
                "\t,null as special_flag\n" +
                "\t,null as imported_flag\n" +
                "\t,null as hidden_flag\n" +
                "\t,null as product_alias_code\n" +
                "\t,null as sender_dept_code\n" +
                "\t,null as receiver_dept_code\n" +
                "\t,regexp_replace(o.cargo_name,',',';') as cargo_name\n" +
                "\t,null as cargo_type\n" +
                "\t,o.declared_value_amt as declared_value_amt\n" +
                "\t,null as discount_ticket_no\n" +
                "\t,null as driver_no\n" +
                "\t,null as focus_code\n" +
                "\t,null as carton_number\n" +
                "\t,null as print_number\n" +
                "\t,null as board_print_flag\n" +
                "\t,null as bucket_flag\t\t\n" +
                "\t,case when src_sys_name='scs_oms2ord_outbound' then '2'\n" +
                "\t\telse '3' end as data_source_flag\n" +
                "\t,src_sys_type\n" +
                "from dwd.dwd_pub_order_dtl_di o where inc_day between '${day_before_30d}' and '${day_before_1d}' and \n" +
                "\tregexp_replace(substr(nvl(order_tm,create_tm),0,10),'-','') between '${day_before_30d}' and '${day_before_1d}'\n" +
                "\tand (src_sys_name='scs_oms2ord_outbound' or limit_type_name\t like '冷运%' )  and src_subsys_code!='TMS-COLD')\t o2 \n" +
                "left join (select * from (select *,row_number()over(partition by order_no order by inc_day  desc) rn from ods_scs_oms2ord.eo_outbound_order where inc_day between '${day_before_30d}' and '${day_before_1d}' )cur where cur.rn=1) eo on o2.order_no=eo.order_no \n" +
                "left join dim.dim_order_type_cfg order_type on o2.src_sys_type=order_type.src_sys_type and o2.source_code=order_type.order_type_code\n" +
                "left join dim.dim_order_status st on o2.order_status=st.order_status_code and o2.src_sys_type=st.src_sys_type\n" +
                ";\n" +
                "\n" +
                "-- 去重\n" +
                "drop table if exists  tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp02;\n" +
                "create table tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp02 stored as parquet as \n" +
                "select \n" +
                "\torder_no\n" +
                "\t,waybill_no\n" +
                "\t,erp_no\n" +
                "\t,product_code\n" +
                "\t,product_name\n" +
                "\t,sender_province_code\n" +
                "\t,sender_province_name\n" +
                "\t,sender_city_code\n" +
                "\t,sender_city_name\n" +
                "\t,sender_area_code\n" +
                "\t,sender_area_name\n" +
                "\t,sender_city_number\n" +
                "\t,sender_detail_address\n" +
                "\t,sender_website_code\n" +
                "\t,sender_website_name\n" +
                "\t,pickup_courier_name\n" +
                "\t,pickup_courier_code\n" +
                "\t,pickup_courier_mobile\n" +
                "\t,sender_company\n" +
                "\t,sender_name\n" +
                "\t,sender_mobile\n" +
                "\t,require_pickup_start\n" +
                "\t,require_pickup_end\n" +
                "\t,monthly_account\n" +
                "\t,monthly_account_name\n" +
                "\t,payment_type_code\n" +
                "\t,payment_type\n" +
                "\t,settlement_type\n" +
                "\t,receiver_province_code\n" +
                "\t,receiver_province_name\n" +
                "\t,receiver_city_code\n" +
                "\t,receiver_city_name\n" +
                "\t,receiver_area_code\n" +
                "\t,receiver_area_name\n" +
                "\t,receiver_city_number\n" +
                "\t,receiver_detail_address\n" +
                "\t,receiver_website_code\n" +
                "\t,receiver_website_name\n" +
                "\t,deliverer_code\n" +
                "\t,deliverer_name\n" +
                "\t,deliverer_mobile\n" +
                "\t,receiver_company\n" +
                "\t,receiver_name\n" +
                "\t,receiver_mobile\n" +
                "\t,require_distribution_start\n" +
                "\t,require_distribution_end\n" +
                "\t,pickup_type\n" +
                "\t,distribution_type\n" +
                "\t,is_special_warehousing\n" +
                "\t,order_source_code\n" +
                "\t,case source_code   \n" +
                "\t\twhen 'SCC_WX' THEN '冷运微信'\n" +
                "\t\twhen 'SCC_WX_QR' THEN '扫码（冷运微信）'\n" +
                "\t\twhen 'SCC' THEN '冷运官网'\n" +
                "\t\twhen 'CCSP' then '速运APP'\n" +
                "\t\twhen 'SCC_CCS' then 'CCS5客服'\n" +
                "\t\twhen 'HAND' then 'OMS手工新增'\n" +
                "\t\twhen 'EXCEL' then 'OMS批量导入'\n" +
                "      when 'SCC_SINGLE' then '工作台单票下单'\n" +
                "      when 'SCC_EXCEL' then '工作台批量下单'\n" +
                "\t\twhen 'WMS' then 'WMS仓单'\n" +
                "      when 'FXG_QR' then '扫码（丰源）'\n" +
                "      when 'SCC_APP_QR' then '扫码（TMS-APP）'\n" +
                "      when 'ORDER_HAND' then '订单管理（单票）'\n" +
                "      when 'ORDER_EXCEL' then '订单管理（批量）'\n" +
                "      when 'GZGL' then '系统对接（广州格利）'\n" +
                "\t  when 'CCSP_H5' then '速运小程序'\n" +
                "\t\telse source_code  end as source_code  \n" +
                "\t,case when order_status='UN_SUBMITTED' \t\tthen '未审核'\n" +
                "\t\t\twhen order_status='SUBMIT_FAIL' \t\t\tthen '审核异常'\n" +
                "\t\t\twhen order_status='SUBMITTED' \t\t\tthen '已审核'\n" +
                "\t\t\twhen order_status='ACCEPT' \t\t\t\tthen '已收件'\n" +
                "\t\t\twhen order_status='IN_TRANSIT'\t\t\tthen '运输中'\n" +
                "\t\t\twhen order_status='WAIT_DISTRIBUTION' \tthen '待配送'\n" +
                "\t\t\twhen order_status='WAIT_SELF_SINCE' \t\tthen '待自提'\n" +
                "\t\t\twhen order_status='HANDOVER' \t\t\tthen '派件交接'\n" +
                "\t\t\twhen order_status='IN_DISTRIBUTION' \t\tthen '配送中'\n" +
                "\t\t\twhen order_status='PART_SIGN_OFF' \t\tthen '部分签收'\n" +
                "\t\t\twhen order_status='SIGN_OFF'\t\t\t\tthen '已签收'\n" +
                "\t\t\twhen order_status='USELESS' \t\t\t\tthen '已取消'\n" +
                "\t\t\twhen order_status='CANCELED' \t\t\tthen '已取消' \n" +
                "\t\t\telse order_status end as  order_status\n" +
                "\t,pay_status\n" +
                "\t,sender_pay_status\n" +
                "\t,receiver_pay_status\n" +
                "\t,transport_status\n" +
                "\t,fee_status\n" +
                "\t,fee_status_remark\n" +
                "\t,batch_no\n" +
                "\t,order_type\n" +
                "\t,out_of_range\n" +
                "\t,user_id\n" +
                "\t,customer_id\n" +
                "\t,business_employee_no\n" +
                "\t,recommend_no\n" +
                "\t,is_special_order\n" +
                "\t,freight_amt\n" +
                "\t,fee_total\n" +
                "\t,fix_price\n" +
                "\t,meterage_weight\n" +
                "\t,dimensional_weight\n" +
                "\t,order_remark\n" +
                "\t,transport_type\n" +
                "\t,waybill_count\n" +
                "\t,goods_quantity\n" +
                "\t,weighing_method\n" +
                "\t,confirm_time\n" +
                "\t,sign_time\n" +
                "\t,cancel_reason\n" +
                "\t,special_address_no\n" +
                "\t,creator\n" +
                "\t,create_time\n" +
                "\t,modifier\n" +
                "\t,modify_time\n" +
                "\t,sender_unit_area_code\n" +
                "\t,receiver_unit_area_code\n" +
                "\t,sender_unit_area_name\n" +
                "\t,receiver_unit_area_name\n" +
                "\t,temperature_level_name\n" +
                "\t,intercept_batch\n" +
                "\t,intercept_time\n" +
                "\t,delivery_batch\n" +
                "\t,gross_weight\n" +
                "\t,animal_inspection_batch_no\n" +
                "\t,estimated_weight\n" +
                "\t,estimate_fee\n" +
                "\t,client_pickup_start\n" +
                "\t,client_pickup_end\n" +
                "\t,return_waybill_no\n" +
                "\t,special_flag\n" +
                "\t,imported_flag\n" +
                "\t,hidden_flag\n" +
                "\t,product_alias_code\n" +
                "\t,sender_dept_code\n" +
                "\t,receiver_dept_code\n" +
                "\t,cargo_name\n" +
                "\t,cargo_type\n" +
                "\t,declared_value_amt\n" +
                "\t,discount_ticket_no\n" +
                "\t,driver_no\n" +
                "\t,focus_code\n" +
                "\t,carton_number\n" +
                "\t,print_number\n" +
                "\t,board_print_flag\n" +
                "\t,bucket_flag\t\t\n" +
                "\t,data_source_flag\n" +
                "from \n" +
                "(select \n" +
                "\t*,row_number()over(partition by order_no order by data_source_flag) rn \n" +
                "from tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp01 where waybill_no is null or waybill_no='') cur where cur.rn=1\n" +
                "union \n" +
                "select \n" +
                "\torder_no\n" +
                "\t,waybill_no\n" +
                "\t,erp_no\n" +
                "\t,product_code\n" +
                "\t,product_name\n" +
                "\t,sender_province_code\n" +
                "\t,sender_province_name\n" +
                "\t,sender_city_code\n" +
                "\t,sender_city_name\n" +
                "\t,sender_area_code\n" +
                "\t,sender_area_name\n" +
                "\t,sender_city_number\n" +
                "\t,sender_detail_address\n" +
                "\t,sender_website_code\n" +
                "\t,sender_website_name\n" +
                "\t,pickup_courier_name\n" +
                "\t,pickup_courier_code\n" +
                "\t,pickup_courier_mobile\n" +
                "\t,sender_company\n" +
                "\t,sender_name\n" +
                "\t,sender_mobile\n" +
                "\t,require_pickup_start\n" +
                "\t,require_pickup_end\n" +
                "\t,monthly_account\n" +
                "\t,monthly_account_name\n" +
                "\t,payment_type_code\n" +
                "\t,payment_type\n" +
                "\t,settlement_type\n" +
                "\t,receiver_province_code\n" +
                "\t,receiver_province_name\n" +
                "\t,receiver_city_code\n" +
                "\t,receiver_city_name\n" +
                "\t,receiver_area_code\n" +
                "\t,receiver_area_name\n" +
                "\t,receiver_city_number\n" +
                "\t,receiver_detail_address\n" +
                "\t,receiver_website_code\n" +
                "\t,receiver_website_name\n" +
                "\t,deliverer_code\n" +
                "\t,deliverer_name\n" +
                "\t,deliverer_mobile\n" +
                "\t,receiver_company\n" +
                "\t,receiver_name\n" +
                "\t,receiver_mobile\n" +
                "\t,require_distribution_start\n" +
                "\t,require_distribution_end\n" +
                "\t,pickup_type\n" +
                "\t,distribution_type\n" +
                "\t,is_special_warehousing\n" +
                "\t,order_source_code\n" +
                "\t,case source_code   \n" +
                "\t\twhen 'SCC_WX' THEN '冷运微信'\n" +
                "\t\twhen 'SCC_WX_QR' THEN '扫码（冷运微信）'\n" +
                "\t\twhen 'SCC' THEN '冷运官网'\n" +
                "\t\twhen 'CCSP' then '速运APP'\n" +
                "\t\twhen 'SCC_CCS' then 'CCS5客服'\n" +
                "\t\twhen 'HAND' then 'OMS手工新增'\n" +
                "\t\twhen 'EXCEL' then 'OMS批量导入'\n" +
                "      when 'SCC_SINGLE' then '工作台单票下单'\n" +
                "      when 'SCC_EXCEL' then '工作台批量下单'\n" +
                "\t\twhen 'WMS' then 'WMS仓单'\n" +
                "      when 'FXG_QR' then '扫码（丰源）'\n" +
                "      when 'SCC_APP_QR' then '扫码（TMS-APP）'\n" +
                "      when 'ORDER_HAND' then '订单管理（单票）'\n" +
                "      when 'ORDER_EXCEL' then '订单管理（批量）'\n" +
                "      when 'GZGL' then '系统对接（广州格利）'\n" +
                "\t  when 'CCSP_H5' then '速运小程序'\n" +
                "\t\telse source_code  end as source_code  \n" +
                "\t,case when order_status='UN_SUBMITTED' \t\tthen '未审核'\n" +
                "\t\t\twhen order_status='SUBMIT_FAIL' \t\t\tthen '审核异常'\n" +
                "\t\t\twhen order_status='SUBMITTED' \t\t\tthen '已审核'\n" +
                "\t\t\twhen order_status='ACCEPT' \t\t\t\tthen '已收件'\n" +
                "\t\t\twhen order_status='IN_TRANSIT'\t\t\tthen '运输中'\n" +
                "\t\t\twhen order_status='WAIT_DISTRIBUTION' \tthen '待配送'\n" +
                "\t\t\twhen order_status='WAIT_SELF_SINCE' \t\tthen '待自提'\n" +
                "\t\t\twhen order_status='HANDOVER' \t\t\tthen '派件交接'\n" +
                "\t\t\twhen order_status='IN_DISTRIBUTION' \t\tthen '配送中'\n" +
                "\t\t\twhen order_status='PART_SIGN_OFF' \t\tthen '部分签收'\n" +
                "\t\t\twhen order_status='SIGN_OFF'\t\t\t\tthen '已签收'\n" +
                "\t\t\twhen order_status='USELESS' \t\t\t\tthen '已取消'\n" +
                "\t\t\twhen order_status='CANCELED' \t\t\tthen '已取消' \n" +
                "\t\t\telse order_status end as  order_status\n" +
                "\t,pay_status\n" +
                "\t,sender_pay_status\n" +
                "\t,receiver_pay_status\n" +
                "\t,transport_status\n" +
                "\t,fee_status\n" +
                "\t,fee_status_remark\n" +
                "\t,batch_no\n" +
                "\t,order_type\n" +
                "\t,out_of_range\n" +
                "\t,user_id\n" +
                "\t,customer_id\n" +
                "\t,business_employee_no\n" +
                "\t,recommend_no\n" +
                "\t,is_special_order\n" +
                "\t,freight_amt\n" +
                "\t,fee_total\n" +
                "\t,fix_price\n" +
                "\t,meterage_weight\n" +
                "\t,dimensional_weight\n" +
                "\t,order_remark\n" +
                "\t,transport_type\n" +
                "\t,waybill_count\n" +
                "\t,goods_quantity\n" +
                "\t,weighing_method\n" +
                "\t,confirm_time\n" +
                "\t,sign_time\n" +
                "\t,cancel_reason\n" +
                "\t,special_address_no\n" +
                "\t,creator\n" +
                "\t,create_time\n" +
                "\t,modifier\n" +
                "\t,modify_time\n" +
                "\t,sender_unit_area_code\n" +
                "\t,receiver_unit_area_code\n" +
                "\t,sender_unit_area_name\n" +
                "\t,receiver_unit_area_name\n" +
                "\t,temperature_level_name\n" +
                "\t,intercept_batch\n" +
                "\t,intercept_time\n" +
                "\t,delivery_batch\n" +
                "\t,gross_weight\n" +
                "\t,animal_inspection_batch_no\n" +
                "\t,estimated_weight\n" +
                "\t,estimate_fee\n" +
                "\t,client_pickup_start\n" +
                "\t,client_pickup_end\n" +
                "\t,return_waybill_no\n" +
                "\t,special_flag\n" +
                "\t,imported_flag\n" +
                "\t,hidden_flag\n" +
                "\t,product_alias_code\n" +
                "\t,sender_dept_code\n" +
                "\t,receiver_dept_code\n" +
                "\t,cargo_name\n" +
                "\t,cargo_type\n" +
                "\t,declared_value_amt\n" +
                "\t,discount_ticket_no\n" +
                "\t,driver_no\n" +
                "\t,focus_code\n" +
                "\t,carton_number\n" +
                "\t,print_number\n" +
                "\t,board_print_flag\n" +
                "\t,bucket_flag\t\t\n" +
                "\t,data_source_flag\n" +
                "from \n" +
                "(select \n" +
                "\t*,row_number()over(partition by waybill_no order by data_source_flag) rn \n" +
                "from tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp01 where waybill_no is not  null and waybill_no!='') cur where cur.rn=1\n" +
                ";\n" +
                "\n" +
                "-- 写入结果表\n" +
                "insert overwrite table dwd_ly.dwd_ly_pub_order_dtl_di partition (inc_day)\n" +
                "select \n" +
                "\torder_no\n" +
                "\t,waybill_no\n" +
                "\t,erp_no\n" +
                "\t,case when product_code='T15' then 'SE0113'\n" +
                "\t\twhen product_code='T34' THEN 'SE0030'\n" +
                "\t\tWHEN product_code='T62' THEN 'SE003001'\n" +
                "\t\tELSE product_code END AS product_code\n" +
                "\t,CASE when product_code='T15' then '冷运标快'\n" +
                "\t\twhen product_code='T34' THEN '冷运零担'\n" +
                "\t\tWHEN product_code='T62' THEN '冷运小票零担'\n" +
                "\t\telse product_name end as product_name\n" +
                "\t,sender_province_code\n" +
                "\t,sender_province_name\n" +
                "\t,sender_city_code\n" +
                "\t,sender_city_name\n" +
                "\t,sender_area_code\n" +
                "\t,sender_area_name\n" +
                "\t,sender_city_number\n" +
                "\t,sender_detail_address\n" +
                "\t,sender_website_code\n" +
                "\t,sender_website_name\n" +
                "\t,pickup_courier_name\n" +
                "\t,pickup_courier_code\n" +
                "\t,pickup_courier_mobile\n" +
                "\t,sender_company\n" +
                "\t,sender_name\n" +
                "\t,sender_mobile\n" +
                "\t,require_pickup_start\n" +
                "\t,require_pickup_end\n" +
                "\t,monthly_account\n" +
                "\t,monthly_account_name\n" +
                "\t,payment_type_code\n" +
                "\t,payment_type\n" +
                "\t,settlement_type\n" +
                "\t,receiver_province_code\n" +
                "\t,receiver_province_name\n" +
                "\t,receiver_city_code\n" +
                "\t,receiver_city_name\n" +
                "\t,receiver_area_code\n" +
                "\t,receiver_area_name\n" +
                "\t,receiver_city_number\n" +
                "\t,receiver_detail_address\n" +
                "\t,receiver_website_code\n" +
                "\t,receiver_website_name\n" +
                "\t,deliverer_code\n" +
                "\t,deliverer_name\n" +
                "\t,deliverer_mobile\n" +
                "\t,receiver_company\n" +
                "\t,receiver_name\n" +
                "\t,receiver_mobile\n" +
                "\t,require_distribution_start\n" +
                "\t,require_distribution_end\n" +
                "\t,pickup_type\n" +
                "\t,distribution_type\n" +
                "\t,is_special_warehousing\n" +
                "\t,order_source_code\n" +
                "\t,source_code\n" +
                "\t,order_status\n" +
                "\t,pay_status\n" +
                "\t,sender_pay_status\n" +
                "\t,receiver_pay_status\n" +
                "\t,transport_status\n" +
                "\t,fee_status\n" +
                "\t,fee_status_remark\n" +
                "\t,batch_no\n" +
                "\t,order_type\n" +
                "\t,out_of_range\n" +
                "\t,user_id\n" +
                "\t,customer_id\n" +
                "\t,business_employee_no\n" +
                "\t,recommend_no\n" +
                "\t,is_special_order\n" +
                "\t,freight_amt\n" +
                "\t,fee_total\n" +
                "\t,fix_price\n" +
                "\t,meterage_weight\n" +
                "\t,dimensional_weight\n" +
                "\t,order_remark\n" +
                "\t,transport_type\n" +
                "\t,waybill_count\n" +
                "\t,goods_quantity\n" +
                "\t,weighing_method\n" +
                "\t,confirm_time\n" +
                "\t,sign_time\n" +
                "\t,cancel_reason\n" +
                "\t,special_address_no\n" +
                "\t,creator\n" +
                "\t,create_time\n" +
                "\t,modifier\n" +
                "\t,modify_time\n" +
                "\t,sender_unit_area_code\n" +
                "\t,receiver_unit_area_code\n" +
                "\t,sender_unit_area_name\n" +
                "\t,receiver_unit_area_name\n" +
                "\t,temperature_level_name\n" +
                "\t,intercept_batch\n" +
                "\t,intercept_time\n" +
                "\t,delivery_batch\n" +
                "\t,gross_weight\n" +
                "\t,animal_inspection_batch_no\n" +
                "\t,estimated_weight\n" +
                "\t,estimate_fee\n" +
                "\t,client_pickup_start\n" +
                "\t,client_pickup_end\n" +
                "\t,return_waybill_no\n" +
                "\t,special_flag\n" +
                "\t,imported_flag\n" +
                "\t,hidden_flag\n" +
                "\t,product_alias_code\n" +
                "\t,sender_dept_code\n" +
                "\t,receiver_dept_code\n" +
                "\t,cargo_name\n" +
                "\t,cargo_type\n" +
                "\t,declared_value_amt\n" +
                "\t,discount_ticket_no\n" +
                "\t,driver_no\n" +
                "\t,focus_code\n" +
                "\t,carton_number\n" +
                "\t,print_number   \n" +
                "\t,case when board_print_flag='NOT_NEED_PRINT' then null \n" +
                "\t\twhen board_print_flag='NO_ENTRY' then '未录入'\n" +
                "\t\twhen board_print_flag='UNPRINTED' then '未打印'\n" +
                "\t\twhen board_print_flag='PRINTED' then '已打印'\n" +
                "\t\telse board_print_flag end as board_print_flag\n" +
                "\t,bucket_flag\t\t\n" +
                "\t,data_source_flag\n" +
                "\t,regexp_replace(substr(create_time,0,10),'-','') as inc_day\n" +
                "from tmp_dwd_ly.dwd_ly_pub_order_dtl_di_temp02 ;\n" +
                "\n" +
                "\n" +
                "drop table if exists tmp_dwd_ly.dwd_ly_pub_order_dtl_di_etl;\n" +
                "create table tmp_dwd_ly.dwd_ly_pub_order_dtl_di_etl stored as parquet as \n" +
                "select \n" +
                "*\n" +
                "from   dwd_ly.dwd_ly_pub_order_dtl_di where inc_day between '${day_before_20d}' and '${day_before_1d}';\n";
        //  String s = "CREATE EXTERNAL TABLE `dim.dim_stay_why_info`( `stay_why_code` string COMMENT '滞留原因代码', `stay_why_name` string COMMENT '滞留原因描述说明', `bill_stay_why_id` string COMMENT '序列号', `created_emp_code` string COMMENT '创建员工号', `created_tm` string COMMENT '创建时间', `modified_emp_code` string COMMENT '修改员工号', `modified_tm` string COMMENT '修改时间', `valid_flg` string COMMENT '有效标志， 0-无效 1-有效', `stay_why_name_en` string COMMENT '滞留原因英文描述说明', `stay_why_type` string COMMENT '原因类型：1-通用，2-关务', `stay_why_desc` string COMMENT '异常原因描述') ROW FORMAT SERDE 'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe' STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'";
        System.out.println(s);
        String[] split = s.split(";");
        for(String one:split){
            SqlBaseLexer lexer = new SqlBaseLexer(new UpperCaseCharStreamJava(CharStreams.
                    fromString(one)));
            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // create a parser that feeds off the tokens buffer
            SqlBaseParser parser = new SqlBaseParser(tokens);
            //     System.out.printf(parser.statement().toStringTree(parser));
            ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
            parseTreeWalker.walk(new ShowUsedTable(),parser.singleStatement());
        }

    }
}
