# flip 134
https://cwiki.apache.org/confluence/display/FLINK/FLIP-134%3A+Batch+execution+for+the+DataStream+API
## motivation
unbounded if it will continuously produce data and will never shut down.
flink 可以处理批和流
现在也可以把无界当成有界，但是flink不会优化
## Proposed Changes
为 DataStream API,增加新的execution mode ,