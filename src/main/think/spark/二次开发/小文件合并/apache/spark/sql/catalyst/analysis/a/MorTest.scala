package org.apache.spark.sql.catalyst.analysis.a


import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.{DataGenerator, convertToStringList, getQuickstartWriteConfigs}
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.hudi.config.HoodieWriteConfig._
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConversions._
/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 * ID   DATE               PERSON             REASON
 * 1   2022/4/20 10:46     01407273            Create
 *
 * *********************************************
 * </pre>
 */
class MorTest extends AnyFunSuite {


  test("insert"){
    val spark = SparkSession.builder()
      .master("local[*]")
     // .config("spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version",2)
      .getOrCreate()
    import spark.implicits._
    (1 to 1000).map(x=>(1, 2, 3)).toSeq
    (1 to 1000).map(x =>(x/20, x/10, x)).toSeq.toDF("x", "y", "z")
      .write.mode(SaveMode.Overwrite).partitionBy("x").parquet("Z:\\tmp\\spark")

  //  Thread.sleep(1000000)
  }

  test("select"){
    val spark = SparkSession.builder()
      .master("local[*]")
      // .config("spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version",2)
      .getOrCreate()
    spark.read.parquet("Z:\\tmp\\spark").show()
    val l = spark.read.parquet("Z:\\tmp\\spark").count()
    print("总共的数据个数为"+l)
    print()
  }
}

object MorTest{
   var open = true;
}
