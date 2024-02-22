package spark;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import cdc.BinlogOffset;
import cdc.MySqlSourceConfig;
import hudi.SparkTableUtil;
import io.debezium.relational.Table;
import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.InternalRow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WriteHudiTable {

    private SparkSession spark;
    public JavaSparkContext javaSparkContext;
    private Map<TableId, TableChanges.TableChange> tableSchemas;
    private MySqlSourceConfig mySqlSourceConfig;



    public WriteHudiTable(Map<TableId, TableChanges.TableChange> tableSchemas, MySqlSourceConfig config) {
        spark = SparkSession.builder()
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
                .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.hudi.catalog.HoodieCatalog")
                .master("local[*]").getOrCreate();
        javaSparkContext = new JavaSparkContext(spark.sparkContext());
        this.tableSchemas = tableSchemas;
        this.mySqlSourceConfig = config;
    }


    public void loadOneTable(Map.Entry<TableId, TableChanges.TableChange> entry,BinlogOffset initBinlogOffset){
        TableChanges.TableChange value = entry.getValue();
        TableId tableId = value.getId();
        String database = tableId.catalog();
        String tableName = tableId.table();
        Table table = value.getTable();
        String url = String.format("jdbc:mysql://%s:%s",mySqlSourceConfig.getHostname(),mySqlSourceConfig.getPort());
        SparkTableUtil.initOneTable(spark,database,tableName,url
                ,mySqlSourceConfig.getUsername(),mySqlSourceConfig.getPassword()
        ,initBinlogOffset);
    }

    public void loadAllTable(BinlogOffset initBinlogOffset){
        for(Map.Entry<TableId, TableChanges.TableChange> entry: tableSchemas.entrySet()){
            loadOneTable(entry,initBinlogOffset);
        }
    }

    public static void main(String[] args) {
//        WriteHudiTable writeHudiTable = new WriteHudiTable();
//        SparkBatchUpdate sparkBatchUpdate = new SparkBatchUpdate();
//        List<InternalRow> list = sparkBatchUpdate.getList();
//        JavaSparkContext javaSparkContext = writeHudiTable.javaSparkContext;
//        JavaRDD<InternalRow> parallelize = javaSparkContext.parallelize(list);
//        writeHudiTable.spark.internalCreateDataFrame(parallelize.rdd(),null,false);
      //  writeHudiTable.spark.createDataFrame()
       // Dataset<String> ds = writeHudiTable.spark.createDataset(data, Encoders.STRING())
         //       Enco
    }




}
