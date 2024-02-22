//package hudi.test
//
//import org.apache.spark.sql.SparkSession
//import org.scalatest.funsuite.AnyFunSuite
//
//class rfcAvro   extends AnyFunSuite{
// // val dataGen = new DataGenerator
//  val spark = SparkSession.builder()
//    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//    .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
//    .config("schema.on.read.enable",true)
//    .config("spark.memory.offHeap.enabled",true)
//    .config("spark.memory.offHeap.size","1g")
//    .master("local[*]").getOrCreate()
//  val tableName = "hudi_trips_cow1"
//  var basePath = "file:///G:\\huditmp\\hudi1"
//
//  test("read"){
//    val frame = spark.read.format("hudi").load(basePath)
//    frame.show(1000)
//  }
//}
