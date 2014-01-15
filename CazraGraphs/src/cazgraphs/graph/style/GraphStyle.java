package cazgraphs.graph.style;

import java.awt.*;

import pwnee.Camera;

import cazgraphs.graph.*;

/** 
 * Interface for objects describing the look and feel of a graph.
 */
public interface GraphStyle {
  
  /** Decides what VertexStyle to use to render a vertex's shape. */
  public VertexStyle getVertexStyle(VertexSprite vertex);
  
  
  /** 
   * Decides what color to use to draw the border of a vertex's shape. 
   * Override this to implement super special coloring effects. 
   */
  public Color getVertexStrokeColor(VertexSprite vertex);
  
  
  /** 
   * Decides what color to use to fill the interior of a vertex's shape.
   * Override this to implement super special coloring effects.
   */
  public Color getVertexFillColor(VertexSprite vertex);
  
  
  /** Decides what EdgeStyle to use to draw the edge between two vertices. */
  public EdgeStyle getEdgeStyle(VertexSprite v1, VertexSprite v2);
  
  
  /** Decides what color to use to draw the edge between two vertices. */
  public Color getEdgeColor(VertexSprite v1, VertexSprite v2);
  
  
  /** Decides the thickness to draw an edge with. */
  public int getEdgeThickness(VertexSprite v1, VertexSprite v2);
  
  
  /** Returns the font used for the graph's text. */
  public Font getFont();
  
  
  /** Returns the amount of padding between a vertex's label and the edge of its shape. */
  public int getLabelPadding();
  
  
  /** Returns the amount of spacing, in pixels, between lines of text in vertex labels. */
  public int getLineSpacing();
  
  
  /** Returns the color to use to display text in vertex labels. */
  public Color getTextColor();
}
