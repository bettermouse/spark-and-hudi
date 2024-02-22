import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;

import java.io.IOException;

public class SimpleClient {
    public static void main(String[] args) {
        BinaryLogClient client = new BinaryLogClient("43.139.40.82", 3306, "root", "root");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(new BinaryLogClient.EventListener() {

            @Override
            public void onEvent(Event event) {
                EventData data = event.getData();
                if (data instanceof TableMapEventData) {
                    System.out.println("Table:");
                    TableMapEventData tableMapEventData = (TableMapEventData) data;
                    System.out.println(tableMapEventData.getTableId()+": ["+tableMapEventData.getDatabase() + "-" + tableMapEventData.getTable()+"]");
                }
                if(data instanceof  RotateEventData){
                    System.out.printf(data.toString());

                }
                if (data instanceof UpdateRowsEventData) {
                    System.out.println("Update:");
                    System.out.println(data.toString());
                } else if (data instanceof WriteRowsEventData) {
                    System.out.println("Insert:");
                    System.out.println(data.toString());
                } else if (data instanceof DeleteRowsEventData) {
                    System.out.println("Delete:");
                    System.out.println(data.toString());
                }
            }
        });
        try {
            //Connect to the replication stream. Note that this method blocks until disconnected.
            client.connect();
            // Connect to the replication stream in a separate thread.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
