package cazgraphs.io.dot;

import java.util.Map;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.graph.GraphSprite;
import cazgraphs.io.parse.AST;
import cazgraphs.io.parse.ParseFail;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.io.parse.ParseSuccess;
import cazgraphs.io.parse.ParseUtils;
import cazgraphs.util.StringSlice;

/** 
 * A vertex AST is just a convenience AST that wraps either a node_id or subgraph AST.
 */
public class VertexAST implements AST {

  private AST ast;
  
  /** Creates a vertex wrapping a node_id. */
  public VertexAST(Node_idAST node_id) {
    ast = node_id;
    
    Debug.debugln("Produced vertex");
  }
  
  /** Creates a vertex wrapping a subgraph. */
  public VertexAST(SubgraphAST subgraph) {
    ast = subgraph;
    
    Debug.debugln("Produced vertex");
  }
  
  
  /** Returns the node_id or subgraph wrapped by this AST. */
  public AST getWrappedAST() {
    return ast;
  }
  
  /** Returns the ID of the wrapped node or subgraph. */
  public String getID() {
    if(ast instanceof Node_idAST) {
      return ((Node_idAST) ast).getID();
    }
    else if(ast instanceof SubgraphAST) {
      return ((SubgraphAST) ast).getID();
    }
    else {
      throw new CazgraphException("VertexAST is wrapping an AST that is neither a Node_idAST nor a SubgraphAST.");
    }
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<VertexAST> parse(StringSlice src, boolean digraph) {
    Debug.debugln("Parsing vertex from: \n" + src);
    
    if(src == null) {
      return new ParseFail<VertexAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    ParseResult<SubgraphAST> subgraph = SubgraphAST.parse(src, digraph);
    if(subgraph.start() == 0) {
      VertexAST result = new VertexAST(subgraph.getAST());
      return new ParseSuccess<VertexAST>(result, src.start(), src.start() + subgraph.end());
    }
    
    ParseResult<Node_idAST> node_id = Node_idAST.parse(src);
    if(node_id.start() == 0) {
      VertexAST result = new VertexAST(node_id.getAST());
      return new ParseSuccess<VertexAST>(result, src.start(), src.start() + node_id.end());
    }
    
    return new ParseFail<VertexAST>("Could not parse DOT vertex in: " + src);
  }
  
  
  /** Updates the graph with this AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs) {
    if(ast instanceof Node_idAST) {
      ((Node_idAST) ast).updateGraph(graph, nodeAttrs);
    }
    else if(ast instanceof SubgraphAST) {
      ((SubgraphAST) ast).updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
    }
    else {
      throw new CazgraphException("VertexAST is wrapping an AST that is neither a Node_idAST nor a SubgraphAST.");
    }
  }
  
  
  
  @Override
  public String stringify() {
    return ast.stringify();
  }
}
