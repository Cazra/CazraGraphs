package cazgraphs;


/** 
 * A type of RuntimeException generated by components of the Cazra Graphs framework 
 * to report errors.
 */
public class CazgraphException extends RuntimeException {
  
  public CazgraphException() {
    super();
  }
  
  public CazgraphException(String message) {
    super(message);
  }
  
  public CazgraphException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public CazgraphException(Throwable cause) {
    super(cause);
  }
  
}
