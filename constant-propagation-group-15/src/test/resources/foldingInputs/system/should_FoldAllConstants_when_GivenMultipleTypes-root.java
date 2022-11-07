package foldingInputs.system;

public class Name {
  public int name(final int y) {
    //this statement combines if, infix plus, infix less than, logical not, parenthesized, and block
    if (!(!((3 + 7) < (7 + (4) + 3 + 2)))) {
      final int q = 4;
    }
    
    final int x = (((3))) + (y);
    final boolean b = ((true));
    final Integer i = (null);
    final char c = ('c');
    final String s = new String(("Hello"));
    final boolean t = ((Name.class) == Name.class);
    if (false) {
      final int d = 4;
    }
    if (false) {
      final int e = 4;
    } else {
      final int f = 4;
    }
    if (true) {
      final int g = 4;
    } else {
      final int h = 4;
    }
    return x;
  }
}