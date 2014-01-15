package cazgraphs.graph.style;

import java.awt.*;

import pwnee.Camera;

import cazgraphs.graph.*;

/** 
 * Provides colors and font metrics to be used by vertices in a GraphSprite. 
 */
public class DefaultGraphStyle implements GraphStyle {
  
  /** Optional reference to a camera. Could be used to adjust stroke thickness to zoom. */
  private Camera camera = null;
  
  /** Whether to adjust stroke thickness to the camera's zoom. */
  private boolean adjustToZoom = false;
  
  /** The font used to draw text labels for vertices in this graph. */
  public Font font = new Font("Lucida Console", java.awt.Font.PLAIN, 12);
  
  /** Default color for text vertex labels. */
  public Color textColor = new Color(0x000000);
  
  /** Default vertex style. */
  public VertexStyle vertexStyle = new EllipseVertexStyle();
  
  /** The default outline color for vertices in the graph. */
  public Color strokeColor = new Color(0x7777AA);
  
  /** The default interior color for vertices in the graph. */
  public Color fillColor = new Color(0xAAAAAAFF, true);
  
  /** The outline color for selected vertices.  */
  public Color selectedStrokeColor = new Color(0xFFDDAA); 
  
  /** The interior color for selected vertices. */
  public Color selectedFillColor = new Color(0xAAFFFFDD, true);
  
  /** The default edge style. */
  public EdgeStyle edgeStyle = new SolidEdgeStyle(); 
  
  /** Default edge style for indirect edges (Like those resulting from vertices being collapsed). */
  public EdgeStyle indirectEdgeStyle = new DashedEdgeStyle();
  
  /** The default color for edges. */
  public Color edgeColor = new Color(0xAA7777AA, true);
  
  
  /** Decides what VertexStyle to use to render a vertex's shape. */
  @Override
  public VertexStyle getVertexStyle(VertexSprite vertex) {
    return vertexStyle;
  }
  
  
  /** 
   * Decides what color to use to draw the border of a vertex's shape. 
   * Override this to implement super special coloring effects. 
   */
  @Override
  public Color getVertexStrokeColor(VertexSprite vertex) {
    if(vertex.isSelected()) {
      return selectedStrokeColor;
    }
    else {
      return strokeColor;
    }
  }
  
  /** 
   * Decides what color to use to fill the interior of a vertex's shape.
   * Override this to implement super special coloring effects.
   */
  @Override
  public Color getVertexFillColor(VertexSprite vertex) {
    if(vertex.isSelected()) {
      return selectedFillColor;
    }
    else {
      return fillColor;
    }
  }
  
  
  
  /** Decides what EdgeStyle to use to draw the edge between two vertices. */
  @Override
  public EdgeStyle getEdgeStyle(VertexSprite v1, VertexSprite v2) {
    if(v1.hasEdge(v2.getID())) {
      return edgeStyle;
    }
    else {
      return indirectEdgeStyle;
    }
  }
  
  
  /** Decides what color to use to draw the edge between two vertices. */
  @Override
  public Color getEdgeColor(VertexSprite v1, VertexSprite v2) {
    return edgeColor;
  }
  
  
  /** Decides the thickness to draw an edge with. */
  @Override
  public int getEdgeThickness(VertexSprite v1, VertexSprite v2) {
    int thickness = 1;
    if(adjustToZoom && camera != null) {
      return (int) (thickness/camera.zoom); 
    }
    return thickness;
  }
  
  
  @Override
  public Font getFont() {
    return font;
  }
  
  
  @Override
  public int getLabelPadding() {
    return 5;
  }
  
  
  @Override
  public int getLineSpacing() {
    return 1;
  }
  
  
  @Override
  public Color getTextColor() {
    return textColor;
  }
}
