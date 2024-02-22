package org.apache.flink.api.connector.source.mytest;

import org.apache.flink.api.connector.source.SourceSplit;

public class MySplit  implements SourceSplit {
    @Override
    public String splitId() {
        return null;
    }
}
