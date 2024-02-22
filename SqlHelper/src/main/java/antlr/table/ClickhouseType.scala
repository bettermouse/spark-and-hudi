package antlr.table

import org.apache.commons.lang3.StringUtils

import scala.collection.mutable


object ClickhouseType {
val hive2Clickhouse = new mutable.HashMap[String, String]()
  hive2Clickhouse.put(HiveType.HBoolean,"Int8")
  hive2Clickhouse.put(HiveType.HString,"String")
  hive2Clickhouse.put(HiveType.HLong,"Int64")
  hive2Clickhouse.put(HiveType.HInt,"Int32")
  hive2Clickhouse.put(HiveType.HFloat,"Float32")
  hive2Clickhouse.put(HiveType.HDouble,"Float64")
  hive2Clickhouse.put(HiveType.HBigint,"Int64")



  def getChTableSql(table:Table):String={
      val sb = new StringBuilder()
      val partitionSb = new StringBuilder()
    //table.database
     val dbName="scs_operation";
      sb.append(String.format("create table  %s ",dbName + "." + table.name))
        .append(" on cluster `default` ")
      sb.append(" ( ")
      val fields = table.fields
      for (field <- fields) {
        val chTYpe = hive2Clickhouse.getOrElse(field.hType,"error")
        sb.append(" " + field.name).append(" " +chTYpe ).append(" comment")
          .append(" ").append(if(StringUtils.isEmpty(field.comment)){"''"}else{field.comment}).append(",")
        if (field.partition == false) {

        } else {
          partitionSb.append(" " + field.name).append(",")
        }
      }
      if (sb.length > 0 && (sb.charAt(sb.length - 1) == ',')) sb.deleteCharAt(sb.length - 1)
      sb.append(" ) ")
      sb.append(String.format("  ENGINE = ReplicatedMergeTree('/clickhouse/PUBLIC/tables/%s/%s/{shard}','{replica}')",dbName,table.name))

    if (partitionSb.length > 0 && (partitionSb.charAt(partitionSb.length - 1) == ',')) partitionSb.deleteCharAt(partitionSb.length - 1)
    val partitionStr = partitionSb.toString()
      if (StringUtils.isNotBlank(partitionStr)) {
        sb.append(" ").append("order by ").append(partitionStr)
      }
    sb.append(" ")
      .append("PARTITION by  inc_day")
//ENGINE = ReplicatedMergeTree('/clickhouse/PUBLIC/tables/scs_operation/dm_order_ld_terminal_monitor/{shard}',
    // '{replica}') PARTITION BY toYYYYMMDD(data_day) ORDER BY (data_day,
    // sender_website_code)

    //  sb.append("  PARTITIONED BY ( `inc_day` string) stored as parquet;")
      sb.toString()
  }

}
