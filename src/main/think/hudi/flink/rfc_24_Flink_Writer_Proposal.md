## Current Pipeline

## The Improvement Proposal
###  STEP1: Remove the Single Parallelism Operator
coordinator
checkpoint时,创建Instant.

checkpoint完成时(notifyCheckpointComplete),提交,会有问题,比如提交的时候失败

operator
checkpoint时提交数据.

仅根据 分区字段分区,会有写入瓶颈,因为分区总是时间
###  STEP2: Make the Write Task Scalable
For each partition, the write client of WriteProcessOperator handles all the logic for indexing/bucketing/data-write:

indexing the records for INSERT/UPDATE(索引数据 insert/update)
use the PARTITIONER for bucketing in order to decide each record’s bucket(fileId)
write the buckets one by one

split the StreamWriteOperator into 2 operators: the BucketAssigner & BucketWriter
#### The BucketAssigner


### STEP3: Write as Mini-batch
We start a new instant when Coordinator starts(instead of start on new checkpoint from #step1 and #step2)
The BucketWriter blocks and flush the pending buffer data when a new checkpoint starts
In Coordinator, if data within one checkpoint write success (got a checkpoint success notification), 
    check and commit the inflight Instant and start a new instant
    
That means, if a checkpoint succeeds but we do not receive the success event, the inflight instant will span two/(or more) checkpoints.
如果没数据,会横跨几个checkpoint.

###  STEP4: A New Index for Flink

