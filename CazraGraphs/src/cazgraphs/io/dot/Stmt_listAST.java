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
import cazgraphs.io.parse.ParseUtils;
import cazgraphs.util.StringSlice;

/** stmt_list node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class Stmt_listAST implements AST {
  
  private static Pattern nextSemiRegex = Pattern.compile(" *;");
  
  private StmtAST head;
  
  private Stmt_listAST tail;
  
  
  
  public Stmt_listAST(StmtAST head, Stmt_listAST tail) {
    this.head = head;
    this.tail = tail;
    
    Debug.debugln("Produced stmt_list");
  }
  
  
  
  /** Returns the head statement. */
  public StmtAST getHead() {
    return head;
  }
  
  /** Returns the tail statement list. */
  public Stmt_listAST getTail() {
    return tail;
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<Stmt_listAST> parse(StringSlice src, boolean digraph) {
    Debug.debugln("Parsing stmt_list from: \n" + src);
    
    if(src == null) {
      return new ParseFail<Stmt_listAST>("Could not parse null.");
    }
    src = src.trimLeft();
    int consumedChars = 0;
    
    // Extract the head statement.
    ParseResult<StmtAST> head = StmtAST.parse(src, digraph);
    if(head.start() == 0) {
      consumedChars += head.end();
      StringSlice edibleSrc = src.subslice(head.end());
      
      // Discard any ;s between statements. 
      Matcher semiMatcher = nextSemiRegex.matcher(edibleSrc);
      if(semiMatcher.find() && semiMatcher.start() == 0) {
        consumedChars += semiMatcher.end();
        edibleSrc = edibleSrc.subslice(semiMatcher.end());
      }
      
      // Extract the optional tail statement. 
      ParseResult<Stmt_listAST> tail = Stmt_listAST.parse(edibleSrc, digraph);
      if(tail.success()) {
        consumedChars += tail.end();
      }
      
      Stmt_listAST result = new Stmt_listAST(head.getAST(), tail.getAST());
      return new ParseSuccess<Stmt_listAST>(result, src.start(), src.start() + consumedChars);
    }
    else if("".equals(src.trim())) {
      // An empty statement list is allowed.
      
      Stmt_listAST result = new Stmt_listAST(null, null);
      return new ParseSuccess<Stmt_listAST>(result, 0, 0);
    }
    else {
      return new ParseFail<Stmt_listAST>("Could not parse DOT stmt_list in: " + src);
    }
  }
  
  
  /** Updates the graph with the AST's data. */
  public void updateGraph(GraphSprite graph, Map<String, String> graphAttrs, Map<String, String> nodeAttrs, Map<String, String> edgeAttrs) {
    if(head != null) {
      head.updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
      
      if(tail != null) {
        tail.updateGraph(graph, graphAttrs, nodeAttrs, edgeAttrs);
      }
    }
  }
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    if(head != null) {
      result += head.stringify() + ";";
      
      if(tail != null) {
        result += tail.stringify();
      }
    }
    
    return result;
  }
  
}
