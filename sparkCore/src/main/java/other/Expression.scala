package other

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


////////////////////////////////////////////////////////////////////////////////////////////////////
// This file defines the basic expression abstract classes in Catalyst.
////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * An expression in Catalyst.
 *
 * If an expression wants to be exposed in the function registry (so users can call it with
 * "name(arguments...)", the concrete implementation must be a case class whose constructor
 * arguments are all Expressions types. See [[Substring]] for an example.
 *
 * There are a few important traits:
 *
 * - [[Nondeterministic]]: an expression that is not deterministic.
 * - [[Unevaluable]]: an expression that is not supposed to be evaluated.
 * - [[CodegenFallback]]: an expression that does not have code gen implemented and falls back to
 *                        interpreted mode.
 *
 * - [[LeafExpression]]: an expression that has no child.
 * - [[UnaryExpression]]: an expression that has one child.
 * - [[BinaryExpression]]: an expression that has two children.
 * - [[TernaryExpression]]: an expression that has three children.
 * - [[BinaryOperator]]: a special case of [[BinaryExpression]] that requires two children to have
 *                       the same output data type.
 *
 */
abstract class Expression extends TreeNode[Expression] {

  /**
   * Returns true when an expression is a candidate for static evaluation before the query is
   * executed.
   *
   * The following conditions are used to determine suitability for constant folding:
   *  - A [[Coalesce]] is foldable if all of its children are foldable
   *  - A [[BinaryExpression]] is foldable if its both left and right child are foldable
   *  - A [[Not]], [[IsNull]], or [[IsNotNull]] is foldable if its child is foldable
   *  - A [[Literal]] is foldable
   *  - A [[Cast]] or [[UnaryMinus]] is foldable if its child is foldable
   */
  def foldable: Boolean = false

  /**
   * Returns true when the current expression always return the same result for fixed inputs from
   * children.
   *
   * Note that this means that an expression should be considered as non-deterministic if:
   * - it relies on some mutable internal state, or
   * - it relies on some implicit input that is not part of the children expression list.
   * - it has non-deterministic child or children.
   *
   * An example would be `SparkPartitionID` that relies on the partition id returned by TaskContext.
   * By default leaf expressions are deterministic as Nil.forall(_.deterministic) returns true.
   */
  def deterministic: Boolean = children.forall(_.deterministic)

  def nullable: Boolean


}
