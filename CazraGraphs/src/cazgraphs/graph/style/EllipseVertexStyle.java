package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.*;

import cazgraphs.graph.*;

/** A node rendering style which draws the node as an ellipse with its center at the node's origin. */
public class EllipseVertexStyle extends VertexStyle {
  
  
  public boolean containsPoint(Point2D p, VertexSprite node) {
    try {
      // Compute the ellipse's metrics.
      Dimension2D dims = getDimensions(node);
      double rx = dims.getWidth()/2;
      double ry = dims.getHeight()/2;
      
      // apply a scale to compress our geometry so that the ellipse is a unit circle.
      AffineTransform scale = AffineTransform.getScaleInstance(1/rx, 1/ry);
      scale.concatenate(node.getTransform().createInverse());
      Point2D transP = scale.transform(p, null);
      
    //  System.out.println(node + " " + transP);
      
      // if our transformed point is in the unit circle, then it is inside the ellipse.
      return (GameMath.dist(0, 0, transP.getX(), transP.getY()) <= 1);
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
    
    Ellipse2D e = new Ellipse2D.Double(0- w/2, 0 - h/2, w, h);
    renderShape(g, node, e);
  }
  
  
  /** Returns what the dimensions of the node's shape, calculated to fit its label's dimensions. */
  public Dimension2D getDimensions(VertexSprite node) {
    Dimension2D labelDims = node.getLabel().getDimensions();
    double labelW = labelDims.getWidth();
    double labelH = labelDims.getHeight();
    
    double w;
    double h;
    if(labelW >= labelH) {
      double d = GameMath.dist(0, 0, labelH, labelH);
      h = d;
      w = d*labelW/labelH;
    }
    else {
      double d = GameMath.dist(0, 0, labelW, labelW);
      w = d;
      h = d*labelH/labelW;
    }
    
    w = Math.max(32,w);
    h = Math.max(32,h);
    
    return new Dimension((int) w, (int) h);
  }
  
  
  public Point2D getPointOnShape(double angle, VertexSprite node) {
    Point2D p = new Point2D.Double(1,0);
    Dimension2D dims = node.getDimensions();
    
    AffineTransform trans = new AffineTransform();
    trans.translate(node.x, node.y);
    trans.scale(dims.getWidth()/2, dims.getHeight()/2);
    trans.rotate(0-GameMath.d2r(angle));
    p = trans.transform(p, null);
    
    return p;
  }
}
