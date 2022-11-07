package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replaces If Statements with boolean predicate with the expression.
 * 
 * @author Ben Millett
 * @author Caleb Sly
 */
public class IfStatementFolding implements Folding {
  static final Logger log = LoggerFactory.getLogger(IfStatementFolding.class);

  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    /**
     * Folds if statements with boolean predicates.
     * 
     * @param node IfStatement node to potentially fold
     */
    @Override
    public void endVisit(IfStatement node) {
      Expression exp = node.getExpression();
      if (!(exp instanceof BooleanLiteral)) {
        return;
      }
      boolean predicate = ((BooleanLiteral) exp).booleanValue();
      AST ast = node.getAST();
      ASTNode newExp;
      if (predicate) {
        newExp = ASTNode.copySubtree(ast, node.getThenStatement());
      } else {
        Statement elseStatement = node.getElseStatement();
        if (elseStatement == null) {
          TreeModificationUtils.removeChildInParent(node);
          didFold = true;
          return;
        }
        newExp = ASTNode.copySubtree(ast, node.getElseStatement());
      }
      TreeModificationUtils.replaceChildInParent(node, newExp);
      didFold = true;

    }

  }

  public IfStatementFolding() {
  }
  
  /**
   * Replaces if statements with boolean predicate in the tree with their expression.
   * 
   * <p>Visits the root and any reachable nodes from the root to replace
   * any IfStatements reachable node with boolean predicate
   * with their executed expression.
   *
   * <p>top := all nodes reachable from root such that each node 
   *           is an if statement that has a boolean predicate
   * 
   * <p>parents := all nodes such that each one is the parent
   *               of some node in top
   * 
   * <p>isFoldable(n) := isIfStatement(n)
   *                     /\ (isBooleanLiteral(predicate(n))
   * 
   * <p>predicate(n) := the conditional element of the if statement
   * 
   * <p>executedStatement(n) := if predicate(n) == true
   *                              executedStatement(n) = thenStatement(n)
   *                            else
   *                              if elseStatement(n) == null
   *                                executedStatement(n) = null
   *                              else
   *                                executedStatement(n) = elseStatement(n)
   * 
   * @modifies nodes in parents
   * 
   * @requires root != null
   * @requires (root instanceof CompilationUnit) \/ parent(root) != null
   * 
   * @ensures fold(root) == (old(top) != emptyset)
   * @ensures forall n in old(top), exists n' in nodes
   *             if executedStatement(n) == null
   *                children(parent(n')) == (children(parent(n)) setminus {n})
   *             else
   *                fresh(n')
*                   /\ value(n') == value(executedStatement(n))
   *                /\ parent(n') == parent(n)
   *                /\ children(parent(n')) == (children(parent(n)) setminus {n}) union {n'}
   *   
   * @param root the root of the tree to traverse.
   * @return true if if statements with boolean predicates were replaced in the rooted tree
   */
  public boolean fold(final ASTNode root) {
    checkRequires(root);
    Visitor visitor = new Visitor();
    root.accept(visitor);
    BlockFolding blockFolding = new BlockFolding();
    blockFolding.fold(root);
    return visitor.didFold;
  }

  private void checkRequires(final ASTNode root) {
    ExceptionUtils.requiresNonNull(root, "Null root passed to IfStatementFolding.fold");

    if (!(root instanceof CompilationUnit) && root.getParent() == null) {
      ExceptionUtils.throwRuntimeException(
          "Non-CompilationUnit root with no parent passed to IfStatementFolding.fold");
    }
  }
}
