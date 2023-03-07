package org.apache.flink.api.connector.source.mytest;

import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.core.io.InputStatus;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MySourceReader implements SourceReader<String, MySplit> {
    @Override
    public void start() {

    }

    @Override
    public InputStatus pollNext(ReaderOutput<String> output) throws Exception {
        return null;
    }

    @Override
    public List<MySplit> snapshotState(long checkpointId) {
        return null;
    }

    @Override
    public CompletableFuture<Void> isAvailable() {
        return null;
    }

    @Override
    public void addSplits(List<MySplit> splits) {

    }

    @Override
    public void notifyNoMoreSplits() {

    }

    @Override
    public void close() throws Exception {

    }
}
