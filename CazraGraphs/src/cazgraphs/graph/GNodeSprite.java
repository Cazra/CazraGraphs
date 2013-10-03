package cazgraphs.graph;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pwnee.*;
import pwnee.sprites.Sprite;
import pwnee.text.Tooltipable;

import cazgraphs.graph.style.*;
import cazgraphs.util.FontUtils;

/** 
 * A sprite for representing a node in a GraphSprite.
 * Each node has a unique String ID (unique in its graph) 
 */
public class GNodeSprite extends Sprite implements Comparable<GNodeSprite>, Tooltipable {
  
  /** 
   * A unique ID for this node in its graph. This is set in the constructor 
   * and should never change... EVER!!!
   */
  public String id = "";
  
  /** The object stored in this node. Unlike id, this is allowed to change. */
  public Object object;
  
  /** The label for displaying object in this node. */
  public GNodeLabel label;
  
  /** The graph containing this node. */
  public GraphSprite graph;
  
  /** 
   * This node's outward edges (going to other nodes). 
   * Pleaes don't add or remove edges directly from this map. 
   * Use the addEdge and removeEdge methods instead.
   */
  public Map<String, GNodeSprite> toEdges = new HashMap<>();
  
  public Map<String, String> edgeLabels = new HashMap<>();
  
  /** 
   * This node's inward edges (coming from other nodes). 
   * Pleaes don't add or remove edges directly from this map. 
   * Use the addEdge and removeEdge methods instead.
   */
  public Map<String, GNodeSprite> fromEdges = new HashMap<>();
  
  
  /** 
   * The node's current velocity along the x-axis, for the convenience
   * of layout algorithms.
   */
  public double dx = 0;
  
  /** 
   * The node's current velocity along the y-axis, for the convenience
   * of layout algorithms.
   */
  public double dy = 0;
   
  /** 
   * The node's mass, for the convenience of layout algorithms.
   */
  public double mass = 1;
  
  
  /** The style used to render the node's shape and calculate collisions. */
  public NodeStyle style = new EllipseNodeStyle();
  
  /** The style used to render the node's outward edges. */
  public EdgeStyle edgeStyle = new SolidEdgeStyle();
  
  /** Whether this node is currently selected. */
  public boolean isSelected = false;
  
  
  
  
  /** 
   * Creates the node with a stored object. The ID for this node is the String
   * returned by the object's toString method.
   */
  public GNodeSprite(GraphSprite g, Object o) {
    super(0,0);
    graph = g;
    
    setObject(o);
    if(object != null) {
      id = object.toString();
    }
    else {
      id = "null";
    }
  }
  
  /**
   * Creates the node with an id specified by the user.
   */
  public GNodeSprite(GraphSprite g, String id, Object o) {
    this(g,o);
    this.id = id;
  }
  
  
  /** 
   * When a node is destroyed, clean up by disconnecting it 
   * from any existing edges.
   */
  public void destroy() {
    super.destroy();
    for(String nID : toEdges.keySet()) {
      GNodeSprite n = toEdges.get(nID);
      
      // Treat this as a bidirectional edge removal.
      n.removeEdge(this);
      this.removeEdge(nID);
    }
  }
  
  
  //////// Stored object
  
  /** Sets the object stored in this node and precomputes its label metrics. */
  public void setObject(Object o) {
    this.object = o;
    this.label = new GNodeLabel(graph, object);
    this.mass = Math.max(1,label.width * label.height /(32*32));
  }
  
  
  //////// Model
  
  /** Gets the set of all of this node's neighbors. */
  public Set<GNodeSprite> getNeighbors() {
    Set<GNodeSprite> neighbors = new HashSet<>();
    neighbors.addAll(toEdges.values());
    neighbors.addAll(fromEdges.values());
    return neighbors;
  }
  
  
  /** Gets the set of nodes this node has an out-edge to. */
  public Set<GNodeSprite> getEdges() {
    Set<GNodeSprite> neighbors = new HashSet<>();
    neighbors.addAll(toEdges.values());
    return neighbors;
  }
  
  /** Gets the set of nodes this node has an in-edge to. */
  public Set<GNodeSprite> getFromEdges() {
    Set<GNodeSprite> neighbors = new HashSet<>();
    neighbors.addAll(fromEdges.values());
    return neighbors;
  }
  
  
  /** 
   * Returns a node this node has an edge to, given that node's ID.
   * 'node.getEdge(nID)' is just more convenient than writing 
   * 'node.toEdges.get(nID)'.
   */
  public GNodeSprite getEdge(String nID) {
    return toEdges.get(nID);
  }
  
  /** 
   * Returns whether this node has an outward edge to another node.
   */
  public boolean hasEdge(GNodeSprite other) {
    if(other == null) {
      return false;
    }
    return hasEdge(other.id);
  }  
  
  public boolean hasEdge(String otherID) {
    return toEdges.containsKey(otherID);
  }
  

