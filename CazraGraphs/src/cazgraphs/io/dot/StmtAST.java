package cazgraphs.io.dot;

import java.util.HashMap;
import java.util.Map;

import cazgraphs.Debug;
import cazgraphs.graph.GraphSprite;
import cazgraphs.io.parse.AST;
import cazgraphs.io.parse.ParseFail;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.io.parse.ParseSuccess;
import cazgraphs.io.parse.ParseUtils;
import cazgraphs.util.StringSlice;

/** stmt node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class StmtAST implements AST {
  
  private AST ast;
  
  
  public StmtAST(Node_stmtAST ast) {
    this.ast = ast;
    
    Debug.debugln("Produced stmt");
  }
  
  public StmtAST(Edge_stmtAST ast) {
    this.ast = ast;
    
    Debug.debugln("Produced stmt");
  }
  
  public StmtAST(Attr_stmtAST ast) {
    this.ast = ast;
    
    Debug.debugln("Produced stmt");
  }
  
  public StmtAST(AssignmentAST ast) {
    this.ast = ast;
    
    Debug.debugln("Produced stmt");
  }
  
  public StmtAST(SubgraphAST ast) {
    this.ast = ast;
    
    Debug.debugln("Produced stmt");
  }
  
  
  
  /** Returns the wrapped AST. */
  public AST getWrappedAST() {
    return ast;
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<StmtAST> parse(StringSlice src, boolean digraph) {
    Debug.debugln("Parsing stmt from:\n" + src);
    
    if(src == null) {
      return new ParseFail<StmtAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    ParseResult<Edge_stmtAST> edge = Edge_stmtAST.parse(src, digraph);
    if(edge.start() == 0) {
      StmtAST result = new StmtAST(edge.getAST());
      return new ParseSuccess<StmtAST>(result, src.start(), src.start() + edge.end());
    }
    
    ParseResult<AssignmentAST> assign = AssignmentAST.parse(src);
    if(assign.start() == 0) {
      StmtAST result = new StmtAST(assign.getAST());
      return new ParseSuccess<StmtAST>(result, src.start(), src.start() + assign.end());
    }
    
    ParseResult<SubgraphAST> subgraph = SubgraphAST.parse(src, digraph);
    if(subgraph.start() == 0) {
      StmtAST result = new StmtAST(subgraph.getAST());
      return new ParseSuccess<StmtAST>(result, src.start(), src.start() + subgraph.end());
    }
    
    ParseResult<Attr_stmtAST> attr = Attr_stmtAST.parse(src);
    if(attr.start() == 0) {
      StmtAST result = new StmtAST(attr.getAST());
      return new ParseSuccess<StmtAST>(result, src.start(), src.start() + attr.end());
    }
    
    ParseResult<Node_stmtAST> node = Node_stmtAST.parse(src);
    if(node.start() == 0) {
      StmtAST result = new StmtAST(node.getAST());
      return new ParseSuccess<StmtAST>(result, src.start(), src.start() + node.end());
    }
    
    
    return new ParseFail<StmtAST>("Could not parse DOT stmt in: " + src);
  }
  
  
  /** Updates the graph with the AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs) {
    if(ast instanceof Node_stmtAST) {
      ((Node_stmtAST) ast).updateGraph(graph, nodeAttrs);
    }
    else if(ast instanceof Edge_stmtAST) {
      ((Edge_stmtAST) ast).updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
    }
    else if(ast instanceof Attr_stmtAST) {
      ((Attr_stmtAST) ast).updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
    }
    else if(ast instanceof AssignmentAST) {
      // TODO: Process graph attribute.
    }
    else if(ast instanceof SubgraphAST) {
      ((SubgraphAST) ast).updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
    }
  }
  
  
  @Override
  public String stringify() {
    return ast.stringify();
  }
}
