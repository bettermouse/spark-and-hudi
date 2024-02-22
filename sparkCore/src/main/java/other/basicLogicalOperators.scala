package other


case class Filter(condition: Expression, child: LogicalPlan)
  extends LogicalPlan {
  override def output: Seq[Attribute] = child.output

  override def maxRows: Option[Long] = child.maxRows

  /**
   * Returns a Seq of the children of this node.
   * Children should not change. Immutability required for containsChild optimization
   */
  override def children: Seq[LogicalPlan] = child::Nil
}

case class Project(projectList: Seq[NamedExpression], child: LogicalPlan) extends LogicalPlan {
  override def output: Seq[Attribute] = projectList.map(_.toAttribute)
  override def maxRows: Option[Long] = child.maxRows
  }
