package jsonQueryServer;


import com.alibaba.fastjson.JSON;

import javax.xml.bind.SchemaOutputResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestQueryServerJson {
    static String url ="http://114.132.68.187:8765";
    static AtomicInteger id =new AtomicInteger(10);

    static class Query{


        public void run(){

            try {
                Map<String, String> objectObjectMap = new HashMap<>();
                objectObjectMap.put("request","openConnection");
                String value = "d"+String.valueOf(id.incrementAndGet());
               // id.incrementAndGet();
                objectObjectMap.put("connectionId", value);
                String open = HttpUtil.doPost(url, JSON.toJSONString(objectObjectMap));
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
              //  System.out.printf(""+fetchStr);





                objectObjectMap.put("request","closeConnection");
                HttpUtil.doPost(url, JSON.toJSONString(objectObjectMap));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

    }


    public static void main(String[] args) throws InterruptedException {
        if(args.length>0){
            url=args[0];
        }
        int thread =1;
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        for(int i=0;i<thread;i++){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        new Query().run();
                    }
                }
            });

        }
        executor.awaitTermination(10000, TimeUnit.DAYS);

        new Query().run();
    }

}
