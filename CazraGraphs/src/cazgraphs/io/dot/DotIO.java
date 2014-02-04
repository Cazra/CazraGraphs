package cazgraphs.io.dot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.graph.GraphSprite;
import cazgraphs.graph.model.DirectedGraph;
import cazgraphs.graph.model.UndirectedGraph;
import cazgraphs.io.GraphIO;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.util.StringSlice;

/** 
 * Simple IO for the GraphVis DOT format. Currently, this only reads the 
 * vertices and their edges, and doesn't read any style attributes. */
public class DotIO extends GraphIO {
  
  private static DotIO instance = null;
  
  private static long nextAnonID = 0;
  
  /** Returns the singleton instance of this class. */
  public static DotIO getInstance() {
    if(instance == null) {
      instance = new DotIO();
    }
    return instance;
  }
  
  
  
  /** The default file extension for DOT text files is "dot". */
  public static String defaultFileExtension() {
    return "dot";
  }
  
  
  public String getDefaultFileExtension() {
    return defaultFileExtension();
  }
  
  
  
  /** Reads a graph from DOT text representation. */
  public GraphSprite unstringify(String str) {
    // canonize new lines.
    str = str.replace("\r\n", "\n").replace("\r", "\n");
    str = removeComments(str);
    Debug.debugln(str);
    return parseGraph(str);
  }
  
  
  
  /** Removes all comments from a DOT source string. */
  private String removeComments(String str) {
    boolean inDQ = false;
    boolean inSQ = false;
    
    String result = str;
    
    for(int i = 0; i < result.length()-1; i++) {
      char c = result.charAt(i);
      
      if(inDQ) {
        if(c == '\"' && result.charAt(i-1) != '\\') {
          inDQ = false;
        }
      }
      else if(inSQ) {
        if(c == '\'' && result.charAt(i-1) != '\\') {
          inSQ = false;
        }
      }
      else {
      
        // Line comment
        if(c == '/' && result.charAt(i+1) == '/') {
          int newLineIndex = result.indexOf("\n", i);
          
          result = result.substring(0, i) + result.substring(newLineIndex);
          i--;
        }
        
        // Block comment
        else if(c == '/' && result.charAt(i+1) == '*') {
          int endBlockIndex = result.indexOf("*/", i) + 2;
          
          result = result.substring(0, i) + result.substring(endBlockIndex);
          i--;
        }
        
        // Python comment
        else if(c == '#' && (i == 0 || result.charAt(i-1) == '\n')) {
          int newLineIndex = result.indexOf("\n", i);
          
          result = result.substring(0, i) + result.substring(newLineIndex+1); 
          i--;
        }
        else if(c == '\"') {
          inDQ = true;
        }
        else if(c == '\'') {
          inSQ = true;
        }
      }
    }
    
    return result;
  }
  
  
  
  /** 
   * Parses a graph from the source string. If the graph could not be parsed, 
   * a CazgraphException is thrown. 
   */
  private GraphSprite parseGraph(String str) {
    ParseResult<GraphAST> result = GraphAST.parse(new StringSlice(str));
    if(result.success()) {
      GraphAST grAST = result.getAST();
      Debug.debugln("Parse success. Stringified version of result: \n" + grAST.stringify());
      return grAST.toGraph();
    }
    else {
      throw new CazgraphException("Could not parse graph: " + result.getMessage());
    }
  }
  
  
  
  
  
  
  /** Creates a DOT text respresentation for a graph. */
  public String stringify(GraphSprite graph) {
    String result = "";
    
    boolean isDirected = !(graph.getGraph() instanceof UndirectedGraph);
    
    if(isDirected) {
      result += "digraph ";
    }
    else {
      result += "graph ";
    }
    result += "cazgraph {\n";
    
    
    Set<String> usedUndirectedEdges = new HashSet<>();
    
    for(String id : graph.getVertexIDs()) {
      
      result += "\"" + id + "\";\n";
      
      for(String edgeID : graph.getEdges(id)) {
        if(isDirected) {
          result += "\"" + id + "\"" + " -> " + "\"" + edgeID + "\";\n";
        }
        else {
          String edge1 = "\"" + id + "\"" + " -> " + "\"" + edgeID + "\";\n";
          String edge2 = "\"" + edgeID + "\"" + " -- " + "\"" + id + "\";\n";
          
          if(!usedUndirectedEdges.contains(edge1) && !usedUndirectedEdges.contains(edge2)) {
            usedUndirectedEdges.add(edge1);
            usedUndirectedEdges.add(edge2);
            
            result += edge1;
          }
        }
      }
    }
    
    
    result += "}";
    return result;
  }
  
  
  /** 
   * Produces an anonymous ID for a graph or subgraph whose ID wasn't defined. 
   * Anonymous IDs in a DOT graph begin with a ; followed by some integer.
   * These IDs should never collide with explicitly defined IDs, since explicit
   * IDs aren't allowed to start with ;.
   */
  public static String makeAnonID() {
    String id = "__anon" + nextAnonID;
    nextAnonID++;
    return id;
  }
  
}


