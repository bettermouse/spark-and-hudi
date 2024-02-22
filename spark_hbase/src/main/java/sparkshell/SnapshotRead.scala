package sparkshell

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableSnapshotInputFormat, TableSnapshotInputFormatImpl}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkConf, SparkContext}

import java.io.File
import java.util.Base64

/**

spark.driver.extraLibraryPath=/opt/hadoop/lib/native
spark.executor.extraLibraryPath=/opt/hadoop/lib/native
spark.hadoopRDD.ignoreEmptySplits=false
--num-executors 2   --executor-memory 40g  --executor-cores 100

 bin/spark-shell --master yarn --conf spark.hadoopRDD.ignoreEmptySplits=false --conf spark.driver.extraLibraryPath=/usr/local/service/hadoop/lib/native  \
 --conf spark.executor.extraLibraryPath=/usr/local/service/hadoop/lib/native  --num-executors 3   --executor-memory 40g  --executor-cores 100






 */
object SnapshotRead {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setAppName("XXX").set("spark.hadoopRDD.ignoreEmptySplits", "false")
    val sc = new SparkContext(sparkConf);
    val conf = new Configuration();
    conf.addResource(new File("/usr/local/service/hbase/conf/hbase-site.xml").toURI().toURL());
    conf.addResource(new File("/usr/local/service/hadoop/etc/hadoop/core-site.xml").toURI().toURL());
//    conf.addResource("hbase-site.xml");
//    conf.set("dfs.nameservices", "XX8003100");
//    conf.set("dfs.ha.namenodes.XX8003100", "nn1,nn2");
//    conf.set("dfs.namenode.rpc-address.XX8003100.nn1", "10.0.7.17:4007");
//    conf.set("dfs.namenode.rpc-address.XX8003100.nn2", "10.0.7.5:4007");
//    conf.set("dfs.client.failover.proxy.provider.XX8003100", "org.apache.hadoop.XX.server.namenode.ha.ConfiguredFailoverProxyProvider");

    val scan = new Scan();
//    scan.setCaching(50000);
//    scan.setBatch(50000);
//    scan.setCacheBlocks(true); // 启动cache blocks
//    scan.setCacheBlocks(false); //必须设置为false
    conf.set(TableInputFormat.SCAN, Bytes.toString(Base64.getEncoder.encode(ProtobufUtil.toScan(scan).toByteArray)));
    val snapshotName ="my_sanpshot";
    TableSnapshotInputFormatImpl.setInput(conf, snapshotName, new Path("/tmp"));
    sc.newAPIHadoopRDD(conf, classOf[TableSnapshotInputFormat],classOf[ImmutableBytesWritable], classOf[Result])
      .mapPartitionsWithIndex((index,it)=>{
        var count:Long =0;
        val l = System.currentTimeMillis()
        while(it.hasNext){
          val tuple = it.next()
          count=count+1
          if(count%10000==0){
            println("index "+ index +"  count "+count+"    time "+((System.currentTimeMillis()-l)/1000))
          }
        }

        List(count).toIterator
      }).sum();
    Thread.sleep(100000);
    sc.stop();
  }
}
