//package spark.sql
//
//import org.apache.spark.sql.Encoders
//import org.apache.spark.sql.Encoder
//import org.apache.spark.sql.catalyst.encoders.{ExpressionEncoder, RowEncoder}
//import org.apache.spark.sql.catalyst.expressions.Attribute
//import org.scalatest.funsuite.AnyFunSuite
///**
// * 接口
// *[[Encoder]] 该接口没有具体的内容
// *帮助类
// * [[Encoders]]
// * 该类是 Encoder的唯一实现
// * [[org.apache.spark.sql.catalyst.encoders.ExpressionEncoder]]
// * 目标是 在对象 T和 InternalRow UnsafeRow之间转换
// *
// * InternalRow和 [[Row]]之间的转换
// * [[RowEncoder]]
// * 从InternalRow 生成Row的具体代码实现
// * [[org.apache.spark.sql.catalyst.expressions.objects.CreateExternalRow]]
// *
// */
//class LearnEncoder extends AnyFunSuite {
//  def main(args: Array[String]): Unit = {
//
//  }
//
///*
//如何使用encoder
//https://jaceklaskowski.gitbooks.io/mastering-spark-sql/content/spark-sql-Encoder.html
//
//InternalRow which is the internal binary row format
//representation (using Catalyst expressions and code generation).
//*/
//  test("see how to use encoder alone"){
//
//    import org.apache.spark.sql.Encoders
//
//    val personEncoder = Encoders.product[Person]
//    println(personEncoder.schema)
//
//    val personExprEncoder = personEncoder.asInstanceOf[ExpressionEncoder[Person]]
//
//    val jacek = Person(0, "Jacek")
//    val row = personExprEncoder.createSerializer()(jacek)
//    println(row)
//    //反序列化row
//    //Note that you must `resolveAndBind` an encoder to a specific schema before you can create a
//    // error
//    val person1 = personExprEncoder.createDeserializer()(row)
////    person
////    print(person)
//    import org.apache.spark.sql.catalyst.dsl.expressions._
//    val attrs = Seq(DslSymbol('id).long, DslSymbol('name).string)
//    val des = personExprEncoder.resolveAndBind().createDeserializer()
//    val person = des(row)
//    println(jacek==person)
//
//    val encoder = RowEncoder(attrs.toStructType).resolveAndBind()
//    encoder.createDeserializer()
//
////    RowEncoder
//  }
//}
