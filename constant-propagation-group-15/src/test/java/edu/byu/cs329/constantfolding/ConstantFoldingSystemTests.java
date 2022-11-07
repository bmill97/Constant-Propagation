package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.byu.cs329.TestUtils;

@DisplayName("System Tests for Constant Folding")
public class ConstantFoldingSystemTests {
  static Logger log = LoggerFactory.getLogger(ConstantFoldingSystemTests.class);

  @Test
  @DisplayName("Should fold all constants when given multiple types")
  void should_FoldAllConstants_when_GivenMultipleTypes() {
    String rootName = "foldingInputs/system/should_FoldAllConstants_when_GivenMultipleTypes-root.java";
    String expectedName = "foldingInputs/system/should_FoldAllConstants_when_GivenMultipleTypes.java";
    assertFilesEqualAfterFold(this, rootName, expectedName);
  }

  private void assertFilesEqualAfterFold(final Object t, String rootName, String expectedName) {
    ASTNode root = TestUtils.getASTNodeFor(t, rootName);
    ConstantFolding.fold(root);
    ASTNode expected = TestUtils.getASTNodeFor(t, expectedName);
    assertTrue(expected.subtreeMatch(new ASTMatcher(), root));
  }
}
