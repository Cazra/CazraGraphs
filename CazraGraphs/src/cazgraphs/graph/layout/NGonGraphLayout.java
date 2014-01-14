package cazgraphs.graph.layout;

import java.awt.geom.*;
import java.util.*;

import pwnee.GameMath;

import cazgraphs.graph.*;

/** 
 * This layout arranges the vertices as vertices of an equidistant 
 * n-sided polygon. The vertices are spaced far enough so that edges won't 
 * cross over vertices' shapes.
 */
public class NGonGraphLayout extends GraphLayout {
  
  
  public void resetPhysics(VertexSprite sprite) {
    // No special physics to set.
  }
  
  public void updatePhysics(VertexSprite sprite) {
    // No special physics to set. 
  }
  
  
  public void stepLayout(GraphSprite graph) {
    int numNodes = graph.size();
    
    if(isPaused() || numNodes < 2) {
      return;
    }
    
    // sort the vertices by their object's toString value.
    List<VertexSprite> sorted = new ArrayList<>(graph.getSprites());
    Collections.sort(sorted, new Comparator<VertexSprite>() {
      public int compare(VertexSprite o1, VertexSprite o2) {
        return o1.getObject().toString().compareTo(o2.getObject().toString());
      }
      
      public boolean equals(Object obj) {
        return obj == this;
      }
    });
    
    // get the maximum vertex boundary side size
    double maxSide = -1;
    for(VertexSprite vertex : sorted) {
      Dimension2D dims = vertex.getDimensions();
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
    for(VertexSprite vertex : sorted) {
      vertex.x = radius*Math.cos(angle);
      vertex.y = 0-radius*Math.sin(angle);
      angle += theta;
    }
    
    setPaused(true);
  }
  
}
