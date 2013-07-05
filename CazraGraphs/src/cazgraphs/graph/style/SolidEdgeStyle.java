package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.Camera;
import pwnee.GameMath;

import cazgraphs.graph.*;
import cazgraphs.util.FontUtils;


/** Draws the edge as a solid line with an arrow head at node n2's end. */
public class SolidEdgeStyle extends EdgeStyle {

  /** Whether to align the label with the angle of the line. */
  public boolean aligned;
  
  /** Make the edge with a label. */
  public SolidEdgeStyle(String txt, boolean align) {
    setLabel(txt);
    aligned = align;
  }
  
  public SolidEdgeStyle(String txt) {
    this(txt, true);
  }
  
  /** Makes the edge without a label. */
  public SolidEdgeStyle() {
    this(null);
  }
  

  public void drawLine(Graphics2D g, double startX, double startY, double endX, double endY) {
    Line2D line = new Line2D.Double(startX, startY, endX, endY);
    g.draw(line);
  }
  
  
  public void drawArrowHead(Graphics2D g, double x, double y, double angle) {
    // skip drawing if too small due to camera zoom.
    if(camera != null && camera.zoom  < 2.0/10) {
      return;
    }
    
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
    // skip drawing if too small due to camera zoom.
    if(camera != null && camera.zoom  < 1.0/10) {
      return;
    }
    
    AffineTransform origT = g.getTransform();
    
    double angleTo = GameMath.angleTo(startX, startY, endX, endY);
    int dist = (int) GameMath.dist(startX, startY, endX, endY);
    
    Dimension2D stringDims = FontUtils.getStringDimensions(label, g.getFont(), 1);
    int yOffset = (int) stringDims.getHeight();
    
    g.translate(startX, startY);
    if(aligned) {
      g.rotate(GameMath.d2r(0-angleTo));
      g.translate((dist - stringDims.getWidth())/2, -1 - yOffset);
    }
    else {
      g.translate(dist/2 * GameMath.cos(angleTo), -dist/2 * GameMath.sin(angleTo)+ 20 - yOffset);
    }
    g.drawString(label, 0, 0);
    
    g.setTransform(origT);
  }
}
