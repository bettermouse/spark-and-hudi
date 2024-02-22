import com.twitter.chill.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableSnapshotInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableSnapshotInputFormatImpl;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.IOException;
import java.util.List;

public class read2 {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        conf.setQuietMode(true);
//        conf.addResource("core-site.xml");
//        conf.addResource("hbase-default.xml");
//        conf.addResource("XX-site.xml");
        conf.addResource("hbase-site.xml");
        conf.set("fs.defaultFS","XX://10.0.7.34:4007");
        conf.set("hbase.rootdir","XX://10.0.7.34:4007/hbase");
        System.out.printf(""+conf.get("emr.temrfs.tmp.cache.dir"));
        //     conf.set("mapreduce.output.fileoutputformat.outputdir", "/tmp");
        final SparkConf sparkConf = new SparkConf().set("spark.hadoopRDD.ignoreEmptySplits","false").setAppName("XXX");
        final JavaSparkContext sc = new JavaSparkContext(sparkConf);

        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false); //必须设置为false
        conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()));
        String snapshotName = args[1];
        TableSnapshotInputFormatImpl.setInput(conf, snapshotName, new Path(args[0]));
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sc.newAPIHadoopRDD(conf, TableSnapshotInputFormat.class, ImmutableBytesWritable.class, Result.class);
        long x =hbaseRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, String>() {
            @Override
            public String call(Tuple2<ImmutableBytesWritable, Result> v1) throws Exception {
                byte[] Temp = v1._1.get();
                String str  =Bytes.toString(v1._2.getRow());
                return "";
            }
        }).count();
        System.out.print(""+x);
        Thread.sleep(100000);
        sc.stop();

    }
}