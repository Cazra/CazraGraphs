package cazgraphs.graph.layout;

import cazgraphs.graph.GraphSprite;
import cazgraphs.graph.VertexSprite;

/** 
 * A graph layout that doesn't do anything.
 */
public class DefaultGraphLayout extends GraphLayout {

  
  /** Performs one iteration through computing the layout for a graph. */
  public void stepLayout(GraphSprite graph) {
    // Does nothing.
  }
  
  public void resetPhysics(VertexSprite sprite) {
    // No special physics to set.
  }
  
  public void updatePhysics(VertexSprite sprite) {
    // No special physics to set. 
  }
  
}
