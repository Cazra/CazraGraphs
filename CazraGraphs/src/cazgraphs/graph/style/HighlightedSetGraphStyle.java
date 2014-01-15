package cazgraphs.graph.style;

import java.awt.*;
import java.util.Set;

import pwnee.Camera;

import cazgraphs.graph.*;


/** 
 * A graph style that highlights a given set of vertices. 
 */
public class HighlightedSetGraphStyle extends DefaultGraphStyle {
  
  public Color highlightFill = new Color(0xFFAAAA);
  
  public Color highlightStroke = new Color(0x884444);
  
  private Set<String> highlighted;
  
  
  
  public HighlightedSetGraphStyle(Set<String> highlighted) {
    this.highlighted = highlighted;
  }
  
  
  @Override
  public Color getVertexStrokeColor(VertexSprite node) {
    if(node.isSelected()) {
      return selectedStrokeColor;
    }
    else if(highlighted.contains(node.getID())) {
      return highlightStroke;
    }
    else {
      return strokeColor;
    }
  }
  
  
  @Override
  public Color getVertexFillColor(VertexSprite node) {
    if(node.isSelected()) {
      return selectedFillColor;
    }
    else if(highlighted.contains(node.getID())) {
      return highlightFill;
    }
    else {
      return fillColor;
    }
  }
}