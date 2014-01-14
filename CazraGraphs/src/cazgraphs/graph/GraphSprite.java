package cazgraphs.graph;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pwnee.*;
import pwnee.sprites.Sprite;

import cazgraphs.CazgraphException;
import cazgraphs.graph.layout.*;
import cazgraphs.graph.model.DirectedGraph;
import cazgraphs.graph.style.*;


/** 
 * A sprite representing a directed or undirected graph.
 *
 * Each node in the graph is associated with a unique ID.
 * The nodes each maintain their own set of edges, instead of the graph 
 * maintaining all the edges. 
 */
public class GraphSprite extends Sprite {
  
  
  /** A mapping of vertices in the graph to sprites used to represent them. */
  private Map<String, VertexSprite> vertexSprites = new HashMap<>();
  
  /** The actual graph data structure this sprite provides a view for. */
  private DirectedGraph graph;
  
  

  /** The most recently selected node. Null if no nodes are currently selected.*/
  public VertexSprite selectedNode = null;
  
  /** The set of nodes that are currently selected. */
  public Set<VertexSprite> selectedNodes = new HashSet<>();
  
  /** 
   * Optional reference to a camera. This is potentially useful for clipping and 
   * for setting uniform stroke widths, regardless of the camera's zoom.
   */
  public Camera camera;
  
  /** The style for specifying the colors and font metrics for this graph. */
  private GraphStyle style = new GraphStyle();
  
  
  /** The default style used to render the graph's edges. */
  private EdgeStyle defaultEdgeStyle = new SolidEdgeStyle();
  
  
  /** The layout algorithm used by this graph. */
  public GraphLayout layoutAlgorithm = new DefaultGraphLayout();
  
  /** These nodes ignore all physics to influence their position. */
  public Set<VertexSprite> anchorNodes = new HashSet<>();
  
  
  
  /** Creates a directed graph at the origin using the provided model. */  
  public GraphSprite(DirectedGraph graph) {
    super(0, 0);
    this.graph = graph;
    for(String vertexID : graph.getVertexIDs()) {
      vertexSprites.put(vertexID, new VertexSprite(this, vertexID));
    }
  }
  
  /** Creates an empty directed graph at the origin. */
  public GraphSprite() {
    this(new DirectedGraph());
  }
  
  
  
  
  //////// Model operations
  
  /** Returns the underlying graph data structure for this sprite. */
  public DirectedGraph getGraph() {
    return graph;
  }
  
  
  
  /** Returns true iff the graph contains a vertex with the specified ID. */
  public boolean hasVertex(String id) {
    return graph.hasVertex(id);
  }
  
  
  /** Returns the set of all the vertex IDs in the graph. */
  public Set<String> getVertexIDs() {
    return graph.getVertexIDs();
  }
  
  
  /**
   * Creates a vertex and adds it to the graph.
   * @param   id    The unique ID to represent the vertex in this graph.
   * @param   object    The object stored at the vertex.
   * @return  The VertexSprite that was added to the graph.
   */
  public VertexSprite addVertex(String id, Object object) {
    graph.addVertex(id, object);
    
    VertexSprite sprite = new VertexSprite(this, id);
    vertexSprites.put(id, sprite);
    
    layoutAlgorithm.setFrozen(false);
    return sprite;
  }
  
  
  /**
   * Adds a vertex containing a String to the graph. The String is both the
   * unique key and the contents of the vertex.
   * @return The VertexSprite that was added to the graph.
   * @throws CazgraphException if id is null.
   */
  public VertexSprite addVertex(String id) {
    return addVertex(id, id);
  }
  
  
  /** Removes a vertex from the graph, if it exists. */
  public void removeVertex(String id) {
    graph.removeVertex(id);
    
    vertexSprites.remove(id);
    
    layoutAlgorithm.setFrozen(false);
  }
  
  
  /** Gets the object stored at a vertex in the graph. */
  public Object getObject(String vertexID) {
    return graph.getObject(vertexID);
  }
  
  
  /** Sets the object stored at a vertex in the graph. */
  public void setObject(String vertexID, Object obj) {
    graph.setObject(vertexID, obj);
  }
  
  
  /** Returns true iff the specified edge exists in this graph. */
  public boolean hasEdge(String from, String to) {
    return graph.hasEdge(from, to);
  }
  
  
  /** Returns the union of the forward and backward edges for the specified vertex. */
  public Set<String> getNeighbors(String vertexID) {
    return graph.getNeighbors(vertexID);
  }
  
  
  /** Returns the set of forward edges for the specified vertex. */
  public Set<String> getEdges(String vertexID) {
    return graph.getEdges(vertexID);
  }
  
  
  /** Returns the set of backward edges for the specified vertex. */
  public Set<String> getBackwardEdges(String vertexID) {
    return graph.getBackwardEdges(vertexID);
  }
  
  
  /** 
   * Adds an edge from one vertex to another, given the IDs of the vertices.
   */
  public void addEdge(String from, String to) {
    addEdge(from, to, "");
  }
  
  /** 
   * Adds a labeled edge from one vertex to another, 
   * given the IDs of the vertices and the text for the label.
   * The vertex from must already exist in the graph, but the vertex to doesn't
   * need to be created yet for the edge to be added to the graph.
   */
  public void addEdge(String from, String to, String label) {
    graph.addEdge(from, to);
    
    VertexSprite vertex = getSprite(from);
    // Create the label for the edge if one was provided.
    if(label != null && !"".equals(label)) {
      vertex.setEdgeLabel(to, label);
    }
    
    layoutAlgorithm.setFrozen(false);
  }
  
  
  
  /** Removes the edge from one vertex to another if it is present. */
  public void removeEdge(String from, String to) {
    graph.removeEdge(from, to);
    layoutAlgorithm.setFrozen(false);
  }
  
