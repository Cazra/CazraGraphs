package cazgraphs.io.parse;

import cazgraphs.CazgraphException;

/** Result of a successful AST parse. */
public class ParseSuccess<T extends AST> extends ParseResult<T> {
  
  public ParseSuccess(T ast, int start, int end) {
    super(ast, start, end, "");
    
    if(ast == null) {
      throw new CazgraphException("Successful ParseResult cannot have null AST.");
    }
    
    if(start < 0) {
      throw new CazgraphException("Cannot have start index < 0.");
    }
    
    if(end < start) {
      throw new CazgraphException("Cannot have end index < start index.");
    }
  }
  
  @Override
  public boolean success() {
    return true;
  }
  
}