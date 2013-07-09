package cazgraphs.graph.style;

import java.awt.*;
import java.util.List;
import java.util.Set;

import pwnee.Camera;

import cazgraphs.graph.*;

/** 
 * 2-colors a graph. Vertices that aren't 2-colorable are painted a third odd color.
 */
public class BipartiteGraphStyle extends GraphStyle {
  
  public Set<GNodeSprite> set1;
  
  public Set<GNodeSprite> set2;
  
  
  public Color set1Stroke = new Color(0xAA5555);
  public Color set1Fill = new Color(0xFFAAAA);
  
  public Color set2Stroke = new Color(0x55AA55);
  public Color set2Fill = new Color(0xAAFFAA);
  
  
  public BipartiteGraphStyle() {
    super();
  }
  
  
  
  public void computeBipartiteness(GraphSprite graph, String startNodeID) {
    List<Set<GNodeSprite>> bipartite = GraphSolver.bicolorGraph(graph, startNodeID);
    set1 = bipartite.get(0);
    set2 = bipartite.get(1);
  }
  
  
  
  
  /** 
   * Decides what color to use to draw the border of a node's shape. 
   * Override this to implement super special coloring effects. 
   */
  public Color getNodeStrokeColor(GNodeSprite node) {
    if(node.isSelected) {
      return selectedStrokeColor;
    }
    else if(set1.contains(node)) {
      return set1Stroke;
    }
    else if(set2.contains(node)) {
      return set2Stroke;
    }
    else {
      return strokeColor;
    }
  }
  
  /** 
   * Decides what color to use to fill the interior of a node's shape.
   * Override this to implement super special coloring effects.
   */
  public Color getNodeFillColor(GNodeSprite node) {
    if(node.isSelected) {
      return selectedFillColor;
    }
    else if(set1.contains(node)) {
      return set1Fill;
    }
    else if(set2.contains(node)) {
      return set2Fill;
    }
    else {
      return fillColor;
    }
  }
  
}
