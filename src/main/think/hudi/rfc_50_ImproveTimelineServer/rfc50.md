# RFC-50: Improve Timeline Server
## Abstract
## Background
Create a MetaClient and get the complete timeline through MetaClient #getActiveTimeline, which will directly scan the XX directory of metadata

Get the timeline through FileSystemView#getTimeline. This timeline is the cache timeline obtained by requesting the Embedded timeline service. 
There is no need to repeatedly scan the XX directory of metadata, but this timeline only contains completed instants
(仅包含完成的timeline)


### Problem description

### Spark and Flink write flow comparison diagram
spark 在driver端操作

flink是一种纯流,常驻服务,在flink中没有可靠的交流机制在TM,JM
so the TM needs to obtain the latest instant by polling the timeline for writing.


HoodieTable
getFileSystemView
getMetaClient