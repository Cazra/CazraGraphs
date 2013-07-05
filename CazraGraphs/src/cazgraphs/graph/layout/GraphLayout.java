package cazgraphs.graph.layout;

import cazgraphs.graph.GraphSprite;

/** 
 * An interface for graph layout algorithms.
 */
public abstract class GraphLayout {
  
  /** Whether the layout is currently frozen. */
  private boolean frozen = false;
  
  
  
  /** Performs one iteration through computing the layout for a graph. */
  public abstract void stepLayout(GraphSprite graph);
  
  /** 
   * Sets whether or not the layout becomes frozen. 
   * If a layout is frozen, then stepLayout should return immediately without
   * doing any layout computations.
   */
  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }
  
  /** Returns whether the layout is currently frozen or not. */
  public boolean isFrozen() {
    return frozen;
  }
}
