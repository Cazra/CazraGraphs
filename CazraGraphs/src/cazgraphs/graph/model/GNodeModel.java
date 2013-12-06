package cazgraphs.graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Model for a node in a graph. */
public class GNodeModel implements Comparable<GNodeModel> {
  
  /** The graph containing this node. */
  public GraphModel graph;
  
  /** 
   * This node's unique ID within its graph. 
   * Once this node is put into a graph, don't change its ID, ever!
   */
  public String id;
  
  /** An object being contained by this node. */
  public Object object;
  
  /** 
   * This node's outward edges (going to other nodes). 
   * Pleaes don't add or remove edges directly from this map. 
   * Use the addEdge and removeEdge methods instead.
   */
  public Map<String, GNodeModel> toEdges = new HashMap<>();
  
  /** 
   * This node's inward edges (coming from other nodes). 
   * Pleaes don't add or remove edges directly from this map. 
   * Use the addEdge and removeEdge methods instead.
   */
  public Map<String, GNodeModel> fromEdges = new HashMap<>();
  
  
  /** A list of observers interested in changes to the model. */
  protected List<GraphObserver> observers = new ArrayList<>();
  
  
  //////// Constructors 
  
  public GNodeModel(GraphModel graph, String id, Object object) {
    this.graph = graph;
    this.id = id;
    this.object = object;
  }
  
  public GNodeModel(GraphModel graph, Object obj) {
    this(graph, obj.toString(), obj);
  }
  
  
  public static int spawnID = 0;
  
  /** Spawns a new, unique GNodeModel and adds it to a graph. */
  public static GNodeModel makeInstance(GraphModel graph, Object obj) {
    String id = "node" + spawnID;
    while(graph.hasNode(id)) {
      spawnID++;
      id = "node" + spawnID;
    }
    spawnID++;
    
    GNodeModel node = new GNodeModel(graph, id, obj);
    graph.addNode(node);
    return node;
  }
  
  
  
  
  //////// Model ops
  
  /** Changes the object stored by this node. */
  public void setObject(Object o) {
    this.object = o;
    notifyObservers(GraphEvent.OBJECT_CHANGED, o);
  }
  
  
  /** Returns the set of all this node's neighbors. */
  public Set<GNodeModel> getNeighbors() {
    Set<GNodeModel> result = new HashSet<>();
    result.addAll(toEdges.values());
    result.addAll(fromEdges.values());
    return result;
  }
  
  
  /** Gets the set of nodes this node has an out-edge to. */
  public Set<GNodeModel> getEdges() {
    Set<GNodeModel> result = new HashSet<>();
    result.addAll(toEdges.values());
    return result;
  }
  
  /** Gets the set of nodes this node has an in-edge from. */
  public Set<GNodeModel> getFromEdges() {
    Set<GNodeModel> result = new HashSet<>();
    result.addAll(fromEdges.values());
    return result;
  }
  
  
  /** 
   * Returns a node this node has an edge to, given that node's ID. 
   * Returns null if the edge doesn't exist.
   */
  public GNodeModel getEdge(String otherID) {
    return toEdges.get(otherID);
  }
  
  /** 
   * Returns a node this node has an edge from, given that node's ID. 
   * Returns null if the edge doesn't exist.
   */
  public GNodeModel getEdgeFrom(String otherID) {
    return fromEdges.get(otherID);
  }
  
  
  /** Returns true iff this node has an edge to the node with the given ID. */
  public boolean hasEdge(String otherID) {
    return toEdges.containsKey(otherID);
  }
  
  /** Returns true iff this node has an edge to the specified node. */
  public boolean hasEdge(GNodeModel other) {
    if(other == null) {
      return false;
    }
    else {
      return hasEdge(other.id);
    }
  }
  
  
  /** Returns true iff this node has an edge from the node with the given ID. */
  public boolean hasEdgeFrom(String otherID) {
    return fromEdges.containsKey(otherID);
  }
  
  /** Returns true iff this node has an edge from the specified node. */
  public boolean hasEdgeFrom(GNodeModel other) {
    if(other == null) {
      return false;
    }
    else {
      return hasEdgeFrom(other.id);
    }
  }
  
  
  /** 
   * Adds an edge from this node to another node. 
   * @param other     The node we're making an edge for.
   * @return          This, for chaining.
   */
  public GNodeModel addEdge(GNodeModel other) {
    if(other == null) {
      return this;
    }
    
    this.toEdges.put(other.id, other);
    other.fromEdges.put(this.id, this);
    
    if(!graph.isDirected) {
      other.toEdges.put(this.id, this);
      this.fromEdges.put(other.id, other);
    }
    
    notifyObservers(GraphEvent.EDGE_ADDED, other);
    return this;
  }
  
  
  /** 
   * Removes an edge from this node. 
   * @param otherID     The id of neighbor being removed.
   * @return        This, for chaining.
   */
  public GNodeModel removeEdge(String otherID) {
    GNodeModel other = toEdges.remove(otherID);
    if(other == null) {
      return this;
    }
    
    other.fromEdges.remove(this.id);
    
    if(!graph.isDirected) {
      other.removeEdge(this.id);
    }
    
    notifyObservers(GraphEvent.EDGE_REMOVED, other);
    return this;
  }
  
  public GNodeModel removeEdge(GNodeModel other) {
    if(other == null) {
      return this;
    }
    else {
      return removeEdge(other.id);
    }
  }
  
  /**
   * Removes all edges from this node.
   * @return    This, for chaining.
   */
  public GNodeModel removeAllEdges() {
    for(String otherID : toEdges.keySet()) {
      removeEdge(otherID);
    }
    return this;
  }
  
  
  /** Return the in-degree of this node - the number of edges going in to this node */
  public int inDegree() {
    return fromEdges.size();
  }
  
  /** Return the out-degree of this node - the number of edges going out from this node */
  public int outDegree() {
    return toEdges.size();
  }
  
  
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
  
  
  
  //////// Misc
  
  public String toString() {
    return "<GNodeModel: " + this.id + ">";
  }
  
  
  /** Nodes are compared by their IDs. */
  public int compareTo(GNodeModel other) {
    return this.id.compareTo(other.id);
  }
  
}

