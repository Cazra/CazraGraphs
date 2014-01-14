package cazgraphs.graph;

import java.awt.*;
import java.awt.geom.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pwnee.*;
import pwnee.sprites.Sprite;
import pwnee.text.Tooltipable;

import cazgraphs.CazgraphException;
import cazgraphs.graph.layout.LayoutPhysics;
import cazgraphs.graph.style.*;
import cazgraphs.util.FontUtils;

/** 
 * A sprite for representing a vertex in a GraphSprite.
 * Each vertex has a unique String ID (unique in its graph) 
 */
public class VertexSprite extends Sprite implements Comparable<VertexSprite>, Tooltipable {
  
  /** 
   * A unique ID for this node in its graph. This is set in the constructor 
   * and should never change... EVER!!!
   */
  private String id = "";
  
  /** The label for displaying object in this node. */
  private VertexLabel label;
  
  /** The graph containing this node. */
  private GraphSprite graph;
  
  /** The labels for this vertex's forward edges. */
  private Map<String, String> edgeLabels = new HashMap<>();
  
  
  /** The physical properties associated with this vertex for computing the graph layout. */
  private LayoutPhysics physics = null;
  
  
  /** Whether this node is currently selected. */
  private boolean isSelected = false;
  
  /** Parent vertex this vertex is nested under, for expandable graphs. */
  private VertexSprite parent = null;
  
  /** Set of nested vertex sprites, for expandable graphs. */
  private Set<VertexSprite> children = new HashSet<>();
  
  /** Whether this sprite is expanded to reveal nested sprites. */
  private boolean isExpanded = true;
  
  /**
   * Creates a vertex sprite at the origin repesenting the vertex in the graph 
   * with the specified ID.
   */
  public VertexSprite(GraphSprite g, String id) {
    super(0,0);
    this.graph = g;
    this.id = id;
    this.label = new VertexLabel(g, g.getObject(id));
  }
  
  
  /** 
   * When a node is destroyed, clean up by disconnecting it 
   * from any existing edges.
   */
  public void destroy() {
    super.destroy();
    graph.removeAllEdges(this.id);
  }
  
  
  
  //////// ID
  
  
  /** Returns the ID of this vertex. */
  public String getID() {
    return id;
  }
  
  
  //////// Stored object
  
  /** Returns the object stored at this node. */
  public Object getObject() {
    return graph.getObject(this.id);
  }
  
  
  
  /** Sets the object stored in this node and uses it as a label for the node. */
  public void setObject(Object obj) {
    graph.setObject(this.id, obj);
    this.label = new VertexLabel(graph, obj);
  }
  
  
  //////// Label
  
  /** Returns the label graphic used for this node. */
  public VertexLabel getLabel() {
    return label;
  }
  
  //////// Model
  
  /** Returns the graph containing this vertex. */
  public GraphSprite getGraph() {
    return graph;
  }
  
  
  /** Returns the union of this vertex's forward and backward edges. */
  public Set<String> getNeighbors() {
    return graph.getNeighbors(this.id);
  }
  
  
  /** Returns the set of this node's forward edges. */
  public Set<String> getEdges() {
    return graph.getEdges(this.id);
  }
  
  /** Returns the set of this node's backward edges. */
  public Set<String> getBackwardEdges() {
    return graph.getBackwardEdges(this.id);
  }
  
  
  /** 
   * Returns true iff the graph contains an edge from this node to the
   * the specified node.
   */
  public boolean hasEdge(String otherID) {
    return graph.hasEdge(this.id, otherID);
  }
  

  /**
   * Creates an edge from this vertex to another vertex.
   * @param otherID     The ID of the vertex we're making an edge for.
   * @return      This, for chaining.
   */
  public VertexSprite addEdge(String otherID) {
    graph.addEdge(this.id, otherID);
    return this;
  }
  
