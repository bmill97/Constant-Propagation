package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;
import edu.byu.cs329.TestUtils;
import java.net.URI;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for folding InfixExpression with less than types")
public class InfixExpressionLessThanFoldingTests {
    InfixExpressionLessThanFolding folderUnderTest = null;

    @BeforeEach
    void beforeEach() {
      folderUnderTest = new InfixExpressionLessThanFolding();
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
    @DisplayName("Should throw RuntimeException when an operand is not of type int")
    void should_ThrowRuntimeException_when_AnOperandIsNotOfTypeInt() {
      String rootName = "foldingInputs/infixExpressionsLessThan/should_ThrowRuntimeException_when_AnOperandIsNotOfTypeInt.java";
      ASTNode root = TestUtils.getASTNodeFor(this, rootName);
      assertThrows(RuntimeException.class, () -> folderUnderTest.fold(root));
    }
  
    @Test
    @DisplayName("Should not fold anything when there are no less than infix expressions with number literals")
    void should_NotFoldAnything_when_ThereAreNoLessThanInfixExpressionsWithNumberLiterals() {
      String rootName = "foldingInputs/infixExpressionsLessThan/should_NotFoldAnything_when_ThereAreNoLessThanInfixExpressionsWithNumberLiterals.java";
      String expectedName = "foldingInputs/infixExpressionsLessThan/should_NotFoldAnything_when_ThereAreNoLessThanInfixExpressionsWithNumberLiterals.java";
      TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
    }
  
    @Test
    @DisplayName("Should only fold less than infix expressions with number literals when given multiple types")
    void should_OnlyFoldLessThanInfixExpressionsWithNumberLiterals_when_GivenMultipleTypes() {
      String rootName = "foldingInputs/infixExpressionsLessThan/should_OnlyFoldLessThanInfixExpressionsWithNumberLiterals_when_GivenMultipleTypes-root.java";
      String expectedName = "foldingInputs/infixExpressionsLessThan/should_OnlyFoldLessThanInfixExpressionsWithNumberLiterals_when_GivenMultipleTypes.java";
      TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
    }
    
}
