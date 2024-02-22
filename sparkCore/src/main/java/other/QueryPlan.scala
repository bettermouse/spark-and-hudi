package other


abstract class QueryPlan[PlanType <: QueryPlan[PlanType]] extends TreeNode[PlanType] {
  self: PlanType =>

  def output: Seq[Attribute]
}