  /**
   * Creates a labeled edge from this vertex to another vertex.
   * @param otherID     The ID of the vertex we're making an edge for.
   * @param label       The label for the edge.
   * @return      This, for chaining.
   */
  public VertexSprite addEdge(String otherID, String label) {
    graph.addEdge(this.id, otherID, label);
    return this;
  }
  
  
  /** 
   * Returns the text label for the edge from this vertex to the one with the 
   * specified ID. 
   */
  public String getEdgeLabel(String otherID) {
    return edgeLabels.get(otherID);
  }
  
  
  /** 
   * Sets the text label for an edge from this node to the node with the 
   * specified ID.
   */
  public void setEdgeLabel(String otherID, String label) {
    if(otherID == null) {
      throw new CazgraphException("Cannot label null edge: " + otherID);
    }
    
    if(graph.hasEdge(this.id, otherID)) {
      edgeLabels.put(otherID, label);
    }
  }
  
  
  /** 
   * Removes a node from this node's edges.
   * @param otherID   The id of neighbor we're removing the edge to.
   * @return        This, for chaining.
   */
  public VertexSprite removeEdge(String otherID) {
    graph.removeEdge(this.id, otherID);
    return this;
  }
  
  
  /**
   * Removes all edges from this node.
   * @return    This, for chaining.
   */
  public VertexSprite removeAllEdges() {
    graph.removeAllEdges(this.id);
    return this;
  }
  
  
  
  /** Nodes are compared by their IDs. */
  public int compareTo(VertexSprite other) {
    return this.id.compareTo(other.id);
  }
  
  
  /** Return the in-degree of this node - the number of edges going in to this node */
  public int inDegree() {
    return getBackwardEdges().size();
  }
  
  /** Return the out-degree of this node - the number of edges going out from this node */
  public int outDegree() {
    return getEdges().size();
  }
  
  
  /** Returns true iff this node has an in-degree of 0. */
  public boolean isSource() {
    return (inDegree() == 0);
  }
  
  /** Returns true iff this node has an out-degree of 0. */
  public boolean isSink() {
    return (outDegree() == 0);
  }
  
  
  
  //////// Nested sprites
  
  /** Returns true iff this sprite and its ancestors are expanded. */
  public boolean isExpanded() {
    return isExpanded && (parent == null || parent.isExpanded());
  }
  
  /** Set whether this sprite is expanded. */
  public void setExpanded(boolean isExpanded) {
    this.isExpanded = isExpanded;
  }
  
  /** 
   * Gets the sprite this one is nested under. Returns null if this sprite is 
   * at the top of the nesting chain. 
   */
  public VertexSprite getParent() {
    return parent;
  }
  
  /** Nests a sprite below this one. */
  public void addChild(VertexSprite v) {
    if(v.parent != null) {
      v.parent.removeChild(v);
    }
    
    v.parent = this;
    this.children.add(v);
  }
  
  /** Nests a set of sprites below this one. */
  public void addChildren(Collection<VertexSprite> set) {
    for(VertexSprite v : set) {
      addChild(v);
    }
  }
  
  /** Unnests a sprite below this one. */
  public void removeChild(VertexSprite v) {
    if(children.contains(v)) {
      v.parent = null;
      this.children.remove(v);
    }
  }
  
  /** Unnests a set of sprites below this one. */
  public void removeChildren(Collection<VertexSprite> set) {
    for(VertexSprite v : set) {
      removeChild(v);
    }
  }
  
  /** Unnests all sprites below this one. */
  public void clearChildren() {
    for(VertexSprite v : children) {
      v.parent = null;
    }
    children.clear();
  }
  
  /** 
   * Returns the first visible vertex at or above this vertex in the  
   * nesting chain. Returns null if one can't be found.
   */
  public VertexSprite getFirstVisible() {
    VertexSprite v = this;
    while(v != null) {
      if(v.isVisible()) {
        break;
      }
      
      v = v.parent;
    }
    return v;
  }
  
  
  //////// Geometry
  
  /** Returns the node's x position. (Used by Tooltipable) */
  public double getX() {
    return x;
  }
  
