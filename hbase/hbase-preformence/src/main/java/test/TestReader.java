package test;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class TestReader implements Iterator<String> {


    public static void main(String[] args) throws IOException {

        //"/Users/mouse/download/temp/vin3/part-00000-988d56df-daf5-41af-967b-f1512d062b0b-c000.snappy.parquet"
        ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(new Path(args[0])).build();
        GenericRecord read = reader.read();
        int i =0;
        while (read!=null){
           // System.out.println(""+read.toString());
            i++;
            read= reader.read();
        }
        System.out.printf("result"+i);
    }
    ParquetReader<GenericRecord> reader = null;

    String current = null;
    public TestReader( String file) throws IOException {

        this.reader = reader = AvroParquetReader.<GenericRecord>builder(new Path(file)).build();
        LinkedList<String> strings = new LinkedList<String>();

    }


    @Override
    public boolean hasNext() {

        return false;
    }

    @Override
    public String next() {
        return null;
    }
}
