package cazgraphs.io.dot;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.graph.GraphSprite;
import cazgraphs.io.parse.AST;
import cazgraphs.io.parse.ParseFail;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.io.parse.ParseSuccess;
import cazgraphs.io.parse.ParseUtils;
import cazgraphs.util.StringSlice;

/** node_idAST node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class Node_idAST implements AST {
  
  private static Pattern pattern = null;
  
  private String id;
  
  private String portID;
  
  private String compassPt;
  
  private static Pattern compassRegex = Pattern.compile("(ne|nw|n|se|sw|s|e|w|c|_)");
  
  public Node_idAST(String id, String portID, String compassPt) {
    this.id = id;
    this.portID = portID;
    this.compassPt = compassPt;
    
    Debug.debugln("Produced node_id");
    Debug.debugln("  ID: " + id);
    Debug.debugln("  port: " + portID);
    Debug.debugln("  compassPt: " + compassPt);
  }
  
  /** Returns the ID for this node. */
  public String getID() {
    return id;
  }
  
  /** Returns the port ID, if it exists. */
  public String getPortID() {
    return portID;
  }
  
  /** Returns the compass point, if it exists. */
  public String getCompassPt() {
    return compassPt;
  }
  
  
  /** Returns the regex for this AST. */
  public static Pattern getRegex() {
    if(pattern == null) {
    //  "([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)( *: *([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)( *: *(ne|nw|n|se|sw|s|e|w|c|_))?)?"
      pattern = Pattern.compile(IDAST.getRegex().pattern() + "( *: *" + IDAST.getRegex().pattern() + "( *: *" + compassRegex.pattern() + ")?)?");
    }
    return pattern;
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<Node_idAST> parse(StringSlice src) {
    Debug.debugln("Parsing node_id from: \n" + src);
    
    if(src == null) {
      return new ParseFail<Node_idAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    Matcher matcher = getRegex().matcher(src);
    if(matcher.find() && matcher.start() == 0) {
      String nodeID = matcher.group(1);
      String portID = matcher.group(6);
      String compassPt = matcher.group(11);
      
      if(compassPt == null && portID != null && compassRegex.matcher(portID).matches()) {
        compassPt = portID;
        portID = null;
      }
      
      Node_idAST result = new Node_idAST(nodeID, portID, compassPt);
      return new ParseSuccess<Node_idAST>(result, src.start(), src.start() + matcher.end());
    }
    else {
      return new ParseFail<Node_idAST>("Could not parse DOT node_id in: " + src);
    }
  }
  
  
  
  public void updateGraph(GraphSprite graph, Map<String, String> nodeAttrs) {
    if(!graph.hasVertex(id)) {
      graph.addVertex(id);
      
      Debug.debugln("Added vertex: " + id);
      
      for(String key : nodeAttrs.keySet()) {
        String val = nodeAttrs.get(key);
        
        // TODO: Put key -> val into the vertex's attributes.
      }
    }
  }
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    result += id;
    
    if(portID != null) {
      result += ":" + portID;
    }
    if(compassPt != null) {
      result += ":" + compassPt;
    }
    
    return result;
  }
  
}
