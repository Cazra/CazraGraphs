package cazgraphs.graph.style;

import java.awt.*;
import java.util.List;
import java.util.Set;

import pwnee.Camera;

import cazgraphs.graph.*;

/** 
 * 2-colors a graph. Vertices that aren't 2-colorable are painted a third odd color.
 */
public class CyclicTreeGraphStyle extends DefaultGraphStyle {
  
  public Color cycleStroke = new Color(0xAA55AA);
  public Color cycleFill = new Color(0xFFAAFF);
  
  
  public CyclicTreeGraphStyle() {
    super();
  }
  

  public Color getVertexStrokeColor(VertexSprite node) {
    if(node.isSelected()) {
      return selectedStrokeColor;
    }
    else if(node.getID().startsWith("ref;")) {
      return cycleStroke;
    }
    else {
      return strokeColor;
    }
  }
  

  public Color getVertexFillColor(VertexSprite node) {
    if(node.isSelected()) {
      return selectedFillColor;
    }
    else if(node.getID().startsWith("ref;")) {
      return cycleFill;
    }
    else {
      return fillColor;
    }
  }
  
}
