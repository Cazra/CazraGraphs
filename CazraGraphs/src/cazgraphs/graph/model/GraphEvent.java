package cazgraphs.graph.model;


public class GraphEvent {
  
  /** The object stored in a node has changed. Has the new object as the subject. */
  public static int OBJECT_CHANGED = 1;
  
  /** An edge has been added to a node. Has the edge's end node as the subject. */
  public static int EDGE_ADDED = 2;
  
  /** An edge has been removed from a node. Has the edge's former end node as the subject. */
  public static int EDGE_REMOVED = 3;
  
  /** A node has been added to a graph. Has the node as the subject. */
  public static int NODE_ADDED = 4;
  
  /** A node has been removed from a graph. Has the node as the subject. */
  public static int NODE_REMOVED = 5;
  
  
  /** The object that generated this event. */
  public Object source;
  
  /** The code for the event. */
  public int code;
  
  /** Optional subject for the event, which observers may use to handle it. */
  public Object subject;
  
  
  
  public GraphEvent(Object source, int code, Object subject) {
    this.source = source;
    this.code = code;
    this.subject = subject;
  }
  
  public GraphEvent(Object source, int code) {
    this(source, code, null);
  }
  
}