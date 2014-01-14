package cazgraphs.graph.style;

import java.awt.*;
import java.util.Map;

import cazgraphs.graph.*;

/**
 * A graph style that colors nodes based on whether they are an ancestor of a 
 * node, one of its descendants, or neither. 
 */
public class AncestryGraphStyle extends GraphStyle {
  
  public static int NOT_RELATED = 0;
  
  public static int DESCENDANT = 1;
  
  public static int ANCESTOR = 2;
  
  public static int BOTH = 3;
  
  
  
  /** The descendant topology for the graph. */
  public Map<String, Integer> descendants = null;
  
  /** The ancestral topology for the graph. */
  public Map<String, Integer> ancestors = null;
  
  /** The maximum depth of the descendant topology. */
  public int maxDesDepth = -1;
  
  /** The maximum depth of the ancestral topology. */
  public int maxAncDepth = -1;
  
  public AncestryGraphStyle() {
    super();
  }
  
  
  /** 
   * Selects a color from the HSB color model to represent a particular depth. 
   * Colors towards the red end of the spectrum are shallow.
   * Colors towards the blue end of the spectrum are deep.
   */
  protected Color getColorForDepth(int depth, int maxDepth, boolean isStroke, int ancestryHint) {
    if(depth < 0 || depth > maxDepth) {
      return new Color(0xEEEEEE);
    }
    
    float hue = 0.2f;
    if(ancestryHint == ANCESTOR) {
      hue = 0.0f;
    }
    if(ancestryHint == DESCENDANT) {
      hue = 0.6f;
    }
    if(ancestryHint == BOTH) {
      hue = 0.75f;
    }
    float sat = 0f;
    if(depth != 0) {
      sat = 0.1f + depth*0.7f/maxDepth;
    }
    else {
      sat = 0;
    }
    float bright = 1.0f;
    if(isStroke) {
      bright = 0.7f;
    }
    
    return Color.getHSBColor(hue, sat, bright);
  }
  
  
  /** Sets the topological information for the style, given the top node. */
  public void setAncestry(VertexSprite node) {
    if(node == null) {
      descendants = null;
      ancestors = null;
      return;
    }
    
    descendants = GraphSolver.simpleTopology(node.getGraph().getGraph(), node.getID());
    maxDesDepth = -1;
    for(Integer depth : descendants.values()) {
      if(depth > maxDesDepth) {
        maxDesDepth = depth;
      }
    }
    
    ancestors = GraphSolver.simpleReverseTopology(node.getGraph().getGraph(), node.getID());
    maxAncDepth = -1;
    for(Integer depth : ancestors.values()) {
      if(depth > maxAncDepth) {
        maxAncDepth = depth;
      }
    }
  }
  
  
  
  public int getAncestryHint(String vertexID) {
    if(descendants.containsKey(vertexID)) {
      if(ancestors.containsKey(vertexID)) {
        return BOTH;
      }
      return DESCENDANT;
    }
    else if(ancestors.containsKey(vertexID)) {
      return ANCESTOR;
    }
    return NOT_RELATED;
  }
  
  
  
  public Color getNodeStrokeColor(VertexSprite node) {
    if(descendants == null || node.isSelected()) {
      return super.getNodeFillColor(node);
    }
    else {
      int ancestry = getAncestryHint(node.getID());
      if(ancestry == DESCENDANT) {
        Integer depth = descendants.get(node.getID());
        return getColorForDepth(depth, maxDesDepth, true, ancestry);
      }
      else if(ancestry == ANCESTOR) {
        Integer depth = ancestors.get(node.getID());
        return getColorForDepth(depth, maxAncDepth, true, ancestry);
      }
      else if(ancestry == BOTH) {
        Integer depth = descendants.get(node.getID());
        return getColorForDepth(depth, Math.max(maxDesDepth, maxAncDepth), true, ancestry);
      }
      else {
        return getColorForDepth(-1, maxDesDepth, true, ancestry);
      }
    }
  }
  
  
  public Color getNodeFillColor(VertexSprite node) {
    if(descendants == null || node.isSelected()) {
      return super.getNodeFillColor(node);
    }
    else {
      int ancestry = getAncestryHint(node.getID());
      if(ancestry == DESCENDANT) {
        Integer depth = descendants.get(node.getID());
        return getColorForDepth(depth, maxDesDepth, node.isSink(), ancestry);
      }
      else if(ancestry == ANCESTOR) {
        Integer depth = ancestors.get(node.getID());
        return getColorForDepth(depth, maxAncDepth, node.isSource(), ancestry);
      }
      else if(ancestry == BOTH) {
        Integer depth = descendants.get(node.getID());
        return getColorForDepth(depth, Math.max(maxDesDepth, maxAncDepth), false, ancestry);
      }
      else {
        return getColorForDepth(-1, maxDesDepth, false, ancestry);
      }
    }
  }
  
  
  public Color getEdgeColor(VertexSprite n1, VertexSprite n2) {
    if(descendants == null || getAncestryHint(n1.getID()) == NOT_RELATED || getAncestryHint(n2.getID()) == NOT_RELATED) {
      return getColorForDepth(-1, maxDesDepth, true, NOT_RELATED);
    }
    else {
      int ancestry = getAncestryHint(n1.getID());
      if(ancestry == DESCENDANT) {
        Integer depth = descendants.get(n1.getID());
        return getColorForDepth(depth, maxDesDepth, true, ancestry);
      }
      else if(ancestry == ANCESTOR) {
        Integer depth = ancestors.get(n1.getID());
        return getColorForDepth(depth, maxAncDepth, true, ancestry);
      }
      else if(ancestry == BOTH) {
        Integer depth = descendants.get(n1.getID());
        return getColorForDepth(depth, Math.max(maxDesDepth, maxAncDepth), true, ancestry);
      }
      else {
        return getColorForDepth(-1, maxDesDepth, true, ancestry);
      }
    }
  }
  
  
  public int getEdgeThickness(VertexSprite n1, VertexSprite n2) {
    if(descendants == null || getAncestryHint(n1.getID()) == NOT_RELATED || getAncestryHint(n2.getID()) == NOT_RELATED) {
      return super.getEdgeThickness(n1, n2);
    }
    else {
        return super.getEdgeThickness(n1, n2) * 2;
    }
  }


}
