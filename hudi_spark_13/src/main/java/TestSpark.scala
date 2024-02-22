import java.io.File

import hudi.SparkUtil
import org.apache.commons.io.FileUtils
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.{DataGenerator, convertToStringList, getQuickstartWriteConfigs}
import org.apache.hudi.config.{HoodieIndexConfig, HoodieWriteConfig}
import org.apache.hudi.index.HoodieIndex.IndexType.BUCKET
import org.apache.hudi.keygen.constant.KeyGeneratorOptions
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{BooleanType, IntegerType, LongType, StructField, StructType}
import org.apache.spark.sql.{Encoders, Row, SaveMode, SparkSession}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConversions._
import scala.collection.mutable


class TestSpark extends AnyFunSuite {

  val dataGen = new DataGenerator
  val spark = SparkSession.builder()
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.hudi.catalog.HoodieCatalog")
    .master("local[*]").getOrCreate()
  val tableName = "hudi_trips_cow1"
  var basePath = TestConf.tablePath + "\\" + tableName

  import spark.implicits._


  test("row encoder") {
    val struct =
      StructType(
        StructField("a", IntegerType, true) ::
          StructField("b", LongType, false) ::
          StructField("c", BooleanType, false) :: Nil)
    val encoder = RowEncoder(struct)
    val serializer = encoder.createSerializer()
    val deserializer = encoder.createDeserializer()
    val row = Row(1, 1l, false)
    val serializerRow = serializer(row)
    val deserializer1 = encoder.resolveAndBind().createDeserializer()

   // resolveAndBind

//    val i = serializerRow.getInt(0)
//    val l = serializerRow.getLong(1)
//    val bool = serializerRow.getBoolean(2)
    val row1 = deserializer1(serializerRow)
    println(row1)
  }

  test("row encoder1") {
    import spark.implicits._
    import spark.implicits._
    val frame = Seq((1, 1l, false)).toDS().toDF("a", "b", "c")
    frame
      .map(x=>{
        x.getInt(0)
      }).show(3)
  }


  test("spark sql"){
  //  val df = Seq(("chen", 10, 30), ("wan", 45, 60)).toDF("name","age","score")
    spark.read.json("G:\\test\\student.txt").registerTempTable("student")
    //df.createOrReplaceTempView("student")
    val frame = spark.sql("select name from student where age>20")
    val logical = frame.queryExecution.logical
    frame.show();
 //   frame.explain("codegen")
  }

}
