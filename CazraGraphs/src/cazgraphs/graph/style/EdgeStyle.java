package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.Camera;
import pwnee.GameMath;

import cazgraphs.graph.VertexSprite;

/** Defines how an edge in a graph should be drawn. */
public abstract class EdgeStyle {
  
  public String label;
  
  /** Optional reference to a camera to optimize rendering when zoomed out far. */
  public Camera camera;
  
  /** 
   * Draws the edge from n1 to n2. 
   * It is assumed that this is drawing in the GraphSprite's geometric space.
   * (Call this from VertexSprite's drawEdges method, and you should be fine.)
   */
  public void draw(Graphics2D g, VertexSprite n1, VertexSprite n2, boolean drawArrowHeads) {
    Stroke origStroke = g.getStroke();
    
    // Edge to self.
    if(n1 == n2) {
      Point2D edgeCenter = n1.getPointOnShape(135);
      double x1 = edgeCenter.getX() - 20;
      double y1 = edgeCenter.getY() - 20;
      double dia = 40;
      g.setStroke(new BasicStroke(n1.getGraph().getStyle().getEdgeThickness(n1, n2)));
      g.setColor(n1.getGraph().getStyle().getEdgeColor(n1, n2));
      
      Shape selfEdge = new Ellipse2D.Double(x1, y1, dia, dia);
      g.draw(selfEdge);
      
      // Draw the label if there is one.
      if(hasLabel()) {
        g.drawString(label, (int) (x1 + dia/2), (int) y1);
      }
    }
    
    // Edge to other
    else {
    
      // Compute the endpoints of the edge based on the nodes' shapes.
      double angle = GameMath.angleTo(n1.x, n1.y, n2.x, n2.y);
      Point2D startPt = n1.getPointOnShape(angle);
      Point2D endPt = n2.getPointOnShape(angle + 180);
      double startX = startPt.getX();
      double startY = startPt.getY();
      double endX = endPt.getX();
      double endY = endPt.getY();
      angle = GameMath.angleTo(startX, startY, endX, endY);
      
      g.setStroke(new BasicStroke(n1.getGraph().getStyle().getEdgeThickness(n1, n2)));
      g.setColor(n1.getGraph().getStyle().getEdgeColor(n1, n2));
      
      // Draw the line.
      drawLine(g, startX, startY, endX, endY);
      
      // Draw the label if there is one.
      if(hasLabel()) {
        drawLabel(g, startX, startY, endX, endY);
      }

      // Draw the arrow heads.
      if(drawArrowHeads) {
        drawArrowHead(g, endX, endY, angle);
        if(n2.hasEdge(n1.getID())) {
          drawArrowHead(g, startX, startY, angle + 180);
        }
      }
    }
    
    g.setStroke(origStroke);
  }
  
  
  /** 
   * Draws the line component of the edge, given its start and 
   * end points in graph coordinates. 
   */
  public abstract void drawLine(Graphics2D g, double startX, double startY, double endX, double endY);
  
  /** 
   * Draws an arrowhead component of the edge, given its point's position in 
   * graph coordinates and the angle of its direction.
   * (draw will pass in the correct parameters for us)
   */
  public abstract void drawArrowHead(Graphics2D g, double x, double y, double angle);
  
  /** 
   * Draws the label for this node if it has one.
   */
  public abstract void drawLabel(Graphics2D g, double startX, double startY, double endX, double endY);
  
  
  
  /** 
   * Returns true iff this edge is labeled.
   */
  public boolean hasLabel() {
    return (label != null);
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String txt) {
    label = txt;
  }
}
