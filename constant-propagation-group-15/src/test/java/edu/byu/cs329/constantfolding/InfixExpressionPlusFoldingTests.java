package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;
import edu.byu.cs329.TestUtils;
import java.net.URI;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for folding InfixExpression with plus types")
public class InfixExpressionPlusFoldingTests {
  InfixExpressionPlusFolding folderUnderTest = null;

  @BeforeEach
  void beforeEach() {
    folderUnderTest = new InfixExpressionPlusFolding();
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
    String rootName = "foldingInputs/infixExpressionsPlus/should_ThrowRuntimeException_when_AnOperandIsNotOfTypeInt.java";
    ASTNode root = TestUtils.getASTNodeFor(this, rootName);
    assertThrows(RuntimeException.class, () -> folderUnderTest.fold(root));
  }

  @Test
  @DisplayName("Should not fold anything when there are no addition infix expressions with number literals")
  void should_NotFoldAnything_when_ThereAreNoAdditionInfixExpressionsWithNumberLiterals() {
    String rootName = "foldingInputs/infixExpressionsPlus/should_NotFoldAnything_when_ThereAreNoAdditionInfixExpressionsWithNumberLiterals.java";
    String expectedName = "foldingInputs/infixExpressionsPlus/should_NotFoldAnything_when_ThereAreNoAdditionInfixExpressionsWithNumberLiterals.java";
    TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
  }

  @Test
  @DisplayName("Should only fold addition infix expressions with number literals when given multiple types")
  void should_OnlyFoldAdditionInfixExpressionsWithNumberLiterals_when_GivenMultipleTypes() {
    String rootName = "foldingInputs/infixExpressionsPlus/should_OnlyFoldAdditionInfixExpressionsWithNumberLiterals_when_GivenMultipleTypes-root.java";
    String expectedName = "foldingInputs/infixExpressionsPlus/should_OnlyFoldAdditionInfixExpressionsWithNumberLiterals_when_GivenMultipleTypes.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
}
