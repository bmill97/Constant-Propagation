package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;
import edu.byu.cs329.TestUtils;
import java.net.URI;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for folding IfStatements types")
public class IfStatementFoldingTests {
    IfStatementFolding folderUnderTest = null;

  @BeforeEach
  void beforeEach() {
    folderUnderTest = new IfStatementFolding();
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
  @DisplayName("Should not fold anything when there are no if statements with boolean predicates")
  void should_NotFoldAnything_when_ThereAreNoIfStatementsWithBooleanPredicates() {
    String rootName = "foldingInputs/ifStatements/should_NotFoldAnything_when_ThereAreNoIfStatementsWithBooleanPredicates.java";
    String expectedName = "foldingInputs/ifStatements/should_NotFoldAnything_when_ThereAreNoIfStatementsWithBooleanPredicates.java";
    TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
  }

  @Test
  @DisplayName("Should only fold if statements with boolean predicates when given multiple types")
  void should_OnlyFoldIfStatementsWithBooleanPredicates_when_GivenMultipleTypes() {
    String rootName = "foldingInputs/ifStatements/should_OnlyFoldIfStatementsWithBooleanPredicates_when_GivenMultipleTypes-root.java";
    String expectedName = "foldingInputs/ifStatements/should_OnlyFoldIfStatementsWithBooleanPredicates_when_GivenMultipleTypes.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }
}
