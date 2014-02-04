package cazgraphs.io.parse;

import cazgraphs.Debug;

/** Result of a failed AST parse. */
public class ParseFail<T extends AST> extends ParseResult<T> {
  
  
  public ParseFail() {
    super(null, -1, -1, "");
  }
  
  
  public ParseFail(String message) {
    super(null, -1, -1, message);
    Debug.debugln("Parse failed: " + message);
  }
  
  
  @Override
  public boolean success() {
    return false;
  }
  
}
