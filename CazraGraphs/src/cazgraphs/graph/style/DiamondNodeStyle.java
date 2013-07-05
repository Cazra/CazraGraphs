package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.*;

import cazgraphs.graph.*;

/** A node rendering style which draws the node as an ellipse with its center at the node's origin. */
public class DiamondNodeStyle extends NodeStyle {
  
  
  public boolean containsPoint(Point2D p, GNodeSprite node) {
    try {
      // Compute the shape's metrics.
      Dimension2D dims = getDimensions(node);
      double w = dims.getWidth();
      double h = dims.getHeight();
      
      // Transform our geometric system so that we are instead testing for 
      // a collision inside a unit square.
      AffineTransform trans = new AffineTransform();
      trans.rotate(Math.PI/4); // concatenate rotate to unit square.
      trans.scale(1/w, 1/h); // concatenate scale to unit diamond.
      
      trans.concatenate(node.curTrans.createInverse());
      Point2D transP = trans.transform(p, null);
      
      double r = 1/Math.sqrt(2)/2;
      
      return (transP.getX() >= -r && transP.getX() <= r && transP.getY() >= -r && transP.getY() <= r);
    }
    catch(Exception e) {
      return false;
    }
  }
  
  
  public void draw(Graphics2D g, GNodeSprite node) {
    
    // Create the ellipse to exactly fit the rectangular shape of the label.
    Dimension2D dims = getDimensions(node);
    int w = (int) dims.getWidth();
    int h = (int) dims.getHeight();
    
    Polygon diamond = new Polygon();
    diamond.addPoint(0-w/2,0);
    diamond.addPoint(0,0-h/2);
    diamond.addPoint(w/2,0);
    diamond.addPoint(0,h/2);
    
    renderShape(g, node, diamond);
  }
  
  
  /** Returns what the dimensions of the node's shape, calculated to fit its label's dimensions. */
  public Dimension2D getDimensions(GNodeSprite node) {
    Dimension2D labelDims = node.label.getDimensions();
    double labelW = labelDims.getWidth();
    double labelH = labelDims.getHeight();
    
    double w;
    double h;
    if(labelW >= labelH) {
      double d = GameMath.dist(0, 0, labelH, labelH);
      d = Math.sqrt(d*d + d*d);
      h = d;
      w = d*labelW/labelH;
    }
    else {
      double d = GameMath.dist(0, 0, labelW, labelW);
      d = Math.sqrt(d*d + d*d);
      w = d;
      h = d*labelH/labelW;
    }
    
    w = Math.max(32,w);
    h = Math.max(32,h);
    
    return new Dimension((int) w, (int) h);
  }
  
  
  
  
  public Point2D getPointOnShape(double angle, GNodeSprite node) {
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
    if( angle <= 90) { // intersects upper-right border
      line2 = new Line2D.Double(0.5, 0, 0, -0.5);
    }
    else if(angle > 90 && angle <= 180) { // intersects upper-left border
      line2 = new Line2D.Double(0, -0.5, -0.5, 0);
    }
    else if(angle > 180 && angle <= 270) { // intersects lower-left border
      line2 = new Line2D.Double(-0.5, 0, 0, 0.5);
    }
    else { // intersects lower-right border
      line2 = new Line2D.Double(0, 0.5, 0.5, 0);
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
