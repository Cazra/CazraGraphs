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

/** subgraph node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class SubgraphAST implements AST {
  
  private static Pattern declPattern = null;
  
  
  private String id;
  
  private Stmt_listAST statementList;
  
  private boolean digraph;
  
  
  public SubgraphAST(String id, Stmt_listAST statementList, boolean digraph) {
    this.id = id;
    this.statementList = statementList;
    this.digraph = digraph;
    
    Debug.debugln("Produced subgraph");
    Debug.debugln("  ID: " + id);
  }
  
  
  /** Returns the ID of the subgraph, if it exists. */
  public String getID() {
    return id;
  }
  
  /** Returns the AST for the subgraph's statement list. */
  public Stmt_listAST getStatementList() {
    return statementList;
  }
  
  
  
  
  /** Returns the regex for this the nonrecursive part of the AST. */
  public static Pattern getDeclRegex() {
    if(declPattern == null) {
    //  "(subgraph( +([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))? *)?\\{"
      declPattern = Pattern.compile("(subgraph( +" + IDAST.getRegex().pattern() + ")? *)?\\{");
    }
    return declPattern;
  }
  
  
  /** Creates a subgraph AST from a the first appearance of a subgraph in a source string. */
  public static ParseResult<SubgraphAST> parse(StringSlice src, boolean digraph) {
    Debug.debugln("Parsing subgraph from: \n" + src);
    
    if(src == null) {
      return new ParseFail<SubgraphAST>("Could not parse null.");
    }
    src = src.trimLeft();
    int consumedChars = 0;
    
    // Extract the optional ID from the optional subgraph declaration.
    Matcher declMatcher = getDeclRegex().matcher(src);
    if(declMatcher.find() && declMatcher.start() == 0) {
      String id = declMatcher.group(3);
      if(id == null) {
        id = DotIO.makeAnonID();
      }
      
      StringSlice edibleSrc = src.subslice(declMatcher.end()-1);
      consumedChars += edibleSrc.start();
      
      // Find the left curly brace containing the contents of the subgraph.
      int leftCurly = edibleSrc.indexOf("{");
      if(leftCurly != 0) {
        return new ParseFail<SubgraphAST>("DOT { at " + leftCurly + " in: " + edibleSrc);
      }
      
      // Find the right curly brace containing the contents of the subgraph. 
      int rightCurly = ParseUtils.findRight(edibleSrc, leftCurly);
      if(rightCurly < 0) {
        return new ParseFail<SubgraphAST>("DOT subgraph missing }: " + src);
      }
      
      // Extract the required statements list.
      StringSlice contents = edibleSrc.subslice(leftCurly + 1, rightCurly);
      consumedChars += contents.end()+1;
      
      ParseResult<Stmt_listAST> stmts = Stmt_listAST.parse(contents, digraph);
      
      if(stmts.success()) {
        SubgraphAST result = new SubgraphAST(id, stmts.getAST(), digraph);
        return new ParseSuccess<SubgraphAST>(result, src.start(), src.start() + consumedChars);
      }
      else {
        return new ParseFail<SubgraphAST>(stmts.getMessage());
      }
    }
    else {
      return new ParseFail<SubgraphAST>("Could not parse subgraph from: " + src);
    }
  }
  
  
  
  /** Updates the graph with the AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs) {
    graph.addVertex(this.id);
    
    GraphSprite subgraph;
    if(digraph) {
      subgraph = new GraphSprite(new DirectedGraph());
    }
    else {
      subgraph = new GraphSprite(new UndirectedGraph());
    }
    statementList.updateGraph(subgraph, graphAttrs, nodeAttrs, edgeAttrs);
    
    // Transfer vertices.
    for(String id : subgraph.getVertexIDs()) {
      if(!graph.hasVertex(id)) {
        graph.addVertex(id);
      }
      graph.getSprite(this.id).addChild(graph.getSprite(id));
    }
    
    // Transfer edges.
    for(String fromID : subgraph.getVertexIDs()) {
      for(String toID : subgraph.getEdges(fromID)) {
        graph.addEdge(fromID, toID);
      }
    }
    
    // TODO: Set attributes of transferred vertices.
  }
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    if(id != null && !"".equals(id)) {
      result += "subgraph " + id + " ";
    }
    result += "{\n";
    result += statementList.stringify() + "\n";
    result += "}";
    
    return result;
  }
}

