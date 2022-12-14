package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

@DisplayName("Tests for folding PrefixExpression types")
public class LogicalNotPrefixExpressionFoldingTests {
    LogicalNotPrefixExpressionFolding folderUnderTest = null;

    @BeforeEach
    void beforeEach() {
      folderUnderTest = new LogicalNotPrefixExpressionFolding();
    }
  
    @Test
    @DisplayName("Should throw RuntimeException when root is null")
    void should_ThrowRuntimeException_when_RootIsNull() {
      assertThrows(RuntimeException.class, () -> {
        folderUnderTest.fold(null);
      });
    }
  
    @Test
    @DisplayName("Should throw RuntimeException when root is not a CompilationUnit and has no parent")
    void should_ThrowRuntimeException_when_RootIsNotACompilationUnitAndHasNoParent() {
      assertThrows(RuntimeException.class, () -> {
        URI uri = TestUtils.getUri(this, "");
        ASTNode compilationUnit = TestUtils.getCompilationUnit(uri);
        ASTNode root = compilationUnit.getAST().newNullLiteral();
        folderUnderTest.fold(root);
      });
    }
  
    @Test
    @DisplayName("Should not fold anything when there are no logical not prefix expressions")
    void should_NotFoldAnything_when_ThereAreNoLogicalNotPrefixExpressions() {
      String rootName = "foldingInputs/prefixExpressions/should_NotFoldAnything_when_ThereAreNoLogicalNotPrefixExpressions.java";
      String expectedName = "foldingInputs/prefixExpressions/should_NotFoldAnything_when_ThereAreNoLogicalNotPrefixExpressions.java";
      TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
    }
  
    @Test
    @DisplayName("Should only fold prefix expressions with logical not operators when given multiple types")
    void should_OnlyFoldPrefixExpressionsWithLogicalNotOperators_when_GivenMultipleTypes() {
      String rootName = "foldingInputs/prefixExpressions/should_OnlyFoldPrefixExpressionsWithLogicalNotOperators_when_GivenMultipleTypes-root.java";
      String expectedName = "foldingInputs/prefixExpressions/should_OnlyFoldPrefixExpressionsWithLogicalNotOperators_when_GivenMultipleTypes.java";
      TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
    }    
}
