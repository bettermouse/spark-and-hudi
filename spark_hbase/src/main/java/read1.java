//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.conf.Configuration
//import org.apache.hadoop.hbase._
//import org.apache.hadoop.mapreduce.Job
//import org.apache.hadoop.hbase.client.Scan
//import org.apache.hadoop.hbase.mapreduce.{TableInputFormat,TableSnapshotInputFormat}
//import org.apache.hadoop.hbase.protobuf.Protobuf
//Utilimport org.apache.spark.{SparkConf,SparkContext}
//import org.apache.hadoop.hbase.util.{Base64,Bytes};
//object SparkReadS
//        napshotDemo{
//    //hbase cluster zk quorum val HBASE_ZOOKEEPER_QUORUM ="ip1,ip2,ip3"
//        def main(args:Array[String]){
//
//            val conf =newSparkConf().setAppName("SparkReadHBaseSnapshotDemo2")
//        .set("spark.serializer","org.apache.spark.serializer.KryoSerializer").setMaster("local") val sc =newSparkContext(conf)
//
//        val job =Job.getInstance(getHbaseConf())TableSnapshotInputFormat.setInput(job,"test-snapshot",newPath("/tmp/TestTable"))
//        val hbaseRDD = sc.newAPIHadoopRDD(job.getConfiguration, classOf[TableSnapshotInputFormat],
//        classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], classOf[org.apache.hadoop.hbase.client.Result])
//
//        hbaseRDD.map(_._2).map(getRes(_)).count()}
//
//        def getRes(result: org.apache.hadoop.hbase.client.Result):String={
//                val
//        } rowkey =Bytes.toString(result.getRow()) val name =Bytes.toString(result.getValue("info".getBytes,"name".getBytes))
//        println(rowkey +"---"+ name) name}def getHbaseConf():Configuration={ v
//        al conf =HBaseConfiguration.create()
//        //zk port,default value:2181 conf.set("hbase.zookeeper.property.clientPort","2181")
//        // conf.set("zookeeper.znode.parent","/hbase") conf.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM)
//        // conf.set("hbase.rootdir","/hbase") conf.set(TableInputFormat.INPUT_TABLE,"TestTable")/
//        // /activite namenode ip : port conf.set("fs.defaultFS","XX://172.21.255.14:4007")
//        // conf.set(TableInputFormat.SCAN, getScanStr()) conf}def getScanStr():String={
//        // val scan =newScan()
//        // val proto =ProtobufUtil.toScan(scan)
//        // Base64.encodeBytes(proto.toByteArray()
//        // )}}
//        }