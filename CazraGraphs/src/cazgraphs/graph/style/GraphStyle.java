package cazgraphs.graph.style;

import java.awt.*;

import pwnee.Camera;

import cazgraphs.graph.*;

/** 
 * Provides colors and font metrics to be used by nodes in a GraphSprite. 
 */
public class GraphStyle {
  
  /** Optional reference to a camera. Could be used to adjust stroke thickness to zoom. */
  public Camera camera = null;
  
  /** Whether to adjust stroke thickness to the camera's zoom. */
  public boolean adjustToZoom = false;
  
  /** The font used to draw text labels for nodes in this graph. */
  public Font font = new Font("Lucida Console", java.awt.Font.PLAIN, 12);
  
  /** The amount of spacing (in pixels) between lines of text in nodes' labels. */
  public int lineSpacing = 1;
  
  /** The amount of padding between a node label's edges and its bounding box. */
  public int padding = 5;
  
  /** Default color for text node labels. */
  public Color textColor = new Color(0x000000);
  
  
  /** The default outline color for nodes in the graph. */
  public Color strokeColor = new Color(0x7777AA);
  
  /** The default interior color for nodes in the graph. */
  public Color fillColor = new Color(0xAAAAAAFF, true);
  
  /** The outline color for selected nodes.  */
  public Color selectedStrokeColor = new Color(0xFFDDAA); 
  
  /** The interior color for selected nodes. */
  public Color selectedFillColor = new Color(0xAAFFFFDD, true);
  
  /** The default color for edges. */
  public Color edgeColor = new Color(0xAA7777AA, true);
  
  
  /** The line thickness of edges in the graph. */
  public int edgeThickness = 1;
  
  
  /** 
   * Decides what color to use to draw the border of a node's shape. 
   * Override this to implement super special coloring effects. 
   */
  public Color getNodeStrokeColor(VertexSprite node) {
    if(node.isSelected()) {
      return selectedStrokeColor;
    }
    else {
      return strokeColor;
    }
  }
  
  /** 
   * Decides what color to use to fill the interior of a node's shape.
   * Override this to implement super special coloring effects.
   */
  public Color getNodeFillColor(VertexSprite node) {
    if(node.isSelected()) {
      return selectedFillColor;
    }
    else {
      return fillColor;
    }
  }
  
  
  /** Decides what color to use to draw the edge between two nodes. */
  public Color getEdgeColor(VertexSprite n1, VertexSprite n2) {
    return edgeColor;
  }
  
  
  /** Decides the thickness to draw an edge with. */
  public int getEdgeThickness(VertexSprite n1, VertexSprite n2) {
    if(adjustToZoom && camera != null) {
      return (int) (edgeThickness/camera.zoom); 
    }
    return edgeThickness;
  }
  
}
