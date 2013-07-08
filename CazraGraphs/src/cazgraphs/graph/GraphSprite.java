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

import cazgraphs.graph.layout.*;
import cazgraphs.graph.style.*;


/** 
 * A sprite for a graph visualization.
 *
 * Each node in the graph is associated with a unique ID.
 * The nodes each maintain their own set of edges, instead of the graph 
 * maintaining all the edges. 
 */
public class GraphSprite extends Sprite {
  
  /** The nodes in this graph. */
  public Map<String, GNodeSprite> nodes = new HashMap<>();
  
  /** These nodes ignore all physics to influence their position. */
  public Set<GNodeSprite> anchorNodes = new HashSet<>();
  
  /** 
   * Whether this map is directed. If false, then the graph's edges are 
   * bidirectional. 
   */
  public boolean isDirected;
  
  /** The most recently selected node. Null if no nodes are currently selected.*/
  public GNodeSprite selectedNode = null;
  
  /** The set of nodes that are currently selected. */
  public Set<GNodeSprite> selectedNodes = new HashSet<>();
  
  /** 
   * Optional reference to a camera. This is potentially useful for clipping and 
   * for setting uniform stroke widths, regardless of the camera's zoom.
   */
  public Camera camera;
  
  /** The style for specifying the colors and font metrics for this graph. */
  public GraphStyle style = new GraphStyle();
  
  /** The layout algorithm used by this graph. */
  public GraphLayout layoutAlgorithm = new DefaultGraphLayout();
  
  public GraphSprite(double x, double y, boolean directed) {
    super(x,y);
    isDirected = directed;
  }
  
  /** Creates a bidirectional graph. */
  public GraphSprite(double x, double y) {
    this(x,y,false);
  }
  
  public GraphSprite(boolean directed) {
    this(0, 0, directed);
  }
  
  public GraphSprite() {
    this(0,0,false);
  }
  
  
  
  
  //////// Model
  
  /** Returns a node in this graph, given that node's id. */
  public GNodeSprite getNode(String id) {
    return nodes.get(id);
  }
  
  
  /**
   * Creates a node and adds it to the graph.
   * @return  The node that was just added.
   */
  public GNodeSprite addNode(String id, Object object) {
    GNodeSprite node = new GNodeSprite(this, id, object);
    this.nodes.put(node.id, node);
    layoutAlgorithm.setFrozen(false);
    return node;
  }
  
  public GNodeSprite addNode(Object object) {
    if(object == null) {
      return null;
    }
    return addNode(object.toString(), object);
  }
  
  
  /** Adds an edge from node id1 to node id2. */
  public void addEdge(String id1, String id2) {
    GNodeSprite node1 = nodes.get(id1);
    GNodeSprite node2 = nodes.get(id2);
    
    if(node1 != null && node2 != null) {
      node1.addEdge(node2);
    }
    layoutAlgorithm.setFrozen(false);
  }
  
  
  public void addEdge(GNodeSprite n1, GNodeSprite n2) {
    addEdge(n1.id, n2.id);
  }
  
  
  /** 
   * Returns a list of all the root nodes for components in the graph. 
   * Ideally, a root should be a source node - it has no incoming edges. 
   * In cyclic graph components that don't have a source node, this will try to  
   * return the critical node in that component with the most edges. 
   */
  public List<GNodeSprite> findRoots() {
    return GraphSolver.findRoots(this);
  }
  
  /** Returns a list of the connected components of the graph. */
  public List<List<GNodeSprite>> findComponents() {
    return GraphSolver.findComponents(this);
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
   * Returns the node containing the given point, in view coordinates. 
   * Null, if no node contains the point.
   */
  public GNodeSprite getNodeAtPoint(Point2D p) {
    List<GNodeSprite> nodeList = new ArrayList<>(nodes.values());
    Collections.reverse(nodeList);
    
    for(GNodeSprite node : nodeList) {
      if(node.containsPoint(p)) {
        return node;
      }
    }
    
    return null;
  }
  
  /** 
   * Causes a node in this graph to become selected.
   * Null causes no node to be currently selected. 
   */
  public void selectNode(GNodeSprite node) {
    selectedNode = node;
    if(node != null) {
      node.isSelected = true;
      selectedNodes.add(node);
    }
    else {
      for(GNodeSprite selSprite : selectedNodes) {
        selSprite.isSelected = false;
      }
      selectedNodes.clear();
    }
  }
  
  /**
   * Sets a node to become the only selected node in this graph.
   * Null causes no node to be currently selected. 
   */
  public void selectSingleNode(GNodeSprite node) {
    selectNode(null);
    selectNode(node);
  }
  
  //////// Rendering
  
  /** Draws all the nodes in the graph and their edges. */
  public void draw(Graphics2D g) {
    AffineTransform origT = g.getTransform();
    g.setTransform(new AffineTransform());
    
    Rectangle2D clipArea = g.getClip().getBounds();
    BufferedImage drawBuffer = new BufferedImage((int) clipArea.getWidth(), (int) clipArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D bufferG = drawBuffer.createGraphics();
    bufferG.setTransform(origT);
    
    // Draw the edges. 
    Set<String> drawnEdges = new HashSet<>();
    for(GNodeSprite node : nodes.values()) {
      node.drawEdges(bufferG, drawnEdges);
    }
    
    // Draw the nodes.
    for(GNodeSprite node : nodes.values()) {
      node.render(bufferG);
      Rectangle2D box = node.getCollisionBox();
    //  if(box != null) {
    //    g.setColor(new Color(0xFFAAAA));
    //    g.draw(box);
    //  }
    }
    
    bufferG.dispose();
    g.drawImage(drawBuffer, 0, 0, null);
    g.setTransform(origT);
  }
}

