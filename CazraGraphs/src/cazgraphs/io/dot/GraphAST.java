package cazgraphs.io.dot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.graph.GraphSprite;
import cazgraphs.graph.model.DirectedGraph;
import cazgraphs.graph.model.UndirectedGraph;
import cazgraphs.io.parse.AST;
import cazgraphs.io.parse.ParseFail;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.io.parse.ParseSuccess;
import cazgraphs.io.parse.ParseUtils;
import cazgraphs.util.StringSlice;

/** graph node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class GraphAST implements AST {
  
  private static Pattern declPattern = null;
  
  
  private boolean isStrict;
  
  private boolean isDigraph;
  
  private String id;
  
  private Stmt_listAST statementList;
  
  
  /** Manually create a graph node for the DOT AST. */
  public GraphAST(boolean isStrict, boolean isDigraph, String id, Stmt_listAST statementList ) {
    this.isStrict = isStrict;
    this.isDigraph = isDigraph;
    this.id = id;
    this.statementList = statementList;
    
    Debug.debugln("Produced graph");
    Debug.debugln("  strict: " + isStrict);
    Debug.debugln("  isDigraph: " + isDigraph);
    Debug.debugln("  ID: " + id);
  }
  
  
  /** Return whether this is a strict graph. */
  public boolean isStrict() {
    return isStrict;
  }
  
  /** Return whether this is a directed graph. */
  public boolean isDigraph() {
    return isDigraph;
  }
  
  /** Return the ID of the graph, if it exists. */
  public String getID() {
    return id;
  }
  
  /** Return the AST for the graph's statement list. */
  public Stmt_listAST getStatementList() {
    return statementList;
  }
  
  
  
  
  
  /** Returns the regex for this AST. */
  public static Pattern getDeclRegex() {
    if(declPattern == null) {
    //  "(strict +)?(graph|digraph)( +([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))? *\\{"
      declPattern = Pattern.compile("(strict +)?(graph|digraph)( +" + IDAST.getRegex().pattern() + ")? *\\{");
    }
    return declPattern;
  }
  
  
  
  /** Attempts to produce the root graph AST node for a DOT source string. */
  public static ParseResult<GraphAST> parse(StringSlice src) {
    Debug.debugln("Parsing graph from: \n" + src);
    
    if(src == null) {
      return new ParseFail<GraphAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    // Try to match the declaration part of the graph.
    Matcher declMatcher = getDeclRegex().matcher(src);
    if(declMatcher.find() && declMatcher.start() == 0) {
      boolean isStrict = (declMatcher.group(1) != null);
      boolean isDigraph = (declMatcher.group(2).equals("digraph"));
      String id = declMatcher.group(4);
      if(id == null) {
        id = DotIO.makeAnonID();
      }
      
      // Find the left curly brace containing the contents of the graph.
      int leftCurly = src.indexOf("{");
      if(leftCurly < 0) {
        return new ParseFail<GraphAST>("DOT graph missing {: " + src);
      }
      
      // Find the right curly brace containing the contents of the graph.
      int rightCurly = ParseUtils.findRight(src, leftCurly);
      if(rightCurly < 0) {
        return new ParseFail<GraphAST>("DOT graph missing }: " + src);
      }
      
      StringSlice stmtsSlice = src.subslice(leftCurly + 1, rightCurly);
      ParseResult<Stmt_listAST> statementList = Stmt_listAST.parse(stmtsSlice, isDigraph);
      
      GraphAST result = new GraphAST(isStrict, isDigraph, id, statementList.getAST());
      return new ParseSuccess(result, src.start(), src.start() + rightCurly+1);
    }
    else {
      return new ParseFail<GraphAST>("Could not parse DOT graph: " + src);
    }
    
  }
  
  
  /** Produces a graph from this AST. */
  public GraphSprite toGraph() {
    GraphSprite result;
    if(isDigraph) {
      result = new GraphSprite(new DirectedGraph());
    }
    else {
      result = new GraphSprite(new UndirectedGraph());
    }
    
    // TODO: Should we do something with isStrict?
    // TODO: Should we do something with the graph ID?
    
    Map<String, String> graphAttrs = new HashMap<>();
    Map<String, String> nodeAttrs = new HashMap<>();
    Map<String, String> edgeAttrs = new HashMap<>();
    statementList.updateGraph(result, graphAttrs, nodeAttrs, edgeAttrs);
    return result;
  }
  
  
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    if(isStrict) {
      result += "strict ";
    }
    
    if(isDigraph) {
      result += "digraph ";
    }
    else {
      result += "graph ";
    }
    
    if(id != null && !"".equals(id)) {
      result += id + " {\n";
    }
    
    result += statementList.stringify() + "\n";
    
    result += "}";
    return result;
  }
}

