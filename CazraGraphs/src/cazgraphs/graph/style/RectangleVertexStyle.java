package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.*;

import cazgraphs.graph.*;

/** A node rendering style which draws the node as an ellipse with its center at the node's origin. */
public class RectangleVertexStyle extends VertexStyle {
  
  
  public boolean containsPoint(Point2D p, VertexSprite node) {
    try {
      // Compute the shape's metrics.
      Dimension2D dims = getDimensions(node);
      double rx = dims.getWidth()/2;
      double ry = dims.getHeight()/2;
      
      Point2D transP = node.getTransform().createInverse().transform(p, null);
      
      return (transP.getX() >= 0-rx && transP.getX() <= rx && transP.getY() >= 0-ry && transP.getY() <= ry);
    }
    catch(Exception e) {
      return false;
    }
  }
  
  
  public void draw(Graphics2D g, VertexSprite node) {
    
    // Create the ellipse to exactly fit the rectangular shape of the label.
    Dimension2D dims = getDimensions(node);
    double w = dims.getWidth();
    double h = dims.getHeight();
    
    Shape shape = new Rectangle2D.Double(0- w/2, 0 - h/2, w, h);
    renderShape(g, node, shape);
  }
  
  
  /** Returns what the dimensions of the node's shape, calculated to fit its label's dimensions. */
  public Dimension2D getDimensions(VertexSprite node) {
    Dimension2D labelDims = node.getLabel().getDimensions();
    double labelW = labelDims.getWidth();
    double labelH = labelDims.getHeight();
    
    double w = Math.max(32, labelW);
    double h = Math.max(32, labelH);
    
    return new Dimension((int) w, (int) h);
  }
  
  
  
  
  public Point2D getPointOnShape(double angle, VertexSprite node) {
    Dimension2D dims = node.getDimensions();
    
    // normalize our angle within the range [0, 360).
    angle = angle % 360;
    if(angle < 0) {
      angle += 360;
    }
    if(angle == 360) {
      angle = 0;
    }
    
    // compute p on a unit square centered at 0,0 with ray at 0,0 in direction of angle.
    Line2D line1 = new Line2D.Double(0,0, GameMath.cos(angle), GameMath.sin(0-angle));
    Line2D line2;
    if(angle > 315 || angle <= 45) { // intersects right border
      line2 = new Line2D.Double(0.5, -0.5, 0.5, 0.5);
    }
    else if(angle > 45 && angle <= 135) { // intersects top border
      line2 = new Line2D.Double(0.5, -0.5, -0.5, -0.5);
    }
    else if(angle > 135 && angle <= 225) { // intersects left border
      line2 = new Line2D.Double(-0.5, 0.5, -0.5, -0.5);
    }
    else { // intersects bottom border
      line2 = new Line2D.Double(-0.5, 0.5, 0.5, 0.5);
    }
    Point2D p = GameMath.lineIntersection(line1, line2);
    
    if(p == null) {
      return new Point(0,0);
    }
    
    // scale p to match our actual rectangle.
    AffineTransform trans = new AffineTransform();
    trans.translate(node.x, node.y);
    trans.scale(dims.getWidth(), dims.getHeight());
    p = trans.transform(p, null);
    
    return p;
  }
  
  
  
  
  
}



