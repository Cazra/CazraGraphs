package cazgraphs.graph.layout;

/** 
 * An interface for objects representing physics properties of a vertex. 
 * Suggested implementation for this interface is that implementing classes
 * should provide static integer constants to use for the property codes of
 * its physical properties.
 * Physics properties are automatically assigned to the vertices of a graph
 * when its layout algorithm is changed.
 */
public interface LayoutPhysics {
  
  /** Returns a double-precision physical property. */
  public double getProp(int propertyCode);
  
  /** Sets a double-precision physical property. */
  public void setProp(int propertyCode, double value);
}
