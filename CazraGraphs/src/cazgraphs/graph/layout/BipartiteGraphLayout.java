package cazgraphs.graph.layout;

import java.awt.geom.*;
import java.util.*;

import cazgraphs.graph.*;


/** 
 * Arranges the graph according to its bipartiteness properties. 
 * Vertices in the first set are arranged in a column to the left. 
 * Vertices in the second set are arranged in a column to the right.
 * Any nodes whose inclusion would not satisfy bipartiteness are arranged in a row
 * under the columns.
 * Each set is sorted in alphabetical order by node IDs.
 */
public class BipartiteGraphLayout extends GraphLayout {
  
  public String startNodeID;
  
  public int hspacing;
  
  public int vspacing;
  
  public BipartiteGraphLayout(String startNodeID, int hspacing, int vspacing) {
    super();
    this.startNodeID = startNodeID;
    this.hspacing = hspacing;
    this.vspacing = vspacing;
  }
  
  
  public void stepLayout(GraphSprite graph) {
    if(isFrozen()) {
      return;
    }
    
    // Get the sorted sets.
    List<Set<String>> bipartite = GraphSolver.bicolorGraph(graph.getGraph(), startNodeID);
    
    List<String> set1 = new ArrayList<>(bipartite.get(0));
    Collections.sort(set1);
    
    List<String> set2 = new ArrayList<>(bipartite.get(1));
    Collections.sort(set2);
    
    List<String> oddSet = new ArrayList<>(bipartite.get(2));
    Collections.sort(oddSet);
    
    // arrange set 1.
    double offsetY = 0;
    double offsetX = 0;
    double maxoffsetY = 0;
    
    for(String vertexID : set1) {
      VertexSprite vertex = graph.getSprite(vertexID);
      Dimension2D dims = vertex.getDimensions();
      vertex.x = 0-dims.getWidth()/2;
      vertex.y = offsetY + dims.getHeight()/2;
      
      offsetY += dims.getHeight() + vspacing;
    }
    
    maxoffsetY = offsetY;
    
    // arrange set 2.
    offsetY = 0;
    offsetX += hspacing;
    
    for(String vertexID : set2) {
      VertexSprite vertex = graph.getSprite(vertexID);
      Dimension2D dims = vertex.getDimensions();
      vertex.x = offsetX + dims.getWidth()/2;
      vertex.y = offsetY + dims.getHeight()/2;
      
      offsetY += dims.getHeight() + vspacing;
    }
    
    maxoffsetY = Math.max(maxoffsetY, offsetY) + vspacing;
    
    // arrange the odd set.
    offsetX = 0;
    
    for(String vertexID : oddSet) {
      VertexSprite vertex = graph.getSprite(vertexID);
      Dimension2D dims = vertex.getDimensions();
      vertex.x = offsetX;
      vertex.y = maxoffsetY + dims.getHeight()/2;
      
      offsetX += dims.getWidth() + vspacing;
    }
    
    setFrozen(true);
  }
  
}
