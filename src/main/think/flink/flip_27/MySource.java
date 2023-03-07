package org.apache.flink.api.connector.source.mytest;

import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.core.io.SimpleVersionedSerializer;

public class MySource implements Source<String, MySplit,SourceSplit> {
    @Override
    public Boundedness getBoundedness() {
        return null;
    }

    @Override
    public SourceReader<String, MySplit> createReader(SourceReaderContext readerContext) throws Exception {
        return null;
    }

    @Override
    public SplitEnumerator<MySplit, SourceSplit> createEnumerator(SplitEnumeratorContext<MySplit> enumContext) throws Exception {
        return null;
    }

    @Override
    public SplitEnumerator<MySplit, SourceSplit> restoreEnumerator(
            SplitEnumeratorContext<MySplit> enumContext,
            SourceSplit checkpoint) throws Exception {
        return null;
    }

    @Override
    public SimpleVersionedSerializer<MySplit> getSplitSerializer() {
        return null;
    }

    @Override
    public SimpleVersionedSerializer<SourceSplit> getEnumeratorCheckpointSerializer() {
        return null;
    }


}
