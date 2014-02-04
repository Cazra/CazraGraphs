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
import cazgraphs.io.parse.ParseUtils;
import cazgraphs.util.StringSlice;

/** edgeRHS node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class EdgeRHSAST implements AST {
  
  public static Pattern edgeOpRegex = Pattern.compile("(--|->)");
  
  private boolean directed;
  
  private VertexAST vertex;
  
  private EdgeRHSAST rhs;
  
  
  public EdgeRHSAST(boolean directed, VertexAST vertex, EdgeRHSAST rhs) {
    this.directed = directed;
    this.vertex = vertex;
    this.rhs = rhs;
    
    Debug.debugln("Produced edgeRHS");
  }
  
  /** Returns whether the edge is directed. */
  public boolean isDirected() {
    return directed;
  }
  
  /** Returns the vertex at the RHS of the edge. */
  public VertexAST getVertex() {
    return vertex;
  }
  
  /** Returns the next chained RHS, if there is one. Otherwise, null is returned. */
  public EdgeRHSAST getRHS() {
    return rhs;
  }
  
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<EdgeRHSAST> parse(StringSlice src, boolean digraph) {
    Debug.debugln("Parsing edgeRHS from: \n" + src);
    
    if(src == null) {
      return new ParseFail<EdgeRHSAST>("Could not parse null.");
    }
    src = src.trimLeft();
    int consumedChars = 0;
    
    // must start with an edgeop.
    Matcher edgeop = edgeOpRegex.matcher(src);
    if(edgeop.find() && edgeop.start() == 0) {
      StringSlice edibleSrc = src.subslice(edgeop.end());
      consumedChars += edibleSrc.start();
      
      edibleSrc = edibleSrc.trimLeft();
      consumedChars += edibleSrc.start();
      
      // Extract vertex.
      ParseResult<VertexAST> vertex = VertexAST.parse(edibleSrc, digraph);
      if(vertex.start() != 0) {
        return new ParseFail<EdgeRHSAST>(vertex.getMessage());
      }
      edibleSrc = edibleSrc.subslice(vertex.end());
      consumedChars += edibleSrc.start();
      
      edibleSrc = edibleSrc.trimLeft();
      consumedChars += edibleSrc.start();
      
      // Extract optional RHS.
      ParseResult<EdgeRHSAST> rhs = EdgeRHSAST.parse(edibleSrc, digraph);
      if(rhs.start() == 0) {
        consumedChars += rhs.end();
      }
      
      EdgeRHSAST result = new EdgeRHSAST(digraph, vertex.getAST(), rhs.getAST());
      return new ParseSuccess<EdgeRHSAST>(result, src.start(), src.start() + consumedChars);
    }
    else {
      return new ParseFail<EdgeRHSAST>("Could not parse DOT edgeRHS in: " + src);
    }
  }
  
  
  
  /** Updates the graph with this AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs, String fromID) {
    String toID = vertex.getID();
    vertex.updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
    
    graph.addEdge(fromID, toID);
    // TODO: Process edge attributes.
    
    // Recursively process the chain of edges.
    if(rhs != null) {
      rhs.updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs, toID);
    }
  }
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    if(directed) {
      result += " -> ";
    }
    else {
      result += " -- ";
    }
    
    result += vertex.stringify();
    
    if(rhs != null) {
      result += rhs.stringify();
    }
    
    return result;
  }
}

