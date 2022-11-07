package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import java.util.ArrayList;
import java.util.List;
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
 * Replaces addition infix expressions with their sum.
 * 
 * @author Ben Millett
 * @author Caleb Sly
 */
public class InfixExpressionPlusFolding implements Folding {
  static final Logger log = LoggerFactory.getLogger(InfixExpressionPlusFolding.class);
  
  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    /**
     * Folds infix expressions with + operator.
     * 
     * @param node InfixExpression node to potentially fold
     */
    @Override
    public void endVisit(InfixExpression node) {
      Operator operator = node.getOperator();
      if (operator != InfixExpression.Operator.PLUS) {
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

      for (Object operand : node.extendedOperands()) {
        Expression expOperand = (Expression) operand;
        if (!(expOperand instanceof NumberLiteral)) {
          return;
        }
      }

      List<Integer> operands = new ArrayList<>();
      for (Object operand : node.extendedOperands()) {
        NumberLiteral expOperand = (NumberLiteral) operand;
        operands.add(Integer.decode(expOperand.getToken()));
      }
      operands.add(Integer.decode(((NumberLiteral)lhs).getToken()));
      operands.add(Integer.decode(((NumberLiteral)rhs).getToken()));
      Integer literal = 0;
      for (int operand : operands) {
        literal += operand;
      }

      AST ast = node.getAST();
      ASTNode newExp = ast.newNumberLiteral(literal.toString());
      TreeModificationUtils.replaceChildInParent(node, newExp);
      didFold = true;
    }
    
  }

  public InfixExpressionPlusFolding() {
  } 
  
  /**
   * Replaces addition infix expressions with number literals in the tree
   * with their sum.
   * 
   * <p>Visits the root and any reachable nodes from the root to replace
   * any InfixExpression reachable node containing number literals
   * with their sum.
   *
   * <p>top := all nodes reachable from root such that each node 
   *           is an infix expression with all operands as number literals
   *           and operator is '+'
   * 
   * <p>parents := all nodes such that each one is the parent
   *               of some node in top
   * 
   * <p>isFoldable(n) := isInfixExpression(n)
   *                     /\ (isNumberLiteral(leftHandOperand(n))
   *                     /\ (isNumberLiteral(rightHandOperand(n))
   *                     /\ (isPlusOperator(operator(n))
   *                     /\ forall o in extendedOperands(n)
   *                            (isNumberLiteral(o))
   * 
   * <p>operands(n) := leftHandOperand(n) union rightHandOperand(n) union
   *                   extendedOperands(n)
   * 
   * <p>sum(list) := for all i in list
   *                    total += toInt(i)
   * 
   * @modifies nodes in parents
   * 
   * @requires root != null
   * @requires (root instanceof CompilationUnit) \/ parent(root) != null
   * @requires for all n in old(top), for all o in operands(n), isInt(o)
   * 
   * @ensures fold(root) == (old(top) != emptyset)
   * @ensures forall n in old(top), exists n' in nodes 
   *             fresh(n')
   *          /\ isNumberLiteral(n')
   *          /\ value(n') == sum(operands(n))
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
    ExceptionUtils.requiresNonNull(root, "Null root passed to InfixExpressionPlusFolding.fold");
 
    if (!(root instanceof CompilationUnit) && root.getParent() == null) {
      ExceptionUtils.throwRuntimeException(
          "Non-CompilationUnit root with no parent passed to InfixExpressionPlusFolding.fold"
      );
    }
  }
    
}
