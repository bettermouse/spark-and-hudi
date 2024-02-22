import org.apache.hadoop.yarn.webapp.example.MyApp;

public class TestPrint {
    public static void main(String[] args) {
      String   MYSQL_GITHUB_SCHEMA = "{\"connect.name\": \"mysql.ghschema.gharchive.Envelope\",\n"
                + "  \"fields\": [{\"default\": null,\"name\": \"before\",\"type\": [\"null\",{\"connect.name\": \"mysql.ghschema.gharchive.Value\",\n"
                + "  \"fields\": [{\"name\": \"id\",\"type\": \"string\"},{\"name\": \"date\",\"type\": \"string\"},{\"default\": null,\"name\": \"timestamp\",\n"
                + "  \"type\": [\"null\",\"long\"]},{\"default\": null,\"name\": \"type\",\"type\": [\"null\",\"string\"]},{\"default\": null,\"name\": \"payload\",\n"
                + "  \"type\": [\"null\",\"string\"]},{\"default\": null,\"name\": \"org\",\"type\": [\"null\",\"string\"]},{\"default\": null,\"name\": \"created_at\",\n"
                + "  \"type\": [\"null\",\"long\"]},{\"default\": null,\"name\": \"public\",\"type\": [\"null\",\"boolean\"]}],\"name\": \"Value\",\"type\": \"record\"\n"
                + "  }]},{\"default\": null,\"name\": \"after\",\"type\": [\"null\",\"Value\"]},{\"name\": \"source\",\"type\": {\"connect.name\": \"io.debezium.connector.mysql.Source\",\n"
                + "  \"fields\": [{\"name\": \"connector\",\"type\": \"string\"},{\"name\": \"name\",\"type\": \"string\"},{\"name\": \"ts_ms\",\"type\": \"long\"},\n"
                + "  {\"name\": \"db\",\"type\": \"string\"},{\"name\": \"table\",\"type\": \"string\"},{\"default\": null,\n"
                + "  \"name\": \"txId\",\"type\": [\"null\",\"long\"]},{\"name\": \"file\",\"type\": \"string\"},{\"default\": null,\"name\": \"pos\",\"type\": [\"null\",\"long\"]},{\"default\": null,\n"
                + "  \"name\": \"row\",\"type\": [\"null\",\"long\"]}],\"name\": \"Source\",\"namespace\": \"io.debezium.connector.mysql\",\"type\": \"record\"\n"
                + "  }},{\"name\": \"op\",\"type\": \"string\"},{\"default\": null,\"name\": \"ts_ms\",\"type\": [\"null\",\"long\"]},{\"default\": null,\"name\": \"transaction\",\n"
                + "  \"type\": [\"null\",{\"fields\": [{\"name\": \"id\",\"type\": \"string\"},{\"name\": \"total_order\",\"type\": \"long\"},{\"name\": \"data_collection_order\",\n"
                + "  \"type\": \"long\"}],\"name\": \"ConnectDefault\",\"namespace\": \"io.confluent.connect.avro\",\"type\": \"record\"}]}],\"name\": \"Envelope\",\n"
                + "  \"namespace\": \"mysql.ghschema.gharchive\",\"type\": \"record\"}";
        System.out.println(MYSQL_GITHUB_SCHEMA);


    }
}
