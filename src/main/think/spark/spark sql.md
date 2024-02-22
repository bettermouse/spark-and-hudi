# spark sql
## other.TreeNode 
abstract class other.TreeNode[BaseType <: other.TreeNode[BaseType]] extends Product with TreePatternBits {
treeNode是一个自限定类型,它的泛型只能是它自身
### other.Expression
### other.QueryPlan
#### SparkPlan

```
  /**
   * Produces the result of the query as an `RDD[InternalRow]`
   *
   * Overridden by concrete implementations of SparkPlan.
   */
  protected def doExecute(): RDD[InternalRow]
```
#### other.LogicalPlan



## 全阶段代码生成
spark.sql.codegen.wholeStage 默认为true

##  物理执行计划
### 生成
org.apache.spark.sql.execution.SparkStrategy
### sparkPlain

## 物理和logical plan的对应关系

spark.internalCreateDataFrame(rdd, schema)
LogicalRDD -> RDDScanExec
为什么全代码生成开启后,RDDScanExec中的doExecute()没有被调用


## unsafeRow
UnsafeRow extends InternalRow
UnsafeRow 是spark中一个很重要的概念.它表示数据的方式是
   [null bit set] [values] [variable length portion]

## UnsafeWriter
  public static int calculateBitSetWidthInBytes(int numFields) {
    return ((numFields + 63)/ 64) * 8;
  }
  1个字段 8个字节
## UnsafeRowSerializer
Serializer for serializing UnsafeRows during shuffle.
Since UnsafeRows are already stored as bytes,
this serializer simply copies those bytes to the underlying output stream. 
When deserializing a stream of rows,
 instances of this serializer mutate and return a single UnsafeRow instance 
 that is backed by an on-heap byte array.
Note that this serializer implements only the Serializer methods that are used during shuffle, 
so certain SerializerInstance methods will throw UnsupportedOperationException.


row.writeToStream(dOut, writeBuffer)


### UnsafeRowWriter 
https://zhuanlan.zhihu.com/p/298203303

Clears out null bits.  This should be called before we write a new row to row buffer.

reset() //会将下标置空
zeroOutNullBytes()
//写数据
setNullAt(5);
write(5, value_5);
//获得row
getRow()



写固定长度的数据不会调用 increaseCursor,有fixSize控制,变长的时候才会调用increaseCursor

返回的baseOffset会变化吗?

#### BufferHolder
如果不弄清楚这个类,就无法理解 UnsafeRowWriter
private byte[] buffer;  buffer用来存数据,但是buffer会经常改变,如果写进去了,就不会想着修改了.
private final UnsafeRow row; 
其中的UnsafeRow难道仅仅是为了保存最初的偏移量
看下getRow方法


### UnsafeProjection
#### RDDScanExec UnsafeProjection.create(schema)
```
  protected override def doExecute(): RDD[InternalRow] = {
    val numOutputRows = longMetric("numOutputRows")
    rdd.mapPartitionsWithIndexInternal { (index, iter) =>
      val proj = UnsafeProjection.create(schema)
      proj.initialize(index)
      iter.map { r =>
        numOutputRows += 1
        proj(r)
      }
    }
  }
```

# LogicalPlan
##Analyzed logicalPlan
UnresolvedRelation
UnresolvedAttribute

### catalog
### rule
### 生成  Analyzed logicalPlan
org.apache.spark.sql.catalyst.analysis.Analyzer.batches 
batch substitution
cte
windowSubstitution
EliminateUnions
SubstituteUnresolvedOrdinals

BatchResolution

Analyzer 实现 RuleExecutor ,调用 execute 方法

## optimizer
### 生成 
EliminateSubqueryAliases
