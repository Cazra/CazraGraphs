package cazgraphs.io.parse;

/** 
 * A class containing result information for an attempt to parse an AST from 
 * some source String. 
 */
public abstract class ParseResult<T extends AST>  {
  
  private T ast;
  
  private int start;
  
  private int end;
  
  private String message;
  
  protected ParseResult(T ast, int start, int end, String message) {
    this.ast = ast;
    this.start = start;
    this.end = end;
    this.message = message;
  }
  
  /** Returns the AST that resulted from the parse. Null if the parse was unsuccessful. */
  public T getAST() {
    return ast;
  }
  
  /** Returns whether the parse was successful. */
  public abstract boolean success();
  
  /** Returns the inclusive start index of the AST in the source string it was parsed from. */
  public int start() {
    return start;
  }
  
  /** Returns the exclusive end index of the AST in the source string it was parsed from. */
  public int end() {
    return end;
  }
  
  /** Returns the message included with a failed result. */
  public String getMessage() {
    return message;
  }
  
}
