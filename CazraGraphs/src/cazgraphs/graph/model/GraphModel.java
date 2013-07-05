package cazgraphs.graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Model for a graph, directed or undirected. */
public class GraphModel {
  
  /** The nodes in this graph. */
  public Map<String, GNodeModel> nodes = new HashMap<>();
  
  /** Whether this graph's edges are directed. */
  public boolean isDirected;
  
  /** A list of observers interested in changes to the model. */
  protected List<GraphObserver> observers = new ArrayList<>();
  
  
  
  //////// Constructors
  
  public GraphModel(boolean isDirected) {
    this.isDirected = isDirected;
  }
  
  /** Default constructor makes an undirected graph. */
  public GraphModel() {
    this(false);
  }
  
  
  
  //////// Model ops
  
  /** Returns a node in this graph, given that node's id. */
  public GNodeModel getNode(String id) {
    return nodes.get(id);
  }
  
  
  /** Returns true iff the graph has a node with the specified ID. */
  public boolean hasNode(String id) {
    return nodes.containsKey(id);
  }
  
  
  /**
   * Creates a node with the specified ID for an object and 
   * adds it to the graph.
   * @return  The node that was just added.
   */
  public GNodeModel addNode(String id, Object object) {
    GNodeModel node = new GNodeModel(this, id, object);
    this.nodes.put(node.id, node);
    
    notifyObservers(GraphEvent.NODE_ADDED, node);
    return node;
  }
  
  public GNodeModel addNode(Object object) {
    if(object == null) {
      return null;
    }
    else if(object instanceof GNodeModel) {
      GNodeModel node = (GNodeModel) object;
      this.nodes.put(node.id, node);
      notifyObservers(GraphEvent.NODE_ADDED, node);
      return node;
    }
    else {
      return addNode(object.toString(), object);
    }
  }
  
  
  /** 
   * Removes a node from the graph. 
   * @param id    The ID of the node to remove.
   * @return      The removed node.
   */
  public GNodeModel removeNode(String id) {
    GNodeModel node = nodes.remove(id);
    if(node == null) {
      return null;
    }
    
    node.removeAllEdges();
    notifyObservers(GraphEvent.NODE_REMOVED, node);
    return node;
  }
  
  public GNodeModel removeNode(GNodeModel node) {
    if(node == null) {
      return null;
    }
    else {
      return removeNode(node.id);
    }
  }
  
  
  /** Adds an edge from one node to another. */
  public void addEdge(GNodeModel fromNode, GNodeModel toNode) {
    if(fromNode != null && toNode != null) {
      fromNode.addEdge(toNode);
    }
  }
  
  public void addEdge(String fromID, String toID) {
    GNodeModel fromNode = nodes.get(fromID);
    GNodeModel toNode = nodes.get(toID);
    
    addEdge(fromNode, toNode);
  }
  
  
  //////// Algorithms
  
  
  //////// Observers
  
  /** Subscribes an observer to this node. */
  public void addObserver(GraphObserver observer) {
    observers.add(observer);
  }
  
  /** Unsubscribes an observer. */
  public void removeObserver(GraphObserver observer) {
    observers.remove(observer);
  }
  
  /** Unsubscribe all observers from this node. */
  public void removeAllObservers() {
    observers.clear();
  }
  
  /** Sends a GraphEvent about this node to all registered observers. */
  protected void notifyObservers(int eventCode, Object subject) {
    GraphEvent evt = new GraphEvent(this, eventCode, subject);
    
    for(GraphObserver observer : observers) {
      observer.handleGraphEvent(evt);
    }
  }
  
}
