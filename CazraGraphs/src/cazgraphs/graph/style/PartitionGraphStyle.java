package cazgraphs.graph.style;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cazgraphs.graph.*;

/** 
 * A graph style that colors the vertices in a graph based on a provided 
 * partition of the graph's set of nodes.
 * Vertices that don't appear in the partition or appear more than once in the
 * partition are colored dark grey. Other nodes are assigned a color for 
 * their particular subset in the partition.
 */
public class PartitionGraphStyle extends GraphStyle {
  
  /** Maps each vertexID in the source graph to a group number. */
  private Map<String, Integer> groupMap;
  
  /** The number of subsets in the partition.*/
  private int numGroups;
  
  public PartitionGraphStyle(List<Set<String>> partition) {
    super();
    
    groupMap = new HashMap<>();
    numGroups = partition.size();
    
    for(int i = 0; i < numGroups; i++) {
      Set<String> subset = partition.get(i);
      for(String vertexID : subset) {
        if(groupMap.containsKey(vertexID)) {
          groupMap.put(vertexID, -1);
        }
        else {
          groupMap.put(vertexID, i);
        }
      }
    }
  }
  
  
  /** 
   * Selects a color from the HSB color model to represent a particular group. 
   */
  protected Color getColorForGroup(int group, boolean isStroke) {
    if(group < 0 || group >= numGroups) {
      return new Color(0xEEEEEE);
    }
    
    float hue = 0f;
    if(numGroups == 0) {
      hue = 0f;
    }
    else {
      hue = group*1f/numGroups;
    }
    float sat = 0.3f;
    float bright = 1.0f;
    if(isStroke) {
      bright = 0.7f;
    }
    
    return Color.getHSBColor(hue, sat, bright);
  }
  
  
  
  public Color getVertexStrokeColor(VertexSprite node) {
    if(node.isSelected()) {
      return super.getVertexStrokeColor(node);
    }
    else {
      Integer group = groupMap.get(node.getID());
      if(group == null) {
        group = -1;
      }
      
      return getColorForGroup(group, true);
    }
  }
  
  
  public Color getVertexFillColor(VertexSprite node) {
    if(node.isSelected()) {
      return super.getVertexFillColor(node);
    }
    else {
      Integer group = groupMap.get(node.getID());
      if(group == null) {
        group = -1;
      }
      
      return getColorForGroup(group, false);
    }
  }
  
  
  public Color getEdgeColor(VertexSprite n1, VertexSprite n2) {
    return getColorForGroup(-1, true);
  }
  
  
  public int getEdgeThickness(VertexSprite n1, VertexSprite n2) {
    return super.getEdgeThickness(n1, n2);
  }
}
