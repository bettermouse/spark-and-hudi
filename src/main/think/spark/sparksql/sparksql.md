# spark sql
在有了spark core的 基础上如何构建 spark sql

## 看下spark 1.0版本
https://spark.apache.org/docs/1.0.2/sql-programming-guide.html



Row

# spark sql 全过程
sql->UnresolvedLocalPlan->AnalyzedLogicalPlan->OptimizedLogicalPlan
物理计划
IteratorPhysicalPlan->sparkPlan->preparedSparkPlan




# 
## logical plan
object RuleExecutor