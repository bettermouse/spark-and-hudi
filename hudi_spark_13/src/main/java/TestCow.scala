import java.io.File

import hudi.SparkUtil
import org.apache.commons.io.FileUtils
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.{DataGenerator, convertToStringList, getQuickstartWriteConfigs}
import org.apache.hudi.config.{HoodieIndexConfig, HoodieWriteConfig}
import org.apache.hudi.index.HoodieIndex.IndexType.BUCKET
import org.apache.hudi.keygen.constant.KeyGeneratorOptions
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConversions._
import scala.collection.mutable


class TestCow extends AnyFunSuite {

  val dataGen = new DataGenerator
  val spark = SparkSession.builder()
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog","org.apache.spark.sql.hudi.catalog.HoodieCatalog")
    .master("local[*]").getOrCreate()
  val tableName = "hudi_trips_cow1"
  var basePath = TestConf.tablePath+"\\"+tableName

  import spark.implicits._



  test("append data") {
  val inserts = convertToStringList(dataGen.generateInserts(10))
  val df = spark.read.json(spark.sparkContext.parallelize(inserts, 2))
df.schema
   HoodieWriteConfig
      .newBuilder()
      .withPreCombineField("ts")
      .forTable(tableName)
  df.show()

  df.write.format("hudi")
    .options(getQuickstartWriteConfigs)
    //Field used in preCombining before actual write
 //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
    //table name
    .option(HoodieWriteConfig.TBL_NAME.key(),tableName)
    //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
   .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"uuid")
    //hoodie.datasource.write.partitionpath.field
    .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
    .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
   // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
    .option(HoodieIndexConfig.INDEX_TYPE.key(), BUCKET.name)
    .option(HoodieIndexConfig.BUCKET_INDEX_NUM_BUCKETS.key(),2)
    .mode(SaveMode.Append)
    .save(basePath)
}

  test("print"){
    val stringToString = mutable.Map.empty ++ Map(PARTITIONPATH_FIELD.key -> "aaa")
    val s ="ok";
    val s1="ok1";
    print(s"$s:$s1")
  }


  test("select data") {

    spark.read.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .load("G:\\huditmp\\scs_aios\\student1").show()
  }

  test("spark sql craete table"){
    spark.sql("create table hudi_cow_nonpcf_tbl (\n  uuid int,\n  name string,\n  price double\n) using hudi\ntblproperties (\n  primaryKey = 'uuid'\n);")
  }

  test("spark sql select table"){
    spark.sql("select * from hudi_cow_nonpcf_tbl")
  }


  test("RECONCILE_SCHEMA drop field"){
    FileUtils.deleteDirectory(new File("G:\\huditmp\\"+"RECONCILE_SCHEMA"))
    val df1 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3,4), (4, 5, 6,7)).toDF("a", "b", "c","ts"))
    df1.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
//      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .option(RECONCILE_SCHEMA.key(),"true")
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    val df2 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3), (4, 5, 6)).toDF("a", "b","ts"))
    df2.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .option(RECONCILE_SCHEMA.key(),"true")
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    spark.read.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .load(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA").show()
  }

  test("RECONCILE_SCHEMA add field"){
    FileUtils.deleteDirectory(new File("G:\\huditmp\\"+"RECONCILE_SCHEMA"))
    val df1 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3,4), (9, 9, 9,9)).toDF("a", "b", "c","ts"))
    df1.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .option(RECONCILE_SCHEMA.key(),"true")
   //   .option(HoodieWriteConfig.SCHEMA_ALLOW_AUTO_EVOLUTION_COLUMN_DROP.key(),"true")
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    val df2 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3,5), (8, 8, 8,8)).toDF("a", "b","ts","d"))
    df2.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .option(RECONCILE_SCHEMA.key(),"true")
      //.option(HoodieWriteConfig.SCHEMA_ALLOW_AUTO_EVOLUTION_COLUMN_DROP.key(),"true")
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    spark.read.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .load(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA").show()
  }


  test("test sample  add field"){
    FileUtils.deleteDirectory(new File("G:\\huditmp\\"+"RECONCILE_SCHEMA"))
    val df1 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3), (4, 5, 6),(9,9,9)).toDF("a","b","ts"))
    df1.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    val df2 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3,5), (4, 5, 6,10)).toDF("d", "b","ts","a"))
    df2.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    spark.read.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .load(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA").show()
  }

  test("test sample  drop field"){
    FileUtils.deleteDirectory(new File("G:\\huditmp\\"+"RECONCILE_SCHEMA"))
    val df1 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3), (4, 5, 6),(9,9,9)).toDF("a","b","ts"))
    df1.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    val df2 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2), (4, 5)).toDF("a", "ts"))
    df2.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    spark.read.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .load(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA").show()
  }


  test("test sample  drop field and add field"){
    FileUtils.deleteDirectory(new File("G:\\huditmp\\"+"RECONCILE_SCHEMA"))
    val df1 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2, 3), (4, 5, 6),(9,9,9)).toDF("a","b","ts"))
    df1.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    val df2 =  SparkUtil.setNullableStateForAllColumns(Seq((1, 2,10), (4, 5,11)).toDF("d", "ts","e"))
    df2.write.format("hudi")
      .options(getQuickstartWriteConfigs)
      //Field used in preCombining before actual write
      //   .option(HoodieWriteConfig.PRECOMBINE_FIELD_NAME.key(),"ts")
      //table name
      .option(HoodieWriteConfig.TBL_NAME.key(),"RECONCILE_SCHEMA")
      //Record key field. Value to be used as the `recordKey` component of `HoodieKey`
      .option(KeyGeneratorOptions.RECORDKEY_FIELD_NAME.key(),"a")
      //hoodie.datasource.write.partitionpath.field
      //      .option( KeyGeneratorOptions.PARTITIONPATH_FIELD_NAME.key(),"partitionpath")
      .option(TABLE_TYPE.key(),MOR_TABLE_TYPE_OPT_VAL)
      // .option(OPERATION.key(),WriteOperationType.UPSERT.value)
      .mode(SaveMode.Append)
      .save(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA")

    spark.read.format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
      .load(TestConf.tablePath+"\\"+"RECONCILE_SCHEMA").show()
  }

  test("xx"){
    var x= Array(1, 2, 3, 3, 3);
    x.groupBy(identity)
  }
}
