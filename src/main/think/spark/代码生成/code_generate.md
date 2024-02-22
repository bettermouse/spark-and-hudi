# [SPARK-2054][SQL] Code Generation for other.Expression Evaluation
https://github.com/apache/spark/pull/993

增加一个新的方式用代码来计算表达式,用scala反射生成,
通过spark.sql.codegen来控制.

计算可以用多个方式进行
Projection 
1.生成一个新的row
2.MutableProjection
Ordering
Condition
生成,解释版本

Interpreted 和 生成的版本

## abstract class CodeGenerator


## CodeGeneratorWithInterpretedFallback
FailedCodegenProjection$ in CodeGeneratorWithInterpretedFallbackSuite
MutableProjection$
Predicate$
RowOrdering$
SafeProjection$
UnsafeProjection$

## other.Project 和 Add 有什么区别呢
org.apache.spark.sql.catalyst.expressions.codegen.ExprCode
project 


## localPlan
other.Filter
other.Project
Sort
Join

## CodeGenerator
生成一个类
每个expression都有eval方法.

##  CodeGenContext

```
  val mutableStates: mutable.ArrayBuffer[(String, String, String)] =
    mutable.ArrayBuffer.empty[(String, String, String)]

references

  /**
   * Holding a list of objects that could be used passed into generated class.
   */
  val references: mutable.ArrayBuffer[Any] = new mutable.ArrayBuffer[Any]()
可以给生成类中传递一些对象
```


### ExprCode
代码块,结果是否为null,结果名

## 代码生成
对于每一个expression 
如果是一个完整的类,包含

```
public class myClass{
private int x1;
private int x2;

    public int process(Row row ){
        
    }
}
```

## 每一个类都有eval方法
也是可行的
如果我有一个复杂的 other.Attribute Add Equal
肯定不希望生成一个类去调用.

如果生成三个类怎么去调用 ?
Equal
Add 
other.Attribute

如果想将三个类合成一个类,需要引入
https://issues.apache.org/jira/browse/SPARK-8117
Push codegen into other.Expression
以前 是在codegen里面的.看一下
https://github.com/apache/spark/commit/5e7b6b67bed9cd0d8c7d4e78df666b807e8f7ef2
