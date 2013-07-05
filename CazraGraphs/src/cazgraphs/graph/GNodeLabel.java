package cazgraphs.graph;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import cazgraphs.util.FontUtils;

/** An immutable label to represent an object stored in a GNodeSprite. */
public class GNodeLabel {
  
  /** The graph this label is using font metrics from. */
  public GraphSprite graph;
  
  /** The object this label represents. */
  public Object object;
  
  /** Text representation of object. */
  public String objString;
  
  /** The label's width. */
  public double width;
  
  /** The label's height. */
  public double height;
  
  
  /** 
   * Create the label from an object. We also need to pass in a graph so we can  
   * use its font metrics to compute the label's metrics.
   */
  public GNodeLabel(GraphSprite g, Object o) {
    graph = g;
    object = o;
    objString = object.toString();
    computeMetrics(graph);
  }
  
  
  
  public void computeMetrics(GraphSprite graph) {
    if(object instanceof Image) {
      Image img = (Image) object;
      
      width = img.getWidth(null);
      while(width == -1) {
        width = img.getWidth(null);
      }
      
      height = img.getHeight(null);
      while(height == -1) {
        height = img.getHeight(null);
      }
    }
    else if(graph != null) {
      Dimension2D dims = FontUtils.getStringDimensions(objString, graph.style.font, graph.style.lineSpacing);
      width = dims.getWidth();
      height = dims.getHeight();
    }
  }
  
  
  
  public Dimension2D getDimensions() {
    return new Dimension((int) width + graph.style.padding*2, (int) height + graph.style.padding*2);
  }
  
  
  
  public void draw(Graphics2D g) {
    AffineTransform origT = g.getTransform();
    g.translate(0-width/2, 0-height/2);
    
    if(object instanceof Image) {
      Image img = (Image) object;
      g.drawImage(img, 0, 0, null);
    }
    else {
      g.setFont(graph.style.font);
      g.setColor(graph.style.textColor);
      FontUtils.drawString(g, object.toString(), graph.style.lineSpacing);
    }
    
    g.setTransform(origT);
  }
  
}
