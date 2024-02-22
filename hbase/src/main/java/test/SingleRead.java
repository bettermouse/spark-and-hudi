package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;

public class SingleRead implements Callable<Map<String, Object>> {
    String tableName;
    final Iterator<String> fileIterator;
    ExecutorService pool;
    Connection connection;
    int concurrency;
    String d;

    public SingleRead(Iterator<String> keyItr, int concurrency, String tableName,String d) throws IOException {
        this.fileIterator = keyItr;
//        Configuration config = HBaseConfiguration.create();
//        this.connection = ConnectionFactory.createConnection(config);
        this.concurrency = concurrency;
        this.pool = Executors.newFixedThreadPool(concurrency);
        this.tableName = tableName;
        this.d = d;
    }
    public Map<String, Object> call() throws Exception {
        List<Future<List<Long>>> report = new ArrayList(this.concurrency);
        long t0 = System.currentTimeMillis();

        for(int i = 0; i < this.concurrency; ++i) {
            List<Long> ct = new LinkedList();
            Future<List<Long>> ctf = this.pool.submit(() -> {
                try {
                    Table table;
                    try {
                        //     table = this.connection.getTable(TableName.valueOf(this.tableName));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    while(true) {
                        String file = null;
                        synchronized(this.fileIterator) {
                            if (!this.fileIterator.hasNext()) {
                                return ct;
                            }
                            file = (String)this.fileIterator.next();
                        }
                        ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(new Path(d+ File.separatorChar+file)).build();
                        GenericRecord read = reader.read();
                        while (read!=null){
                            // System.out.println(""+read.toString());

                            String s = (String) (read.get(0)).toString();
                            long startTime = System.currentTimeMillis();
                            Get get = new Get((s).getBytes());
                            read= reader.read();
                            //    table.get(get);
                            ct.add(System.currentTimeMillis() - startTime);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return  ct;
            });
            report.add(ctf);
        }

        List<List<Long>> costList = new ArrayList(this.concurrency);

        for(int i = 0; i < this.concurrency; ++i) {
            System.out.println("--------------------"+i );
            costList.add((List)((Future)report
                    .get(i))
                    .get());
        }

        long costTotal = System.currentTimeMillis() - t0;
        this.pool.shutdownNow();
        Map<String, Object> res = new HashMap();
        res.put("cost_list", costList);
        res.put("cost_total", costTotal);
        return res;
    }
}
