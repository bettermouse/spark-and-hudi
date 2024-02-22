package hbaseTool;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.regionserver.HRegionFileSystem;

import java.io.IOException;

public class Tools {
    public static void main(String[] args) throws IOException {
        // 获取XX  region 相关的信息 default/user_table/1221fd402b2e7c813da47ebcb7b8d81d
        readXXRegionPath();
    }

    public static void readXXRegionPath() throws IOException {
        // 获取XX  region 相关的信息 default/user_table/1221fd402b2e7c813da47ebcb7b8d81d
        FileSystem rootFs = FileSystem.get(new Configuration());
        RegionInfo hri = HRegionFileSystem.loadRegionInfoFileContent(rootFs, new Path("/Users/mouse/bigData/hbase-2.4.18-SNAPSHOT/tmp/hbase/data/default/user_table/1221fd402b2e7c813da47ebcb7b8d81d"));
        System.out.printf(""+hri);

    }
}
