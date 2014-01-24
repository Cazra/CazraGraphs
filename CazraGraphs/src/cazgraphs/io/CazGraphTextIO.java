package cazgraphs.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cazgraphs.CazgraphException;
import cazgraphs.graph.GraphSprite;

/** 
 * This class provides utilities for reading and writing graphs in the simple 
 * CazGraphs text format. Graph text files in this format are of the 
 * following form:
 * <br/>The file is divided into 4 section separated by lines containing only 
 * "--". 
 * <br/>The first section is a comment section that serves as both a header
 * describing the graph and as a place to comment about the vertices.
 * <br/>The second section contains 0 or more lines listing the vertices of the 
 * graph. Each vertex is on its own line and is identified by a unique name.
 * <br/>The third section is a comment section for the edges of the graph.
 * <br/>The fourth section contains 0 or more lines describing the outward
 * edges of each vertex. Each line is of the form
 * <br/>[source vertex] -> [comma-delimited list of destination vertices]
 */
public class CazGraphTextIO {
  
  /** Whether debug messages are enabled for this class. False by default. */
  public static boolean debugEnabled = false;
  
  /** Reads a graph sprite from a file in the CazGraph text format. */
  public static GraphSprite loadFromFile(String filepath) {
    GraphSprite graph = new GraphSprite();
    try {
      BufferedReader br = new BufferedReader(new FileReader(filepath));
      String input = "";
      String line = br.readLine();
      while(line != null) {
        input += line + "\n";
        line = br.readLine();
      }
      
      return loadFromString(input);
    }
    catch(Exception e) {
      throw new CazgraphException("Could not read graph from file: " + filepath, e);
    }
  }
  
  /** Reads a graph sprite from a string in the CazGraph text format. */
  public static GraphSprite loadFromString(String str) {
    GraphSprite graph = new GraphSprite();
    String[] sections = str.split("--");
      
    // vertices
    debugln("vertices: ");
    String[] vertices = sections[1].split("\n");
    for(String vertex : vertices) {
      debugln(vertex);
      if(!vertex.equals("")) {
        graph.addVertex(vertex.trim());
      }
    }
    
    // edges
    debugln("edges: ");
    String[] edges = sections[3].split("\n");
    for(String edge : edges) {
      debugln(edge);
      if(!edge.equals("")) {
        String[] ends = edge.split("->");
        String source = ends[0].trim();
        
        if(!ends[1].trim().equals("")) {
          String[] targets = ends[1].split(",");
          for(String target : targets) {
            target = target.trim();
            graph.addEdge(source, target);
          }
        }
      }
    }
    
    return graph;
  }
  
  
  
  
  /** Saves the graph to a file in the CazGraph text format. */
  public static void saveToFile(GraphSprite graph, String filepath) {
    try {
      FileWriter fw = new FileWriter(filepath);
      fw.write(stringify(graph));
      fw.close();
    }
    catch(Exception e) {
      throw new CazgraphException("Could not save graph to file: " + filepath, e);
    }
  }
  
  
  
  
  /** Creates a String representation of a graph in CazGraph text format. */
  public static String stringify(GraphSprite graph) {
    String result = "Vertices\n--\n";
      
    List<String> sortedVertices = new ArrayList<>(graph.getVertexIDs());
    Collections.sort(sortedVertices);
    
    for(String name : sortedVertices) {
      result += name + "\n";
    }
    
    
    result += "--\nEdges\n--\n";
    for(String name : sortedVertices) {
      result += name + " -> ";
      
      List<String> sortedEdges = new ArrayList<>(graph.getEdges(name));
      Collections.sort(sortedEdges);
      
      boolean first = true;
      for(String toID : sortedEdges) {
        if(first) {
          first = false;
        }
        else {
          result += ", ";
        }
        result += toID;
      }
      result += "\n";
    }
    
    return result;
  }
  
  
  
  /** Prints debugging messages if debugging is enabled for this class. */
  private static void debugln(String str) {
    if(debugEnabled) {
      System.out.println(str);
    }
  }
}

