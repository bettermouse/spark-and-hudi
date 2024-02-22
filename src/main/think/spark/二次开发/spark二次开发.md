# 1 表结构替换
数仓经过重构后,会有 表名和列名替换的情况
a x,y,z
b x,m,n

现在 b表换成 c表
c x,m1,n1

select
y,m,n
from
(
select a.y,b.x,m,n from a left join b on a.x=b.x
) t;

目标

select a.y,c.x,m1 as m, n1 as n from a left join c on a.x=c.x;

```
  test("3"){
    val spark = SparkSession.builder()
      .master("local[*]").getOrCreate()
    import spark.implicits._
    Seq((1, 2, 3), (4, 5, 6)).toDF("x", "y", "z").createOrReplaceTempView("a");
    Seq((1, 2, 5), (4, 5, 6)).toDF("x", "m", "n").createOrReplaceTempView("b");
    Seq((1, 2, 5), (4, 5, 6)).toDF("x", "m1", "m2").createOrReplaceTempView("c");

    spark.sql("select y,m,n from(select a.y,b.x,m,n from a left join b on a.x=b.x) t;").explain("extended")
  }

```

```
/**
 * A globally unique id for a given named expression.
 * Used to identify which attribute output by a relation is being
 * referenced in a subsequent computation.
 *
 * The `id` field is unique within a given JVM, while the `uuid` is used to uniquely identify JVMs.
 */
case class ExprId(id: Long, jvmId: UUID) {

  override def equals(other: Any): Boolean = other match {
    case ExprId(id, jvmId) => this.id == id && this.jvmId == jvmId
    case _ => false
  }

  override def hashCode(): Int = id.hashCode()

}

case class LocalRelation(
    output: Seq[Attribute],
    data: Seq[InternalRow] = Nil,
    // Indicates whether this relation has data from a streaming source.
    override val isStreaming: Boolean = false)
  extends LeafNode with analysis.MultiInstanceRelation {

在这里发现通过  string 查找 对应的表,在这里打断点
  def lookupTempView(table: String): Option[SubqueryAlias] = {
    val formattedTable = formatTableName(table)
    getTempView(formattedTable).map { view =>
      SubqueryAlias(formattedTable, view)
    }
  }

Resolve relations to temp views. This is not an actual rule, and is called by Analyzer.ResolveTables and Analyzer.ResolveRelations.
org.apache.spark.sql.catalyst.analysis.Analyzer.ResolveRelations#lookupRelation


/**
 * Encapsulates an identifier that is either a alias name or an identifier that has table
 * name and a qualifier.
 * The SubqueryAlias node keeps track of the qualifier using the information in this structure
 * @param name - Is an alias name or a table name
 * @param qualifier - Is a qualifier
 */
case class AliasIdentifier(name: String, qualifier: Seq[String]) {
  import org.apache.spark.sql.connector.catalog.CatalogV2Implicits._

  def this(identifier: String) = this(identifier, Seq())

  override def toString: String = (qualifier :+ name).quoted
}

```

# spark 小文件自动合并
https://aws.amazon.com/cn/blogs/china/application-and-practice-of-spark-small-file-merging-function-on-aws-s3/

if (conf.getOption("spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version").isEmpty) {
  hadoopConf.set("mapreduce.fileoutputcommitter.algorithm.version", "1")
}

org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
FileOutputCommitter
```
 if (!fs.rename(taskAttemptPath, committedTaskPath)) {
   throw new IOException("Could not rename " + taskAttemptPath + " to "
       + committedTaskPath);
 }
 LOG.info("Saved output of task '" + attemptId + "' to " +
     committedTaskPath);
```

```
  test("4"){
    val spark = SparkSession.builder()
      .master("local[*]").getOrCreate()
    import spark.implicits._
    Seq((1, 2, 3), (4, 5, 6)).toDF("x", "y", "z")
      .write.mode(SaveMode.Overwrite).partitionBy("x").parquet("Z:\\tmp\\spark")
  }

}
```
job  jobAttempt
task taskAttempt
Stage stageAttempt
## 5.9 输出提交协调器
OutputCommitCoordinator
  private[scheduler] def taskCompleted(
      stage: Int,
      stageAttempt: Int,
      partition: Int,
      attemptNumber: Int,
      reason: TaskEndReason): Unit = synchronized {
    val stageState = stageStates.getOrElse(stage, {

判断stage 的attemp 是否可以成功
AskPermissionToCommitOutput 这个是在作业提交之前查询的.

```
  override def commit(): WriteTaskResult = {
    releaseResources()
    val summary = ExecutedWriteSummary(
      updatedPartitions = updatedPartitions.toSet,
      stats = statsTrackers.map(_.getFinalStats()))
    WriteTaskResult(committer.commitTask(taskAttemptContext), summary)
  }

object SparkHadoopMapRedUtil extends Logging {
  /**
   * Commits a task output.  Before committing the task output, we need to know whether some other
   * task attempt might be racing to commit the same output partition. Therefore, coordinate with
   * the driver in order to determine whether this attempt can commit (please see SPARK-4879 for
   * details).
   *
   * Output commit coordinator is only used when `spark.hadoop.outputCommitCoordination.enabled`
   * is set to true (which is the default).
   */
```
## DynamicPartitionDataWriter

## FileFormatWriter
```
hadoop 中的job 对应 于spark中的stage
jobIdInstant=1683668753238
jobId=job_20230510054553131870729672390673_0000
    val jobId = SparkHadoopWriterUtils.createJobID(new Date(jobIdInstant), sparkStageId)
// 分区 对应 于task
    val taskId = new TaskID(jobId, TaskType.MAP, sparkPartitionId)
taskId=task_20230510054553131870729672390673_0000_m_000001
    val taskAttemptId = new TaskAttemptID(taskId, sparkAttemptNumber)
attempt_20230510054553131870729672390673_0000_m_000001_1
```
## 对于V1 的小文件处理.
```
if (algorithmVersion == 1) {
```