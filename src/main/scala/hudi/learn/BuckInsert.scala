/*
package hudi.learn

import org.apache.hudi.DataSourceWriteOptions
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils._
import org.apache.hudi.common.model.WriteOperationType
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConverters._
class BuckInsert  extends AnyFunSuite{
  val spark = SparkSession.builder()
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog","org.apache.spark.sql.hudi.catalog.HoodieCatalog")
    .master("local[*]").getOrCreate()
  val dataGen = new DataGenerator
  test("date gen"){
    val tableName = "hudi_trips_cow"
    val basePath = "file:///G:\\tmp\\hudi_trips_cow"
    val inserts = convertToStringList(dataGen.generateInserts(1000000))
    val df = spark.read.json(spark.sparkContext.parallelize(inserts.asScala, 2))
    df.write.format("hudi")
    //  .options(getQuickstartWriteConfigs)
      .option(DataSourceWriteOptions.RECORDKEY_FIELD.key(), "ts")
      .option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key(), "partitionpath")
      .option(DataSourceWriteOptions.PRECOMBINE_FIELD.key(), "uuid")
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .option(OPERATION.key(),WriteOperationType.BULK_INSERT.value())
      .mode(SaveMode.Append)
      .save(basePath);
    Thread.sleep(100000)
  }
}
*/
