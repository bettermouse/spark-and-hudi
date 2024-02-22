package jsonQueryServer;


import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestQueryServerJson1 {
    static String url ="http://114.132.68.187:8765";
    static AtomicInteger id =new AtomicInteger(10);

    static class Query{
        String value;

        public Query(String value) {
            this.value=value;
        }

        public void run(){

            try {

              //  System.out.printf(""+open);

                //查询数据
                Map<String, String> statement = new HashMap<>();
                statement.put("request","createStatement");
                statement.put("connectionId",value);
                String createStatement = HttpUtil.doPost(url, JSON.toJSONString(statement));
                String statementId = JSON.parseObject(createStatement).getString("statementId");
                Map<String, Object> execute = new HashMap<>();
                execute.put("request","prepareAndExecute");
                execute.put("connectionId",value);
                execute.put("statementId",Integer.valueOf(statementId));
                execute.put("sql","select * from STUDENT");
                execute.put("maxRowsInFirstFrame",1);
                execute.put("maxRowCount",3);
                String result = HttpUtil.doPost(url, JSON.toJSONString(execute));

                //fetch
                Map<String, Object> fetch = new HashMap<>();
                fetch.put("request","fetch");
                fetch.put("connectionId",value);
                fetch.put("statementId",statementId);
                fetch.put("offset",1);
                fetch.put("fetchMaxRowCount",3);
                String fetchStr = HttpUtil.doPost(url, JSON.toJSONString(fetch));
                System.out.printf(""+fetchStr);





//                objectObjectMap.put("request","closeConnection");
//                HttpUtil.doPost(url, JSON.toJSONString(objectObjectMap));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

    }


    public static void main(String[] args) throws InterruptedException {
        int thread =1000;
        if(args.length>0){
            url=args[0];
             thread = Integer.valueOf(args[1]);
        }

        ExecutorService executor = Executors.newFixedThreadPool(1000);
        for(int i=0;i<thread;i++){
            Map<String, String> objectObjectMap = new HashMap<>();
            objectObjectMap.put("request","openConnection");
            String value = "d"+String.valueOf(id.incrementAndGet());
            // id.incrementAndGet();
            objectObjectMap.put("connectionId", value);
            try {
                String open = HttpUtil.doPost(url, JSON.toJSONString(objectObjectMap));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        new Query(value).run();
                    }
                }
            });

        }
        executor.awaitTermination(10000, TimeUnit.DAYS);

       // new Query().run();
    }

}
