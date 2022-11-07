package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replaces logical not prefix expressions with the boolean literals.
 * 
 * @author Ben Millett
 * @author Caleb Sly
 */
public class LogicalNotPrefixExpressionFolding implements Folding {
  static final Logger log = LoggerFactory.getLogger(LogicalNotPrefixExpressionFolding.class);

  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    /**
     * Folds prefix expressions with ! operator.
     * 
     * @param node PrefixExpression node to potentially fold
     */
    @Override
    public void endVisit(PrefixExpression node) {
      Operator operator = node.getOperator();
      if (operator != PrefixExpression.Operator.NOT) {
        return;
      }

      Expression exp = node.getOperand();
      if (!(exp instanceof BooleanLiteral)) {
        return;
      }
      boolean literal = !((BooleanLiteral) exp).booleanValue();

      AST ast = node.getAST();
      ASTNode newExp = ast.newBooleanLiteral(literal);
      TreeModificationUtils.replaceChildInParent(node, newExp);
      didFold = true;
    }

  }

  /**
   * Replaces logical not Prefix Expressions in the tree with their equivalent
   * boolean literal.
   * 
   * <p>Visits the root and any reachable nodes from the root to replace any
   * PrefixExpression reachable node containing a boolean literal with a logical
   * not before it with the opposite boolean literal
   *
   * <p>top := all nodes reachable from root such that each node is a prefix
   * expression with a boolean not applied to a boolean literal
   * 
   * <p>parents := all nodes such that each one is the parent of some node in top
   * 
   * <p>isFoldable(n) := isPrefixExpression(n) 
   *                     /\ ( isBooleanLiteral(expression(n))
   *                     /\ ( isLogicalNot(operator(n))
   * 
   * 
   * @modifies nodes in parents
   * 
   * @requires root != null
   * @requires (root instanceof CompilationUnit) \/ parent(root) != null
   * 
   * @ensures fold(root) == (old(top) != emptyset)
   * @ensures forall n in old(top), exists n' in nodes 
   *            fresh(n')
   *            /\ isBooleanLiteral(n')
   *            /\ value(n') == !value(n)
   *            /\ parent(n') == parent(n) 
   *            /\ children(parent(n')) == (children(parent(n)) setminus {n}) union {n'}
   * 
   * @param root the root of the tree to traverse.
   * @return true if prefix expressions were replaced in the rooted tree
   */
  @Override
  public boolean fold(ASTNode root) {
    checkRequires(root);
    Visitor visitor = new Visitor();
    root.accept(visitor);
    return visitor.didFold;
  }

  private void checkRequires(final ASTNode root) {
    ExceptionUtils.requiresNonNull(root, 
        "Null root passed to LogicalNotPrefixExpressionFolding.fold");

    if (!(root instanceof CompilationUnit) && root.getParent() == null) {
      ExceptionUtils.throwRuntimeException(
          "Non-CompilationUnit root with no parent given LogicalNotPrefixExpressionFolding.fold");
    }
  }

}
