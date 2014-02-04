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

/** attr_stmt node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class Attr_stmtAST implements AST {
  
  private static Pattern pattern = null;
  
  
  private String type;
  
  private Attr_listAST attributes;
  
  public Attr_stmtAST(String type, Attr_listAST attributes) {
    this.type = type;
    this.attributes = attributes;
    
    Debug.debugln("Produced attr_stmt");
  }
  
  
  /** Returns the type for the attribute statement. */
  public String getType() {
    return type;
  }
  
  
  /** Returns the AST for the list of attributes. */
  public Attr_listAST getAttributes() {
    return attributes;
  }
  
  
  
  
  
  /** Returns the regex for this AST. */
  public static Pattern getRegex() {
    if(pattern == null) {
    //  "(graph|node|edge) *((\\[ *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))( *(;|,) *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)))* *\\])+( *\\[ *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))( *(;|,) *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)))* *\\])*)"
      pattern = Pattern.compile("(graph|node|edge) *(" + Attr_listAST.getRegex().pattern() +")");
    }
    return pattern;
  }
  
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<Attr_stmtAST> parse(StringSlice src) {
    Debug.debugln("Parsing attr_stmt from: \n" + src);
    
    if(src == null) {
      return new ParseFail<Attr_stmtAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    Matcher matcher = getRegex().matcher(src);
    if(matcher.find() && matcher.start() == 0) {
      
      // Required type.
      String type = matcher.group(1);
      
      // Required attributes.
      ParseResult<Attr_listAST> attributes = Attr_listAST.parse(new StringSlice(matcher.group(2)));
      
      Attr_stmtAST result = new Attr_stmtAST(type, attributes.getAST());
      return new ParseSuccess<Attr_stmtAST>(result, src.start(), src.start() + matcher.end());
    }
    else {
      return new ParseFail<Attr_stmtAST>("Could not parse DOT attr_stmt in: " + src);
    }
  }
  
  
  
  /** Updates the graph with the AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs) {
    if("graph".equals(type)) {
      graphAttrs.putAll(attributes.toMap());
    }
    else if("node".equals(type)) {
      nodeAttrs.putAll(attributes.toMap());
    }
    else if("edge".equals(type)) {
      edgeAttrs.putAll(attributes.toMap());
    }
    else {
      throw new CazgraphException("attr_stmt type (" + type + ") is not graph, node, or edge.");
    }
  }
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    result += type + " " + attributes.stringify();
    
    return result;
  }
}

