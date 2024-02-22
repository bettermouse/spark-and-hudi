import com.twitter.chill.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableSnapshotInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableSnapshotInputFormatImpl;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import scala.Tuple2;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;


public class CopyRead {

    public static void main(String[] args) throws IOException, InterruptedException {
        final SparkConf sparkConf = new SparkConf().setAppName("XXX").set("spark.hadoopRDD.ignoreEmptySplits","false");
        final JavaSparkContext sc = new JavaSparkContext(sparkConf);
        Configuration conf = new Configuration();
        conf.addResource("hbase-site.xml");
        conf.set("dfs.nameservices", "XX8003100");
        conf.set("dfs.ha.namenodes.XX8003100", "nn1,nn2");
        conf.set("dfs.namenode.rpc-address.XX8003100.nn1", "10.0.7.17:4007");
        conf.set("dfs.namenode.rpc-address.XX8003100.nn2", "10.0.7.5:4007");
        conf.set("dfs.client.failover.proxy.provider.XX8003100", "org.apache.hadoop.XX.server.namenode.ha.ConfiguredFailoverProxyProvider");
//        conf.set("hbase.zookeeper.quorum", "XXX");
//        conf.set("hbase.zookeeper.property.clientPort", "XXX");
//        System.out.printf("----------"+conf.get("hbase.zookeeper.quorum"));

        Scan scan = new Scan();
        scan.setCaching(50000);
        scan.setBatch(50000);
        scan.setCacheBlocks(true); // 启动cache blocks
        scan.setCacheBlocks(false); //必须设置为false
        conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()));
        String snapshotName = args[1];
        TableSnapshotInputFormatImpl.setInput(conf, snapshotName, new Path(args[0]));
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sc.newAPIHadoopRDD(conf, TableSnapshotInputFormat.class, ImmutableBytesWritable.class, Result.class);

        long x =hbaseRDD
                .mapPartitions(new FlatMapFunction<Iterator<Tuple2<ImmutableBytesWritable, Result>>, String>() {
                    LinkedList<String> linkedList = new LinkedList<String>();
                    @Override
                    public Iterator<String> call(Iterator<Tuple2<ImmutableBytesWritable, Result>> tuple2Iterator) throws Exception {
                        int i =0;
                        while (tuple2Iterator.hasNext()){
                            Tuple2<ImmutableBytesWritable, Result> next = tuple2Iterator.next();
                            i++;
                            if(i%1000==0){
                                System.out.println(""+i);
                            }
                        }
                        return linkedList.iterator();
                    }
                }).count();

        System.out.print("count"+x);
        Thread.sleep(100000);
        sc.stop();

    }
}