package test;

import org.apache.hbase.thirdparty.org.apache.commons.cli.CommandLine;
import org.apache.hbase.thirdparty.org.apache.commons.cli.DefaultParser;
import org.apache.hbase.thirdparty.org.apache.commons.cli.Options;

import java.io.File;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("m", true, "read/write/scan");
        options.addOption("t", true, "hbase table name");
        options.addOption("c", true, "thread count");
//        options.addOption("b", true, "batch load per write");
//        options.addOption("f", true, "test data file");
        options.addOption("d", true, "test data dir");
//        options.addOption("p", true, "list file which match prefix in data dir");
//        options.addOption("w", true, "plus data");
//        options.addOption("l", true, "scan limit");
        CommandLine cmd = (new DefaultParser()).parse(options, args);
        int concurrency = Integer.parseInt(cmd.getOptionValue("c", "10"));
        String testFile = cmd.getOptionValue("f");
        String d = cmd.getOptionValue("d");
        String[] ds = new File(d).list();

        Iterator<String> itr = Arrays.stream(ds).iterator();
        String testMethod = cmd.getOptionValue("m", "read");
        String tableName = cmd.getOptionValue("t", "vehicle");
        int batchWrite = Integer.parseInt(cmd.getOptionValue("b", "1"));
        int dataPlus = Integer.parseInt(cmd.getOptionValue("w", "1"));
        String[] keyPrefix = new String[dataPlus];

        for(int i = 0; i < dataPlus; ++i) {
            keyPrefix[i] = String.format("%03d", i);
        }

        Map<String, Object> res = null;
        switch (testMethod) {
            case "read":
                res = (new SingleRead(itr, concurrency, tableName,d)).call();
                break;
//            case "write":
//                if (batchWrite <= 1) {
//                    res = (new SingleWrite(itr, concurrency, tableName, batchWrite, keyPrefix)).call();
//                } else {
//                    res = (new BatchWrite(itr, concurrency, tableName, batchWrite, keyPrefix)).call();
//                }
//                break;
//            case "scan":
//                (new ScanN(tableName, concurrency, Integer.parseInt(cmd.getOptionValue("l", "0")))).call();
//                break;
            default:
                throw new RuntimeException("Unsupported test command: " + testMethod);
        }

        if (res != null) {
            printRes(res, concurrency);
        }

    }

    public static void printRes(Map<String, Object> res, int concurrency) {
        List<List<Long>> timeList = (List)res.get("cost_list");
        List<Long> flatList = new ArrayList();
        Iterator lt = timeList.iterator();

        while(lt.hasNext()) {
            List<Long> l = (List)lt.next();
            flatList.addAll(l);
        }
        //所有的时间排序
        flatList.sort(Long::compareTo);
        long costTotal = (Long)res.get("cost_total");
        long costOp = (Long)flatList.stream().reduce(0L, Long::sum);
        System.out.println("cost_total: " + costTotal + ", cost_op: " + costOp / (long)concurrency);
        System.out.println("qps: " + (double)flatList.size() / ((double)costOp / 1000.0 / (double)concurrency + 1.0));
        System.out.println("avg: " + costOp / (long)flatList.size());
        System.out.println("p999: " + percentile(flatList, 0.999));
        System.out.println("p99: " + percentile(flatList, 0.99));
        System.out.println("p95: " + percentile(flatList, 0.95));
        System.out.println("p90: " + percentile(flatList, 0.9));
        System.out.println("p75: " + percentile(flatList, 0.75));
    }

    public static long percentile(List<Long> latencies, double p) {
        int index = (int)Math.ceil(p * (double)latencies.size());
        return (Long)latencies.get(index - 1);
    }
}