  /** Returns the node's y position. (Used by Tooltipable) */
  public double getY() {
    return y;
  }
  
  
  /** Return the rectangle that bounds the node's style shape. Returns null if this node is not visible. */
  public Rectangle2D getCollisionBox() {
    if(!isVisible()) {
      return null;
    }
    Dimension2D dims = getDimensions();
    return new Rectangle2D.Double(x-dims.getWidth()/2, y-dims.getHeight()/2, dims.getWidth(), dims.getHeight());
  }
  
  
  /** Returns the dimensions of the bounding box for the shape drawn by this node's style. */
  public Dimension2D getDimensions() {
    if(!isVisible()) {
      return new Dimension(0,0);
    }
    return getStyle().getDimensions(this);
  }
  
  
  /** 
   * Checks if this node's shape (Determined by its style) contains a point, 
   * given in view coordinates.
   */
  public boolean containsPoint(Point2D p) {
    if(!isVisible()) {
      return false;
    }
    return getStyle().containsPoint(p, this);
  }
  
  
  /** 
   * Returns the point on the boundary of this node's shape in a specified 
   * direction (angle in degrees) from the node's center. 
   */
  public Point2D getPointOnShape(double angle) {
    if(!isVisible()) {
      return new Point2D.Double(x,y);
    }
    return getStyle().getPointOnShape(angle, this);
  }
  
  
  //////// Layout physics
  
  /** Sets the layout physics object for this vertex. */
  public void setPhysics(LayoutPhysics physics) {
    this.physics = physics;
  }
  
  /** Returns the value of some physics property of this vertex. */
  public double getPhysicsProp(int code) {
    return physics.getProp(code);
  }
  
  /** Sets the value of some physics property of this vertex. */
  public void setPhysicsProp(int code, double value) {
    physics.setProp(code, value);
  }
  
  
  
  //////// Selection
  
  /** Returns true iff this node is currently selected. */
  public boolean isSelected() {
    return isSelected;
  }
  
  /** Sets whether this node is currently selected. */
  public void setSelected(boolean selected) {
    isSelected = selected;
  }
  
  
  //////// Rendering
  
  public boolean isVisible() {
    return isVisible && (parent == null || parent.isExpanded());
  }
  
  
  /** Draws the node, but not the edges. */
  public void draw(Graphics2D g) {
    if(!isVisible()) {
      return;
    }
    
    g.setColor(new Color(0xAA0000));
    getStyle().draw(g, this);
    
    Camera camera = graph.getStyle().camera;
    if(camera != null && camera.zoom <= 1.0/12) {
      return;
    }
    label.draw(g);
  }
  
  
  /** 
   * Draws the outward edges of this node. This is assumed to be 
   * rendering with the graph's transform, rather than this node's
   * transform.
   */
  public void drawEdges(Graphics2D g, Set<String> drawnEdges) {
    if(!isVisible()) {
      VertexSprite visibleAncestor = getFirstVisible();
      if(visibleAncestor != null) {
        
      }
      
      return;
    }
  
    for(String otherID : getEdges()) {
      drawEdge(g, drawnEdges, otherID);
    }
  }
  
  
  /** Draws the edge between this node and another node if the edge hasn't been drawn yet. */
  private void drawEdge(Graphics2D g, Set<String> drawnEdges, String otherID) {
    
    VertexSprite other = graph.getSprite(otherID);
    if(other == null) {
      return;
    } 
    else if(!other.isVisible()) {
    //  VertexSprite visibleAncestor = other.getFirstVisible();
    //  if(visibleAncestor != null) {
    //    drawEdge(g, drawnEdges, visibleAncestor.getID());
    //  }
      return;
    }
    
    
    String edgeKey = graph.getEdgeID(this.id, otherID);
    
    if(!drawnEdges.contains(edgeKey)) {
      EdgeStyle edgeStyle = graph.getStyle().getEdgeStyle(this, other);
      edgeStyle.setLabel(edgeLabels.get(otherID));
      
      drawnEdges.add(edgeKey);
      edgeStyle.draw(g, this, other, true); //graph.isDirected);
    }
  }
  
  /** Returns the VertexStyle being used to render this vertex. */
  public VertexStyle getStyle() {
    return graph.getStyle().getVertexStyle(this);
  }
  
  
  //////// Misc
  
  /** Gets the string used to display a tooltip for this node. */
  public String getTooltipString() {
    return "" + getObject();
  }
  
  public boolean isActive() {
    return (isVisible() && opacity > 0.5);
  }
  
  public String toString() {
    return "<VertexSprite: " + this.id + ">";
  }
}

