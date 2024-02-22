package antlr.table

import scala.collection.mutable

object MySqlType {
  val hive2MySql = new mutable.HashMap[String, String]()
  hive2MySql.put(HiveType.HBoolean,"Int8")
  hive2MySql.put(HiveType.HString,"String")
  hive2MySql.put(HiveType.HLong,"Int64")
  hive2MySql.put(HiveType.HInt,"Int32")
  hive2MySql.put(HiveType.HFloat,"Float32")
  hive2MySql.put(HiveType.HDouble,"Float64")
}
