package cazgraphs.io.parse;

/** 
 * An interface for a node in an AST.
 */
public interface AST {
  
  /** 
   * Produces a parseable String representation of the AST. Parsing this 
   * String should produce an equivalent AST.
   */
  public String stringify();
}