  /** Removes all edges in this graph. */
  public void removeAllEdges() {
    graph.removeAllEdges();
  }
  
  
  /** Removes all edges to and from the specified vertex. */
  public void removeAllEdges(String vertexID) {
    graph.removeAllEdges(vertexID);
  }
  
  
  /** Gets the unique ID for an edge between two vertices. */
  public String getEdgeID(String from, String to) {
    if(from.compareTo(to) < 0) {
      return from + "->" + to;
    }
    else {
      return to + "->" + from;
    }
  }
  
  
  /** Clears the graph. */
  public void clear() {
    graph.clear();
  }
  
  
  
  //////// Vertex sprites
  
  
  
  /** Returns the sprite for the given vertex ID. */
  public VertexSprite getSprite(String vertexID) {
    return vertexSprites.get(vertexID);
  }
  
  
  /** Returns the collection of vertex sprites in the graph. */
  public Collection<VertexSprite> getSprites() {
    return vertexSprites.values();
  }
  
  
  
  //////// Graph properties
  
  /** Returns the number of vertices in the graph. */
  public int size() {
    return graph.size();
  }
  
  
  /** 
   * Returns a list of all the root nodes for components in the graph. 
   * Ideally, a root should be a source node - it has no incoming edges. 
   * In cyclic graph components that don't have a source node, this will try to  
   * return the critical node in that component with the most edges. 
   */
  public Set<String> findRoots() {
    return GraphSolver.findRoots(getGraph());
  }
  
  /** Returns a list of the connected components of the graph. */
  public List<Set<String>> findComponents() {
    return GraphSolver.findComponents(getGraph());
  }
  
  /** Returns true iff this graph contains any cycles. */
  public boolean hasCycles() {
    return GraphSolver.hasCycles(getGraph(), true);
  }
  
  /** Returns true iff this graph is a tree (or a forest of trees). */
  public boolean isTree() {
    return GraphSolver.isTree(getGraph());
  }
  
  //////// Layout
  
  
  /** 
   * Performs one step through the layout algorithm.
   */
  public void stepLayout() {
    layoutAlgorithm.stepLayout(this);
  }
  
  /** Freezes the graph so that the layout algorithm becomes inactive. */
  public void freezeLayout() {
    layoutAlgorithm.setFrozen(true);
  }
  
  
  //////// Interaction 
  
  
  /** 
   * Returns the topmost node containing the given point, in view coordinates. 
   * Null, if no node contains the point.
   */
  public VertexSprite getNodeAtPoint(Point2D p) {
    List<VertexSprite> spriteList = new ArrayList<>(vertexSprites.values());
    Collections.reverse(spriteList);
    
    for(VertexSprite vertex : spriteList) {
      if(vertex.containsPoint(p)) {
        return vertex;
      }
    }
    
    return null;
  }
  
  
  
  
  /** Adds a vertex to the current set of selected vertices. */
  public void selectVertex(VertexSprite vertex) {
    selectedNode = vertex;
    if(vertex != null) {
      vertex.setSelected(true);
      selectedNodes.add(vertex);
    }
  }
  
  /** Adds a vertex to the current set of selected vertices. */
  public void selectVertex(String vertexID) {
    if(vertexID != null) {
      selectVertex(getSprite(vertexID));
    }
  }
  
  /**
   * Sets a vertex to become the only selected vertex in this graph.
   * Null causes no vertex to be currently selected. 
   */
  public void selectSingleVertex(VertexSprite vertex) {
    unselectAll();
    selectVertex(vertex);
  }
  
  /**
   * Sets a vertex to become the only selected vertex in this graph.
   * Null causes no vertex to be currently selected. 
   */
  public void selectSingleVertex(String vertexID) {
    unselectAll();
    selectVertex(vertexID);
  }
  
  
  /** Unselects all nodes. */
  public void unselectAll() {
    for(VertexSprite vertex : selectedNodes) {
      vertex.setSelected(false);
    }
    selectedNodes.clear();
  }
  
  
  //////// Rendering
  
  /** Draws the graph's vertices and edges. */
  public void draw(Graphics2D g) {
    AffineTransform origT = g.getTransform();
    g.setTransform(new AffineTransform());
    
    Rectangle2D clipArea = g.getClip().getBounds();
    BufferedImage drawBuffer = new BufferedImage((int) clipArea.getWidth(), (int) clipArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D bufferG = drawBuffer.createGraphics();
    bufferG.setTransform(origT);
    
    // Draw the edges. 
    Set<String> drawnEdges = new HashSet<>();
    for(VertexSprite vertex : getSprites()) {
      vertex.drawEdges(bufferG, drawnEdges);
    }
    
    // Draw the vertices.
    for(VertexSprite vertex : getSprites()) {
      vertex.render(bufferG);
    //  Rectangle2D box = vertex.getCollisionBox();
    //  if(box != null) {
    //    g.setColor(new Color(0xFFAAAA));
    //    g.draw(box);
    //  }
    }
    
    bufferG.dispose();
    g.drawImage(drawBuffer, 0, 0, null);
    g.setTransform(origT);
  }
  
  
  /** Gets the GraphStyle currently being used by the graph. */
  public GraphStyle getStyle() {
    return style;
  }
  
  /** 
   * Sets the style for the graph to use. 
   * @throws CazgraphException if style is null.
   */
  public void setStyle(GraphStyle style) {
    if(style == null) {
      throw new CazgraphException("The graph's style cannot be null.");
    }
    
    this.style = style;
  }
  
  /** Returns the default style used to render edges in this graph. */
  public EdgeStyle getDefaultEdgeStyle() {
    return defaultEdgeStyle;
  }
  
}

