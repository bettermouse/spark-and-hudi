# RFC-42: Consistent Hashing Index for Dynamic Bucket Number
## Abstract
hudi去重通过索引查找,bucket index 比Bloom  index 3x性能提升,但是它需要固定
桶的个数,而且后来不能改变,
https://cwiki.apache.org/confluence/display/HUDI/RFC+-+29%3A+Hash+Index
通过重写整张表来实现.

Consistent Hashing Index
## Background
primary key concept
data skew,一个方案是一个bucket 对应多个file group 
另一个方案是 Consistent Hashing,
Hash value is obtained by computing Hash(v) % 0xFFFF, which falls into a pre-defined range (i.e., [0, 0xFFFF] in the figure).
和传统的hash策略相比.增加了 mapping layer
## Implementation
Bucket Resizing (Splitting & Merging)



Concurrent Writer & Reader
Dual write solution

#  Bucket Index for HUDI

https://issues.apache.org/jira/browse/HUDI-1951.
Add bucket hash index, compatible with the hive bucket

bucket id的生成,前8位是桶的id.
```
  @Override
  public BucketInfo getBucketInfo(int bucketNumber) {
    String partitionPath = partitionPaths.get(bucketNumber / numBuckets);
    String bucketId = BucketIdentifier.bucketIdStr(bucketNumber % numBuckets);
    Option<String> fileIdOption = Option.fromJavaOptional(updatePartitionPathFileIds
        .getOrDefault(partitionPath, Collections.emptySet()).stream()
        .filter(e -> e.startsWith(bucketId))
        .findFirst());
    if (fileIdOption.isPresent()) {
      return new BucketInfo(BucketType.UPDATE, fileIdOption.get(), partitionPath);
    } else {
      return new BucketInfo(BucketType.INSERT, BucketIdentifier.newBucketFileIdPrefix(bucketId), partitionPath);
    }
  }

```