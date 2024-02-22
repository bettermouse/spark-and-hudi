package hudi;

import cdc.BinlogOffset;
import com.google.common.collect.ImmutableMap;
import io.debezium.relational.Table;
import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hudi.DataSourceWriteOptions;
import org.apache.hudi.common.model.HoodieTableType;
import org.apache.hudi.common.table.HoodieTableConfig;
import org.apache.hudi.common.table.HoodieTableMetaClient;
import org.apache.hudi.config.HoodieWriteConfig;
import org.apache.spark.sql.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.apache.hudi.common.table.HoodieTableConfig.PAYLOAD_CLASS_NAME;

import scala.collection.JavaConverters;
import scala.collection.mutable.Buffer;

import static org.apache.hudi.keygen.constant.KeyGeneratorOptions.Config.*;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

public class SparkTableUtil extends TableUtil{


    public static void createTable(String database,String tableName,Table table, Schema schema) throws IOException {
        HoodieTableConfig hoodieConfig = new HoodieTableConfig();
        //hoodie.table.base.file.format  parquet
        String baseFileFormat = hoodieConfig.getStringOrDefault(HoodieTableConfig.BASE_FILE_FORMAT);
        //hoodie.archivelog.folder
        String  archiveLogFolder = hoodieConfig.getStringOrDefault(HoodieTableConfig.ARCHIVELOG_FOLDER);
        boolean populateMetaFields = hoodieConfig.getBooleanOrDefault(HoodieTableConfig.POPULATE_META_FIELDS);
        boolean useBaseFormatMetaFile = hoodieConfig.getBooleanOrDefault(HoodieTableConfig.PARTITION_METAFILE_USE_BASE_FORMAT);



        HoodieTableMetaClient.withPropertyBuilder()
                .setTableType(HoodieTableType.MERGE_ON_READ)
                .setDatabaseName(database)
                .setTableName(tableName)
                .setBaseFileFormat(baseFileFormat)
                .setArchiveLogFolder(archiveLogFolder)
                //PAYLOAD_CLASS_NAME
                .setPayloadClassName(hoodieConfig.getString(PAYLOAD_CLASS_NAME))
                // we can't fetch preCombine field from hoodieConfig object, since it falls back to "ts" as default value,
                // but we are interested in what user has set, hence fetching from optParams.
                //根据更新时间
                .setPreCombineField("modify_time")
                //根据创建时间分区
                .setPartitionFields("create_time")
                .setPopulateMetaFields(populateMetaFields)
                //key
                .setRecordKeyFields(hoodieConfig.getString(table.primaryKeyColumnNames().stream().map(String::valueOf).findFirst().get()))
                        //.collect(Collectors.joining(","))))
                .setRecordKeyFields(table.primaryKeyColumnNames().stream().map(String::valueOf).findFirst().get())
                        //table.primaryKeyColumnNames().stream().map(String::valueOf).collect(Collectors.joining(",")))

                //.setCDCEnabled(hoodieConfig.getBooleanOrDefault(HoodieTableConfig.CDC_ENABLED))
                //.setCDCSupplementalLoggingMode(hoodieConfig.getStringOrDefault(HoodieTableConfig.CDC_SUPPLEMENTAL_LOGGING_MODE))
               // .setKeyGeneratorClassProp(hoodieConfig.getString(DataSourceWriteOptions.KEYGENERATOR_CLASS_NAME.key))
               .setKeyGeneratorClassProp("org.apache.hudi.keygen.TimestampBasedKeyGenerator")
                .set(ImmutableMap.of(TIMESTAMP_INPUT_DATE_FORMAT_PROP,"yyyy-MM-dd hh:mm:ss",
                        TIMESTAMP_OUTPUT_DATE_FORMAT_PROP,"yyyy-MM-dd",
                        "hoodie.deltastreamer.keygen.timebased.timestamp.type","DATE_STRING",
                        "hoodie.datasource.write.recordkey.field","id"))
                // .set(timestampKeyGeneratorConfigs)
//                .setHiveStylePartitioningEnable(hoodieConfig.getBoolean(HIVE_STYLE_PARTITIONING))
//                .setUrlEncodePartitioning(hoodieConfig.getBoolean(URL_ENCODE_PARTITIONING))
          //      .setPartitionMetafileUseBaseFormat(useBaseFormatMetaFile)
              //  .setShouldDropPartitionColumns(hoodieConfig.getBooleanOrDefault(HoodieTableConfig.DROP_PARTITION_COLUMNS))
            //    .setCommitTimezone(HoodieTimelineTimeZone.valueOf(hoodieConfig.getStringOrDefault(HoodieTableConfig.TIMELINE_TIMEZONE)))
               // .initTable(sparkContext.hadoopConfiguration, path);



                .setTableCreateSchema(SchemaUtil.addMetaSchema(schema).toString())
                  .initTable(new Configuration(),"G:\\huditmp\\"+database+"\\"+tableName);

    }


    /**
     * select * from one_table;
     * // Note: JDBC loading and saving can be achieved via either the load/save or jdbc methods
     * // Loading data from a JDBC source
     * Dataset<Row> jdbcDF = spark.read()
     *   .format("jdbc")
     *   .option("url", "jdbc:postgresql:dbserver")
     *   .option("dbtable", "schema.tablename")
     *   .option("user", "username")
     *   .option("password", "password")
     *   .load();
     */
    public static void initOneTable(SparkSession spark,String database,
                                    String tableName,String url,
                                    String username,String password,
                                    BinlogOffset initBinlogOffset){
        String filename = initBinlogOffset.getFilename();
       // String substring = filename.substring(filename.lastIndexOf('.') + 1);
        Buffer<Column> columnBuffer = JavaConverters.asScalaBuffer(
                Arrays.asList(lit(filename),
                        lit(String.valueOf(initBinlogOffset.getPosition())),
                        lit("0"),
                        lit(null),
                        lit(filename+initBinlogOffset.getPosition())));
        Dataset<Row> jdbcDF = spark.read()
                .format("jdbc")
                .option("url", url)
                .option("dbtable", database+"."+tableName)
                .option("user", username)
                .option("password", password)
                .load();
        Dataset<Row> rowDataset = jdbcDF.withColumns(JavaConverters.asScalaBuffer(SchemaUtil.MYSQL_META_COLUMNS)
                , columnBuffer).limit(10000);
//        rowDataset.printSchema();
//        rowDataset.show();
        ImmutableMap<String, String> of = ImmutableMap.of(TIMESTAMP_INPUT_DATE_FORMAT_PROP, "yyyy-MM-dd hh:mm:ss",
                TIMESTAMP_OUTPUT_DATE_FORMAT_PROP, "yyyy-MM-dd",
                "hoodie.deltastreamer.keygen.timebased.timestamp.type", "EPOCHMILLISECONDS",
                "hoodie.datasource.write.recordkey.field", "id",
                "hoodie.deltastreamer.keygen.timebased.timestamp.scalar.time.unit","days");
        rowDataset.write()
                .format("hudi")
                .options(of)
                .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
                .mode(SaveMode.Append)
                .save("G:\\huditmp\\"+database+"\\"+tableName);
    }


}
