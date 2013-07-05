package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.Camera;
import pwnee.GameMath;

import cazgraphs.graph.*;
import cazgraphs.util.FontUtils;


/** Draws the edge as a solid line with an arrow head at node n2's end. */
public class DashedEdgeStyle extends EdgeStyle {

  /** Whether to align the label with the angle of the line. */
  public boolean aligned;
  
  
  /** The length of line segments in the dashed edge. */
  public double segmentLength;
  
  /** The legnth of gaps between line segments in the dashed edge. */
  public double gapLength;
  
  
  
  public DashedEdgeStyle(String txt, boolean align, double sl, double gl) {
    segmentLength = sl;
    gapLength = gl;
    
    setLabel(txt);
    aligned = align;
  }
  
  public DashedEdgeStyle(String txt, double sl, double gl) {
    this(txt, true, sl, gl);
  }
  
  public DashedEdgeStyle(double sl, double gl) {
    this(null, sl, gl);
  }
  
  public DashedEdgeStyle() {
    this(16,10);
  }
  
  public DashedEdgeStyle(String txt, boolean align) {
    this(txt, align, 16, 10);
  }
  
  public DashedEdgeStyle(String txt) {
    this(txt, true);
  }
  

  
  public void drawLine(Graphics2D g, double startX, double startY, double endX, double endY) {
    double angle = GameMath.angleTo(startX, startY, endX, endY);
    
    double segDX = segmentLength*GameMath.cos(angle);
    double segDY = 0-segmentLength*GameMath.sin(angle);
    double gapDX = gapLength*GameMath.cos(angle);
    double gapDY = 0-gapLength*GameMath.sin(angle);
    
    double x = startX;
    double y = startY;
    boolean done = false;
    while(!done) {
      double prevX = x;
      double prevY = y;
      x+= segDX;
      y+= segDY;
      
      if(Math.abs(x - startX) >= Math.abs(endX - startX) || Math.abs(y - startY) >= Math.abs(endY - startY)) {
        done = true;
        x = prevX;
        y = prevY;
      }
      else {
        Line2D line = new Line2D.Double(prevX, prevY, x, y);
        g.draw(line);
        
        x += gapDX;
        y += gapDY;
      }
    }
  }
  
  
  
  public void drawArrowHead(Graphics2D g, double x, double y, double angle) {
    double backAngle1 = angle + 160;
    double backAngle2 = angle - 160;
    double length = 16;
    
    double endX, endY;
    Line2D line;
    
    endX = x + length*GameMath.cos(backAngle1);
    endY = y - length*GameMath.sin(backAngle1);
    line = new Line2D.Double(x, y, endX, endY);
    g.draw(line);
    
    endX = x + length*GameMath.cos(backAngle2);
    endY = y - length*GameMath.sin(backAngle2);
    line = new Line2D.Double(x, y, endX, endY);
    g.draw(line);
  }
  
  
  public void drawLabel(Graphics2D g, double startX, double startY, double endX, double endY) {
    AffineTransform origT = g.getTransform();
    
    double angleTo = GameMath.angleTo(startX, startY, endX, endY);
    int dist = (int) GameMath.dist(startX, startY, endX, endY);
    
    Dimension2D stringDims = FontUtils.getStringDimensions(label, g.getFont(), 1);
    int yOffset = (int) stringDims.getHeight();
    
    g.translate(startX, startY);
    if(aligned) {
      g.rotate(GameMath.d2r(0-angleTo));
      g.translate((dist - stringDims.getWidth())/2, -1);
    }
    else {
      g.translate(dist/2 * GameMath.cos(angleTo), -dist/2 * GameMath.sin(angleTo)+20);
    }
    g.drawString(label, 0, 0 - yOffset);
    
    g.setTransform(origT);
  }
}
