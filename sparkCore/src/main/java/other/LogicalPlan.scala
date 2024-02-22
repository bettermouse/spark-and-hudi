package other

abstract class LogicalPlan extends QueryPlan[LogicalPlan] {

  /**
   * Returns the maximum number of rows that this plan may compute.
   *
   * Any operator that a Limit can be pushed passed should override this function (e.g., Union).
   * Any operator that can push through a Limit should override this function (e.g., other.Project).
   */
  def maxRows: Option[Long] = None

}