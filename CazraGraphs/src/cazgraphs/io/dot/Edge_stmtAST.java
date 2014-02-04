package cazgraphs.io.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

/** edge_stmt node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class Edge_stmtAST implements AST {
  
  private VertexAST lhs;
  
  private EdgeRHSAST rhs;
  
  private Attr_listAST attributes;
  
  
  
  
  public Edge_stmtAST(VertexAST lhs, EdgeRHSAST rhs, Attr_listAST attributes) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.attributes = attributes;
    
    Debug.debugln("Produced edge_stmt");
  }
  
  /** Returns the vertex on the LHS of the statement. */
  public VertexAST getLHS() {
    return lhs;
  }
  
  /** Returns the RHS of the statement. */
  public EdgeRHSAST getRHS() {
    return rhs;
  }
  
  /** Returns the attributes list for the statement, if it exists. Otherwise null is returned. */
  public Attr_listAST getAttributes() {
    return attributes;
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<Edge_stmtAST> parse(StringSlice src, boolean digraph) {
    Debug.debugln("Parsing edge_stmt from: \n" + src);
    
    if(src == null) {
      return new ParseFail<Edge_stmtAST>("Could not parse null.");
    }
    src = src.trimLeft();
    int consumedChars = 0;
    
    // Extract the LHS.
    ParseResult<VertexAST> lhs = VertexAST.parse(new StringSlice(src), digraph);
    if(lhs.start() != 0) {
      return new ParseFail<Edge_stmtAST>(lhs.getMessage());
    }
    StringSlice edibleSrc = src.subslice(lhs.end());
    consumedChars += edibleSrc.start();
    
    edibleSrc = edibleSrc.trimLeft();
    consumedChars += edibleSrc.start();
    
    // Extract the RHS.
    ParseResult<EdgeRHSAST> rhs = EdgeRHSAST.parse(edibleSrc, digraph);
    if(rhs.start() != 0) {
      return new ParseFail<Edge_stmtAST>(rhs.getMessage());
    }
    edibleSrc = edibleSrc.subslice(rhs.end());
    consumedChars += edibleSrc.start();
    
    edibleSrc = edibleSrc.trimLeft();
    consumedChars += edibleSrc.start();
    
    // Extract the optional attributes list.
    ParseResult<Attr_listAST> attrs = Attr_listAST.parse(edibleSrc);
    if(attrs.success()) {
      consumedChars += attrs.end();
    }
    
    Edge_stmtAST result = new Edge_stmtAST(lhs.getAST(), rhs.getAST(), attrs.getAST());
    return new ParseSuccess<Edge_stmtAST>(result, src.start(), src.start() + consumedChars);
  }
  
  
  
  
  /** Updates the graph with this AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs) {
    if(attributes != null) {
      edgeAttrs = attributes.mergeWith(edgeAttrs);
    }
    
    // Process the edges in the chain.
    String fromID = lhs.getID();
    lhs.updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
    
    rhs.updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs, fromID);
  }
  
  
  @Override
  public String stringify() {
    String result = "";
    
    result += lhs.stringify();
    result += rhs.stringify();
    
    if(attributes != null) {
      result += " " + attributes.stringify();
    }
    
    return result;
  }
  
}
