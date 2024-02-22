# rfc 13 Integrate Hudi with Flink
https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=141724520
## Abstract
集成flink
## Motivation
## Design
## Batch abstraction on Flink streams: windows and checkpoints
hudi需要元子的写入一批数据,在使用flink的时候,这个也需要保证.由于小文件/性能,需要采用批的方式写入.
在flink中,可以用窗口作为批来切流数据,在某种程度上来说checkpoint也是的.



# 其它
## prepareSnapshotPreBarrier
在checkpoint之前也可以做一些有意思的事情.

https://blog.jrwang.me/2019/flink-source-code-checkpoint/