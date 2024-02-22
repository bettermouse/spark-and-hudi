import org.apache.spark.sql.catalyst.expressions.codegen.{CodegenContext, ExprCode, JavaCode}
import org.apache.spark.sql.catalyst.expressions.{Add, Literal, MonotonicallyIncreasingID, Subtract, Unhex}
import org.apache.spark.sql.types.StringType
import org.scalatest.funsuite.AnyFunSuite

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description: 
 * HISTORY
 * *********************************************
 * ID   DATE               PERSON             REASON
 * 1   2023/4/25 18:11     01407273            Create
 *
 * *********************************************
 * </pre>
 */
class TestCodeGenerator extends AnyFunSuite{
  test(""){
    val ctx = new CodegenContext()
    val code1 = Add(Add(Literal(3),Literal(100)),Subtract(Literal(1),Literal(1)) ).genCode(ctx)
    println(code1)
    val isNull = ctx.freshName("isNull")
    val value = ctx.freshName("value")
    val code = ExprCode(
      JavaCode.isNullVariable(isNull),
      JavaCode.variable(value, StringType))
    println(code)
  }

  test("1"){
    val ctx = new CodegenContext()
    val code = MonotonicallyIncreasingID().genCode(ctx)
    print(code)
  }


  test("code generator references"){
    val ctx = new CodegenContext()
    val unhex = Unhex(Literal("120"))
    val code = unhex.genCode(ctx)
    println(code)
    val list = List(1, 3, 5, "seven")
    list
  }
}
