package antlr.listener


import antlr.generate.{SqlBaseBaseListener, SqlBaseLexer, SqlBaseParser, UpperCaseCharStreamJava}
import antlr.table.{Field, Table}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.antlr.v4.runtime.tree.ParseTreeWalker

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable.ArrayBuffer

class ResolveCreateTable extends SqlBaseBaseListener {
  val fields = ArrayBuffer[Field]()
  var table: Table = null


  override def enterColTypeList(ctx: SqlBaseParser.ColTypeListContext): Unit = {
    for (one <- ctx.colType.asScala) {
      fields += Field(one.dataType.getText()
        , one.colName.identifier.getText()
        , if(one.commentSpec()!=null){one.commentSpec().STRING().getText()}else{""})
    }

    super.enterColTypeList(ctx)
  }


  /**
   * {@inheritDoc }
   *
   * <p>The default implementation does nothing.</p>
   */
  override def enterPartitionColumn(ctx: SqlBaseParser.PartitionColumnContext): Unit = {
    fields += Field(ctx.colType().dataType.getText()
      , ctx.colType().colName.identifier.getText()
      , if (ctx.colType().commentSpec() != null) {
        ctx.colType().commentSpec().STRING().getText()
      } else {
        ""
      },partition=true)
  }

  override def enterCreateTableHeader(ctx: SqlBaseParser.CreateTableHeaderContext): Unit = {
    val text = ctx.multipartIdentifier.getText
    val strings = text.split("\\.")
    if (strings.length > 1) {
      table = Table(strings(0), strings(1), "", null);
    } else {
      table = Table("default", strings(0), "", null);
    }
    super.enterCreateTableHeader(ctx)
  }

  override def enterCreateTableClauses(ctx: SqlBaseParser.CreateTableClausesContext): Unit = {
    try {
      val text = ctx.commentSpec().get(0).STRING().getText
      table.comment = text;
    } catch {
      case _ =>
    }
  }

  def getTable(): Table = {
    table.fields = fields;
    table
  }

}
object ResolveCreateTable{
  def resolveTable(s: String):Table= {
    val lexer: SqlBaseLexer = new SqlBaseLexer (new UpperCaseCharStreamJava (CharStreams.fromString (s) ) )
    val tokens: CommonTokenStream = new CommonTokenStream (lexer)

    // create a parser that feeds off the tokens buffer
    val parser: SqlBaseParser = new SqlBaseParser (tokens)

    val parseTreeWalker: ParseTreeWalker = new ParseTreeWalker
    //ResolveCreateTable
    val listener: ResolveCreateTable = new ResolveCreateTable
    parseTreeWalker.walk (listener, parser.singleStatement)
    val table: Table = listener.getTable
    table
  }
}
