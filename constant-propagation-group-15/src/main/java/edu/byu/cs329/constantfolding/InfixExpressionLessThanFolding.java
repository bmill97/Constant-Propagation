package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replaces less than infix expressions with the boolean literals.
 * 
 * @author Ben Millett
 * @author Caleb Sly
 */
public class InfixExpressionLessThanFolding implements Folding {
  static final Logger log = LoggerFactory.getLogger(InfixExpressionLessThanFolding.class);
  
  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    /**
     * Folds infix expressions with < operator.
     * 
     * @param node InfixExpression node to potentially fold
     */
    @Override
    public void endVisit(InfixExpression node) {
      Operator operator = node.getOperator();
      if (operator != InfixExpression.Operator.LESS) {
        return;
      }

      Expression lhs = node.getLeftOperand();
      if (!(lhs instanceof NumberLiteral)) {
        return;
      }

      Expression rhs = node.getRightOperand();
      if (!(rhs instanceof NumberLiteral)) {
        return;
      }

      int left = Integer.decode(((NumberLiteral)lhs).getToken());
      int right = Integer.decode(((NumberLiteral)rhs).getToken());
      boolean literal = (left < right);


      AST ast = node.getAST();
      ASTNode newExp = ast.newBooleanLiteral(literal);
      TreeModificationUtils.replaceChildInParent(node, newExp);
      didFold = true;
    }
    
  }

  public InfixExpressionLessThanFolding() {
  } 
  
  /**
   * Replaces less than infix expressions with number literals in the tree
   * with their sum.
   * 
   * <p>Visits the root and any reachable nodes from the root to replace
   * any InfixExpression reachable node containing number literals
   * with the boolean outcome.
   *
   * <p>top := all nodes reachable from root such that each node 
   *           is an infix expression with all operands as number literals
   *           and operator is '<'
   * 
   * <p>parents := all nodes such that each one is the parent
   *               of some node in top
   * 
   * <p>isFoldable(n) := isInfixExpression(n)
   *                     /\ (isNumberLiteral(leftHandOperand(n))
   *                     /\ (isNumberLiteral(rightHandOperand(n))
   *                     /\ (isLessThanOperator(operator(n))
   * 
   * @modifies nodes in parents
   * 
   * @requires root != null
   * @requires (root instanceof CompilationUnit) \/ parent(root) != null
   * @requires for all n in old(top), isInt(leftHandOperand(n)) /\ isInt(rightHandOperand(n))
   * 
   * @ensures fold(root) == (old(top) != emptyset)
   * @ensures forall n in old(top), exists n' in nodes 
   *             fresh(n')
   *          /\ isBooleanLiteral(n')
   *          /\ value(n') == (toInt(leftHandOperand(n)) < toInt(rightHandOperand(n))
   *          /\ parent(n') == parent(n)
   *          /\ children(parent(n')) == (children(parent(n)) setminus {n}) union {n'}
   *   
   * @param root the root of the tree to traverse.
   * @return true if infix expressions were replaced in the rooted tree
   */
  public boolean fold(final ASTNode root) {
    checkRequires(root);
    Visitor visitor = new Visitor();
    root.accept(visitor);
    return visitor.didFold;
  }

  private void checkRequires(final ASTNode root) {
    ExceptionUtils.requiresNonNull(root, "Null root passed to InfixExpressionLessThanFolding.fold");
 
    if (!(root instanceof CompilationUnit) && root.getParent() == null) {
      ExceptionUtils.throwRuntimeException(
          "Non-CompilationUnit root with no parent passed to InfixExpressionLessThanFolding.fold"
      );
    }
  }
    
}
