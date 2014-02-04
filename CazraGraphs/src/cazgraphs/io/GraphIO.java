package cazgraphs.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.graph.GraphSprite;


/** Subclasses of this are able to save and load graphs in various text formats. */
public abstract class GraphIO {
  
  
  
  /** Returns the default lowercase extension for files using this graph text format. */
  public abstract String getDefaultFileExtension();
  
  
  
  /** Reads a graph sprite from a file. */
  public GraphSprite loadFromFile(String filepath) {
    GraphSprite graph = new GraphSprite();
    try {
      BufferedReader br = new BufferedReader(new FileReader(filepath));
      String input = "";
      String line = br.readLine();
      while(line != null) {
        input += line + "\n";
        line = br.readLine();
      }
      
      return unstringify(input);
    }
    catch(Exception e) {
      throw new CazgraphException("Could not read graph from file: " + filepath, e);
    }
  }
  
  
  
  /** Reads a graph sprite from a string representation of it. */
  public abstract GraphSprite unstringify(String str);
  
  
  
  /** Saves the graph to a file. */
  public void saveToFile(GraphSprite graph, String filepath) {
    try {
      FileWriter fw = new FileWriter(filepath);
      fw.write(stringify(graph));
      fw.close();
    }
    catch(Exception e) {
      throw new CazgraphException("Could not save graph to file: " + filepath, e);
    }
  }
  
  
  
  /** Creates a String representation of a graph. */
  public abstract String stringify(GraphSprite graph);
}
