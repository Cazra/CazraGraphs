package cazgraphs.graph.layout;

import java.awt.geom.*;
import java.util.*;

import pwnee.GameMath;

import cazgraphs.graph.*;

/** 
 * This layout arranges the nodes as vertices of an equidistant 
 * n-sided polygon. The nodes are spaced far enough so that edges won't 
 * cross over nodes' shapes.
 */
public class NGonGraphLayout extends GraphLayout {
  
  public void stepLayout(GraphSprite graph) {
    int numNodes = graph.nodes.size();
    
    if(isFrozen() || numNodes < 2) {
      return;
    }
    
    // sort the nodes by their object value.
    List<GNodeSprite> sorted = new ArrayList<>(graph.nodes.values());
    Collections.sort(sorted, new Comparator<GNodeSprite>() {
      public int compare(GNodeSprite o1, GNodeSprite o2) {
        return o1.object.toString().compareTo(o2.object.toString());
      }
      
      public boolean equals(Object obj) {
        return obj == this;
      }
    });
    
    // get the maximum node boundary side size
    double maxSide = -1;
    for(GNodeSprite node : sorted) {
      Dimension2D dims = node.getDimensions();
      if(dims.getWidth() > maxSide) {
        maxSide = dims.getWidth();
      }
      if(dims.getHeight() > maxSide) {
        maxSide = dims.getHeight();
      }
    }
      
    double nodeRad = GameMath.dist(0,0,maxSide/2,maxSide/2);
    double theta = Math.PI * 2 / numNodes;
    double theta2 = (Math.PI - theta)/2;
    double sideLength = nodeRad/Math.cos(theta2);
    double radius = sideLength/(2*Math.sin(theta/2));
    
    double angle = 0;
    for(GNodeSprite node : sorted) {
      node.x = radius*Math.cos(angle);
      node.y = 0-radius*Math.sin(angle);
      angle += theta;
    }
    
    setFrozen(true);
  }
  
}
