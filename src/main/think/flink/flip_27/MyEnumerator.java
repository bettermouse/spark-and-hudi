package org.apache.flink.api.connector.source.mytest;

import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.api.connector.source.SplitEnumerator;

import java.io.IOException;
import java.util.List;

public class MyEnumerator implements SplitEnumerator<MySplit, SourceSplit> {
    @Override
    public void start() {

    }

    @Override
    public void handleSplitRequest(
            int subtaskId,
            String requesterHostname) {

    }

    @Override
    public void addSplitsBack(List<MySplit> splits, int subtaskId) {

    }

    @Override
    public void addReader(int subtaskId) {

    }

    @Override
    public SourceSplit snapshotState(long checkpointId) throws Exception {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
