package cazgraphs.graph.style;

import java.awt.*;
import java.util.Map;

import cazgraphs.graph.*;

/** 
 * A graph style that color codes nodes based on the topology of the graph 
 * starting from some specified node. 
 */
public class TopologyGraphStyle extends DefaultGraphStyle {
  
  /** The computed topology for the graph. */
  public Map<String, Integer> topology = null;
  
  /** The maximum depth of the topology. */
  public int maxDepth = -1;
  
  public TopologyGraphStyle() {
    super();
  }
  
  
  /** 
   * Selects a color from the HSB color model to represent a particular depth. 
   * Colors towards the red end of the spectrum are shallow.
   * Colors towards the blue end of the spectrum are deep.
   */
  protected Color getColorForDepth(int depth, boolean isStroke) {
    if(depth < 0 || depth > maxDepth) {
      return new Color(0xEEEEEE);
    }
    
    float hue = 0f;
    if(depth == 0) {
      hue = 0f;
    }
    else {
      hue = (depth-1)*0.7f/maxDepth;
    }
    float sat = 0.3f;
    if(depth == 0) {
      sat = 0.0f;
    }
    float bright = 1.0f;
    if(isStroke) {
      bright = 0.7f;
    }
    
    return Color.getHSBColor(hue, sat, bright);
  }
  
  
  /** Sets the topological information for the style, given the top node. */
  public void setTopology(VertexSprite node) {
    if(node == null) {
      topology = null;
      return;
    }
    
    topology = GraphSolver.simpleTopology(node.getGraph().getGraph(), node.getID());
    maxDepth = -1;
    for(Integer depth : topology.values()) {
      if(depth > maxDepth) {
        maxDepth = depth;
      }
    }
  }
  
  
  
  public Color getVertexStrokeColor(VertexSprite node) {
    if(topology == null || node.isSelected()) {
      return super.getVertexStrokeColor(node);
    }
    else {
      Integer depth = topology.get(node.getID());
      if(depth == null) {
        depth = -1;
      }
      
      return getColorForDepth(depth, true);
    }
  }
  
  
  public Color getVertexFillColor(VertexSprite node) {
    if(topology == null || node.isSelected()) {
      return super.getVertexFillColor(node);
    }
    else {
      Integer depth = topology.get(node.getID());
      if(depth == null) {
        depth = -1;
      }
      
      return getColorForDepth(depth, node.isSink());
    }
  }
  
  
  public Color getEdgeColor(VertexSprite n1, VertexSprite n2) {
    if(topology == null) {
      return getColorForDepth(-1, true);
    }
    else {
      Integer depth1 = topology.get(n1.getID());
      Integer depth2 = topology.get(n2.getID());
      if(depth1 == null) {
        depth1 = -1;
      }
      if(depth2 == null) {
        depth2 = -1;
      }
      int depth = (int) Math.min(depth1, depth2);
      
      return getColorForDepth(depth, true);
    }
  }
  
  
  public int getEdgeThickness(VertexSprite n1, VertexSprite n2) {
    if(topology == null) {
      return super.getEdgeThickness(n1, n2);
    }
    else {
      if(topology.containsKey(n1.getID())) {
        return super.getEdgeThickness(n1, n2) * 2;
      }
      else {
        return super.getEdgeThickness(n1, n2);
      }
    }
  }
}
