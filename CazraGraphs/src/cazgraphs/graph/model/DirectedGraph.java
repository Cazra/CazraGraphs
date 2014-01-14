package cazgraphs.graph.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cazgraphs.CazgraphException;

/** A data structure for a directed graph. */
public class DirectedGraph {
  
  /** 
   * The vertices, represented as a map of unique vertex IDs to the objects 
   * contained at the vertices. 
   */
  private Map<String, Object> vertices;
  
  /** The sets of forward edges for all vertices in the graph. */
  private Map<String, Set<String>> edges;
  
  /** The sets of backward edges corresponding to the graph's set of forward edges. */
  private Map<String, Set<String>> backEdges;
  
  
  /** Creates an empty directed graph. */
  public DirectedGraph() {
    vertices = new HashMap<>();
    edges = new HashMap<>();
    backEdges = new HashMap<>();
  }
  
  
  
  /** Returns the number of vertices in the graph. */
  public int size() {
    return vertices.size();
  }
  
  /** Removes all vertices from the graph. */
  public void clear() {
    removeAllEdges();
    vertices.clear();
  }
  
  
  /** 
   * Creates a copy of this directed graph that can be modified without 
   * changing the structure of the original graph.
   */
  public DirectedGraph createCopy() {
    DirectedGraph copy = new DirectedGraph();
    
    // copy vertices
    for(String vertexID : getVertexIDs()) {
      copy.addVertex(vertexID, getObject(vertexID));
    }
    
    // copy edges
    for(String from : getVertexIDs()) {
      for(String to : getEdges(from)) {
        copy.addEdge(from, to);
      }
    }
    
    return copy;
  }
  
  //////// Vertex operations
  
  /** Returns true iff the graph contains a vertex with the specified ID. */
  public boolean hasVertex(String id) {
    return vertices.containsKey(id);
  }
  
  
  /** Returns the object stored at a vertex. */
  public Object getObject(String vertexID) {
    return vertices.get(vertexID);
  }
  
  /** Sets the object stored at a vertex. */
  public void setObject(String vertexID, Object obj) {
    if(!hasVertex(vertexID)) {
      throw new CazgraphException("Cannot set object for " + vertexID + " because " + vertexID + " doesn't exist.");
    }
  }
  
  
  /** Returns the set of IDs for all vertices present in this graph. */
  public Set<String> getVertexIDs() {
    return vertices.keySet();
  }
  
  
  /** 
   * Adds a vertex to the graph, given a unique ID for the vertex and the 
   * object stored at the vertex. 
   */
  public void addVertex(String id, Object obj) {
    if(id == null) {
      throw new CazgraphException("The ID of a vertex cannot be null.");
    }
    
    vertices.put(id, obj);
    edges.put(id, new HashSet<String>());
    backEdges.put(id, new HashSet<String>());
  }
  
  
  /** Removes a vertex from the graph. */
  public void removeVertex(String id) {
    removeAllEdges(id);
    vertices.remove(id);
  }
  
  
  /** Removes all vertices accessible from the specified vertex. */
  public void removeSubGraph(String startVertexID) {
    Stack<String> dfs = new Stack<>();
    dfs.push(startVertexID);
    
    while(!dfs.empty()) {
      String vertexID = dfs.pop();
      if(hasVertex(vertexID)) {
        for(String otherID : getEdges(vertexID)) {
          dfs.push(otherID);
        }
        removeVertex(vertexID);
      }
      
    }
  }
  
  
  //////// Edge operations
  
  /** Returns true iff the specified edge exists in this graph. */
  public boolean hasEdge(String from, String to) {
    return hasVertex(from) && edges.get(from).contains(to);
  }
  
  
  /** Returns the set of forward edges for the specified vertex. */
  public Set<String> getEdges(String vertexID) {
    if(!hasVertex(vertexID)) {
      throw new CazgraphException("Cannot get edges for " + vertexID + 
                                  " because it doesn't exist in the graph.");
    }
    return edges.get(vertexID);
  }
  
  
  /** Returns the set of backward edges for the specified vertex. */
  public Set<String> getBackwardEdges(String vertexID) {
    if(!hasVertex(vertexID)) {
      throw new CazgraphException("Cannot get edges for " + vertexID + 
                                  " because it doesn't exist in the graph.");
    }
    return backEdges.get(vertexID);
  }
  
  
  /** Returns the union of the forward and backward edges for the specified vertex. */
  public Set<String> getNeighbors(String vertexID) {
    Set<String> result = new HashSet<>();
    result.addAll(getEdges(vertexID));
    result.addAll(getBackwardEdges(vertexID));
    return result;
  }
  
  /** Adds a directed edge to the graph. */
  public void addEdge(String from, String to) {
    if(from == null || to == null) {
      throw new CazgraphException("Cannot create null edge: " + from + " -> " + to);
    }
    
    if(!hasVertex(from)) {
      throw new CazgraphException("Cannot create edge {" + from + " -> " + to + "} because " + from + " doesn't exist.");
    }
    
    if(!hasVertex(to)) {
      throw new CazgraphException("Cannot create edge {" + from + " -> " + to + "} because " + to + " doesn't exist.");
    }
    
    // Create the forward edge.
    if(!edges.containsKey(from)) {
      edges.put(from, new HashSet<String>());
    }
    edges.get(from).add(to);
    
    // Create the corresponding backward edge.
    if(!backEdges.containsKey(to)) {
      backEdges.put(to, new HashSet<String>());
    }
    backEdges.get(to).add(from);
  }
  
  
  /** Removes a directed edge from the graph if it exists. */
  public void removeEdge(String from, String to) {
    if(edges.containsKey(from)) {
        edges.get(from).remove(to);
    }
    
    if(backEdges.containsKey(to)) {
      backEdges.get(to).remove(from);
    }
  }
  
  
  /** Removes all edges in this graph. */
  public void removeAllEdges() {
    edges.clear();
    backEdges.clear();
  }
  
  
  /** Removes all edges to and from the specified vertex. */
  public void removeAllEdges(String vertexID) {
    for(String to : new HashSet<String>(getEdges(vertexID))) {
      removeEdge(vertexID, to);
    }
    
    for(String from : new HashSet<String>(getBackwardEdges(vertexID))) {
      removeEdge(from, vertexID);
    }
  }
  
  
  
}




