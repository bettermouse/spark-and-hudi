package generate.logicalPlan

import generate.antlr.{SqlBaseBaseVisitor, SqlBaseParser}
import other.LogicalPlan

class AstBuilder extends SqlBaseBaseVisitor[LogicalPlan]{
  /**
   * {@inheritDoc }
   *
   * <p>The default implementation returns the result of calling
   * {@link #visitChildren} on {@code ctx}.</p>
   */
  override def visitTableName(ctx: SqlBaseParser.TableNameContext): LogicalPlan = {
    val table = UnresolvedRelation(
      visitTableIdentifier(ctx.tableIdentifier),
      Option(ctx.strictIdentifier).map(_.getText))
    table.optionalMap(ctx.sample)(withSample)
  }
}
