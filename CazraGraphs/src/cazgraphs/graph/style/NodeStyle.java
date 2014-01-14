package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.*;

import cazgraphs.graph.*;

/** Defines how the non-label part of a node should be drawn. */
public abstract class NodeStyle {
  
  /** 
   * Determines whether the shape rendered by this style for a node contains 
   * the given point, in view coordinates. 
   * @param p             The point we're checking, in view coordinates.
   * @param node          The node using this style.
   * @return              True, iff p is contained in the shape drawn by this 
   *                      style to fit its node's label.
   */
  public abstract boolean containsPoint(Point2D p, VertexSprite node);
  
  /** 
   * Renders the style to fit the dimensions of a node's label 
   * (the visualization of its contained object, usually just a String). 
   * @param g             The graphics context, passed in by the node's draw method.
   * @param node          The node using this style.
   */
  public abstract void draw(Graphics2D g, VertexSprite node);
  
  
  protected void renderShape(Graphics2D g, VertexSprite node, Shape shape) {
    GraphStyle gStyle = node.getGraph().getStyle();
    
    // fill the interior
    g.setColor(gStyle.getNodeFillColor(node));
    g.fill(shape);
    
    // draw the outline
    Stroke origStroke = g.getStroke();
    g.setStroke(new BasicStroke(gStyle.getEdgeThickness(node, node)));
    
    g.setColor(gStyle.getNodeStrokeColor(node));
    g.draw(shape);
    g.setStroke(origStroke);
  }
  
  
  /** 
   * Computes the dimensions of the box fitting the shape drawn by this style 
   * to fit the dimensions of a node's label.
   * @param node          The node using this style.
   * @return              The dimensions of the bounding box for this style's shape.
   */
  public abstract Dimension2D getDimensions(VertexSprite node);
  
  /** 
   * Returns the outermost point on the boundary of the shape drawn by this
   * node style in a specified direction (angle in degrees) from the shape's center.
   */
  public abstract Point2D getPointOnShape(double angle, VertexSprite node);
}
