package spark;

import org.apache.hudi.org.apache.hadoop.hbase.security.User;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;

public class TestSparkAs {


    SparkSession spark = SparkSession.builder()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.hudi.catalog.HoodieCatalog")
            .master("local[*]").getOrCreate();

    @Test
    public void testStrangerAs() {
        Dataset<Row> text = spark.read().text("G:\\test\\test.csv");

    }
}
