package org.apache.hadoop.mapreduce.lib.output;

public class FileSize {
    private String path;
    private String name;
    private long size;
    private String prePath;
    public FileSize(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public FileSize(String path, String name, long size, String prePath) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.prePath = prePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPrePath() {
        return prePath;
    }

    public void setPrePath(String prePath) {
        this.prePath = prePath;
    }


    public String processRelaPath(){
        return path.replace(prePath,"");
    }

    @Override
    public String toString() {
        return "FileSize{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", prePath='" + prePath + '\'' +
                '}';
    }
}
