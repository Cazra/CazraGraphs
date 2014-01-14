package cazgraphs.graph.layout;

import cazgraphs.graph.GraphSprite;
import cazgraphs.graph.VertexSprite;

/** 
 * An interface for graph layout algorithms.
 */
public abstract class GraphLayout {
  
  /** Whether the layout is currently paused. */
  private boolean paused = false;
  
  
  
  /** Performs one iteration through computing the layout for a graph. */
  public abstract void stepLayout(GraphSprite graph);
  
  /** 
   * Sets the physical properties of a vertex to its default values 
   * for this algorithm. 
   */
  public abstract void resetPhysics(VertexSprite sprite);
  
  
  /** 
   * Updates the physics of a vertex according to some change in the vertex's
   * model.
   */
  public abstract void updatePhysics(VertexSprite sprite);
  
  
  /** 
   * Sets whether or not the layout becomes paused. 
   * If a layout is paused, then stepLayout should return immediately without
   * doing any layout computations.
   */
  public void setPaused(boolean paused) {
    this.paused = paused;
  }
  
  /** Returns whether the layout is currently paused or not. */
  public boolean isPaused() {
    return paused;
  }
}
