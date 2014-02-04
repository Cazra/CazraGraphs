package cazgraphs.io.dot;

import java.util.HashMap;
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
import cazgraphs.util.StringSlice;

/** node_stmt node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class Node_stmtAST implements AST {
  
  private static Pattern pattern = null;
  
  
  private Node_idAST id;
  
  private Attr_listAST attributes;
  
  public Node_stmtAST(Node_idAST id, Attr_listAST attributes) {
    this.id = id;
    this.attributes = attributes;
    
    Debug.debugln("Produced node_stmt");
  }
  
  
  /** Returns the AST for the node ID. */
  public Node_idAST getNodeID() {
    return id;
  }
  
  /** Returns the AST for the attributes list, if it exists.*/
  public Attr_listAST getAttributes() {
    return attributes;
  }
  
  
  
  /** Returns the regex for this AST. */
  public static Pattern getRegex() {
    if(pattern == null) {
    //  "(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)( *: *([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)( *: *(ne|nw|n|se|sw|s|e|w|c|_))?)?)( *((\\[ *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))( *(;|,) *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)))* *\\])+( *\\[ *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))( *(;|,) *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)))* *\\])*))?"
      pattern = Pattern.compile(Node_idAST.getRegex().pattern() + "( *(" + Attr_listAST.getRegex().pattern() +"))?");
    }
    return pattern;
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<Node_stmtAST> parse(StringSlice src) {
    Debug.debugln("Parsing node_stmt from: \n" + src);
    
    if(src == null) {
      return new ParseFail<Node_stmtAST>("Could not parse null.");
    }
    src = src.trim();
    
    Matcher matcher = getRegex().matcher(src);
    if(matcher.find() && matcher.start() == 0) {
      
      // Required id
      ParseResult<Node_idAST> node_id = Node_idAST.parse(new StringSlice(matcher.group(1)));
      
      // Optional attributes
      ParseResult<Attr_listAST> attrs = Attr_listAST.parse(new StringSlice(matcher.group(14)));
      
      Node_stmtAST result = new Node_stmtAST(node_id.getAST(), attrs.getAST());
      return new ParseSuccess<Node_stmtAST>(result, src.start(), src.start() + matcher.end());
    }
    else {
      return new ParseFail<Node_stmtAST>("Could not parse DOT node_stmt in: " + src);
    }
  }
  
  
  /** Updates the graph with the AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> nodeAttrs) {
    // Add in any explicit attributes to set.
    if(attributes != null) {
      nodeAttrs = attributes.mergeWith(nodeAttrs);
    }
    id.updateGraph(graph, nodeAttrs);
  }
  
  
  @Override
  public String stringify() {
    String result = "";
    
    result += id.stringify();
    if(attributes != null) {
      result += " " + attributes.stringify();
    }
    
    return result;
  }
}