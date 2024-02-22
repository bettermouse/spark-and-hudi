package antlr.table


import org.apache.commons.lang3.StringUtils

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


case class Field(hType: String, var name: String, var comment: String, partition: Boolean = false, nullable: Boolean = true)

case class Table(var database: String, var name: String, var comment: String, var fields: ArrayBuffer[Field])


object HiveType {
  //  boolean	true/false	TRUE
  //  tinyint	1字节的有符号整数	-128~127 1Y
  //  smallint	2个字节的有符号整数，-32768~32767	1S
  //  int	4个字节的带符号整数	1
  //  bigint	8字节带符号整数	1L
  //  float	4字节单精度浮点数	1
  //  double	8字节双精度浮点数	1
  //  deicimal	任意精度的带符号小数	1
  //  String	字符串，变长	“a”,’b’
  //  varchar	变长字符串	“a”,’b’
  //  char	固定长度字符串	“a”,’b’
  //  binary	字节数组	无法表示
  //  timestamp	时间戳，纳秒精度	1.22327E+11
  //  date	日期	‘2018-04-07’

  val HBoolean = "boolean"
  val HString = "string"
  val HLong = "long"
  val HInt = "int"
  val HFloat = "float"
  val HDouble = "double"
  val HBigint = "bigint"
}

object TableHelper {

  def fillTableComment(oldTable: Table): Table = {
    val fields = oldTable.fields
    val allTable = LoadAllTable.getAllTable().asScala
    val name2Comment = new mutable.HashMap[String, String]()
    for (table <- allTable) {
      val tableFields = table.fields
      for (field <- tableFields) {
        name2Comment.put(field.name, field.comment)
      }
    }
    for (field <- fields) {
      if (StringUtils.isEmpty(field.comment)) {
        field.comment = name2Comment.getOrElse(field.name, "")
      }
    }
    oldTable.fields = fields
    oldTable
  }

  def getCreateTableSql(table: Table): String = {
    val sb = new StringBuilder()
    val partitionSb = new StringBuilder()
    sb.append(String.format("create table  %s ",table.database + "." + table.name))
    sb.append(" ( ")
    val fields = table.fields
    for (field <- fields) {
      if (field.partition == false) {
        sb.append(" " + field.name).append(" " + field.hType).append(" comment")
          .append(" ").append(field.comment).append(",")
      } else {
        partitionSb.append(" " + field.name).append(" " + field.hType).append(" comment")
          .append(" ").append(field.comment).append(",")
      }
    }
    if (sb.length > 0 && (sb.charAt(sb.length - 1) == ',')) sb.deleteCharAt(sb.length - 1)
    sb.append(" ) ")
    if (partitionSb.length > 0 && (partitionSb.charAt(partitionSb.length - 1) == ',')) partitionSb.deleteCharAt(partitionSb.length - 1)
    val partitionStr = partitionSb.toString()
    if (StringUtils.isNotBlank(partitionStr)) {
        sb.append("partitioned by ").append("(")
          .append(partitionStr.substring(0, partitionStr.length - 1))
          .append(")")
    }

    if (StringUtils.isNotBlank(table.comment)) {
      sb.append(" comment ").append(table.comment);
    }
    sb.append("  PARTITIONED BY ( `inc_day` string) stored as parquet;")
    sb.toString()
  }


}