  /**
   * Adds a node to this node's edges.
   * @param n     The node we're making an edge for.
   * @return      This, for chaining.
   */
  public GNodeSprite addEdge(GNodeSprite n) {
    if(n == null) {
      return this;
    }
    
    this.toEdges.put(n.id, n);
    n.fromEdges.put(this.id, this);
    
    if(!graph.isDirected) {
      n.toEdges.put(this.id, this);
      this.fromEdges.put(n.id, n);
    }
    return this;
  }
  
  public GNodeSprite addEdge(GNodeSprite n, String label) {
    edgeLabels.put(n.id, label);
    return addEdge(n);
  }
  
  
  /** 
   * Removes a node from this node's edges.
   * @param nID      The id of neighbor being removed.
   * @return        This, for chaining.
   */
  public GNodeSprite removeEdge(String nID) {
    GNodeSprite next = toEdges.remove(nID);
    if(next == null) {
      return this;
    }
    
    next.fromEdges.remove(this.id);
    
    if(!graph.isDirected) {
      next.removeEdge(this);
    }
    return this;
  }
  
  public GNodeSprite removeEdge(GNodeSprite n) {
    if(n == null) {
      return this;
    }
    return removeEdge(n.id);
  }
  
  /**
   * Removes all edges from this node.
   * @return    This, for chaining.
   */
  public GNodeSprite removeAllEdges() {
    // remove all forward edges. 
    for(String nID : new HashSet<String>(toEdges.keySet())) {
      removeEdge(nID);
    }
    
    // remove all back edges.
    for(GNodeSprite prev : new HashSet<GNodeSprite>(fromEdges.values())) {
      prev.removeEdge(this.id);
    }
    return this;
  }
  
  
  
  /** Nodes are compared by their IDs. */
  public int compareTo(GNodeSprite other) {
    return this.id.compareTo(other.id);
  }
  
  
  /** Return the in-degree of this node - the number of edges going in to this node */
  public int inDegree() {
    return fromEdges.size();
  }
  
  /** Return the out-degree of this node - the number of edges going out from this node */
  public int outDegree() {
    return toEdges.size();
  }
  
  
  /** Returns true iff this node has an in-degree of 0. */
  public boolean isSource() {
    return (inDegree() == 0);
  }
  
  /** Returns true iff this node has an out-degree of 0. */
  public boolean isSink() {
    return (outDegree() == 0);
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
    if(!isVisible) {
      return null;
    }
    Dimension2D dims = getDimensions();
    return new Rectangle2D.Double(x-dims.getWidth()/2, y-dims.getHeight()/2, dims.getWidth(), dims.getHeight());
  }
  
  
  /** Returns the dimensions of the bounding box for the shape drawn by this node's style. */
  public Dimension2D getDimensions() {
    if(!isVisible) {
      return new Dimension(0,0);
    }
    return style.getDimensions(this);
  }
  
  
  /** 
   * Checks if this node's shape (Determined by its style) contains a point, 
   * given in view coordinates.
   */
  public boolean containsPoint(Point2D p) {
    if(!isVisible) {
      return false;
    }
    return style.containsPoint(p, this);
  }
  
  
  /** 
   * Returns the point on the boundary of this node's shape in a specified 
   * direction (angle in degrees) from the node's center. 
   */
  public Point2D getPointOnShape(double angle) {
    if(!isVisible) {
      return new Point2D.Double(x,y);
    }
    return style.getPointOnShape(angle, this);
  }
  
  
  //////// Rendering
  
  /** Draws the node, but not the edges. */
  public void draw(Graphics2D g) {
    g.setColor(new Color(0xAA0000));
    style.draw(g, this);
    
    if(graph.camera != null && graph.camera.zoom <= 1.0/12) {
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
    if(!isVisible) {
      return;
    }
  
    for(GNodeSprite other : toEdges.values()) {
      drawEdge(g, drawnEdges, other);
    }
  }
  
  /** Draws the edge between this node and another node if the edge hasn't been drawn yet. */
  public void drawEdge(Graphics2D g, Set<String> drawnEdges, GNodeSprite other) {
    drawEdge(g, drawnEdges, other, edgeStyle);
  }
  
  
  /** Draws the edge between this node and another node if the edge hasn't been drawn yet. */
  public void drawEdge(Graphics2D g, Set<String> drawnEdges, GNodeSprite other, EdgeStyle edgeStyle) {
    if(other == null || !other.isVisible) {
      return;
    }
    
    edgeStyle.setLabel(edgeLabels.get(other.id));
    
    String edgeKey = "";
    if(this.id.compareTo(other.id) < 0) {
      edgeKey = this.id + "->" + other.id;
    }
    else {
      edgeKey = other.id + "->" + this.id;
    }
    
    if(!drawnEdges.contains(edgeKey)) {
      drawnEdges.add(edgeKey);
      edgeStyle.draw(g, this, other, true); //graph.isDirected);
    }
  }
  
  
  //////// Misc
  
  /** Gets the string used to display a tooltip for this node. */
  public String getTooltipString() {
    return object.toString();
  }
  
  public boolean isActive() {
    return (isVisible && opacity > 0.5);
  }
  
  public String toString() {
    return "<GNodeSprite: " + this.id + ">";
  }
}

