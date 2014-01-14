package cazgraphs.graph;

import java.util.*;

import cazgraphs.graph.model.*;
import cazgraphs.graph.style.CyclicTreeGraphStyle;

/** 
 * Provides various static methods for solving various graph problems
 * with GraphSprites. 
 */
public class GraphSolver {
  
  /** Finds the connected components of a graph. Completes in O(n) time. */
  public static List<Set<String>> findComponents(DirectedGraph graph) {
    List<Set<String>> components = new ArrayList<>();
    
    // keep track of which which nodes have already been visited, along with their 
    Set<String> visited = new HashSet<>();
    for(String startID : graph.getVertexIDs()) {
      if(visited.contains(startID)) {
        continue;
      }
      
      Set<String> component = new HashSet<>();
      
      // do a depth-first-search to populate the component.
      Stack<String> dfs = new Stack<>();
      dfs.push(startID);
      while(!dfs.empty()) {
        String vertexID = dfs.pop();
        if(!visited.contains(vertexID)) {
          component.add(vertexID);
          visited.add(vertexID);
          
          for(String otherID : graph.getNeighbors(vertexID)) {
            dfs.push(otherID);
          }
        }
      }
      
      components.add(component);
    }
    
    return components;
  }
  
  
  
  /** 
   * Finds the roots of the connected components in a graph. 
   * Here we define the roots as a minimum set of vertices from which all
   * other vertices in the graph can be accessed.
   * Completes in O(n) time.
   */
  public static Set<String> findRoots(DirectedGraph graph) {
    DirectedGraph graphCopy = graph.createCopy();
    Set<String> roots = new HashSet<>();
    
    // Source vertices are roots.
    Set<String> sources = findSources(graphCopy);
    
    // Add each source as a root and remove their subgraphs from the graph copy.
    for(String source : sources) {
      roots.add(source);
      graphCopy.removeSubGraph(source);
    }
    
    // Find the remaining non-source root vertices.
    while(graphCopy.size() > 0) {
      List<Set<String>> components = findComponents(graphCopy);
      for(Set<String> component : components) {
        String topID = null;
        Set<String> visited = new HashSet<>();
        
        // Find the "top" vertices in each component.
        for(String vertexID : component) {
          if(!visited.contains(vertexID)) {
            topID = vertexID;
            
            // Visit each vertex accessible from the top vertex by dfs.
            Stack<String> dfs = new Stack<>();
            dfs.push(topID);
            while(!dfs.empty()) {
              String id = dfs.pop();
              if(!visited.contains(id)) {
                visited.add(id);
                for(String otherID : graphCopy.getEdges(id)) {
                  dfs.push(otherID);
                }
              }
            }
          }
        }
        
        // Add the top vertex to our roots, and remove its subgraph.
        roots.add(topID);
        graphCopy.removeSubGraph(topID);
      }
    }
    
    return roots;
  }
  
  
  /** Finds all the source nodes in a graph. */
  public static Set<String> findSources(DirectedGraph graph) {
    Set<String> sources = new HashSet<>();
    
    for(String vertexID : graph.getVertexIDs()) {
      if(graph.getBackwardEdges(vertexID).size() == 0) {
        sources.add(vertexID);
      }
    }
    
    return sources;
  }
  
  
  /** Finds all the sink nodes in a graph. */
  public static Set<String> findSinks(GraphSprite graph) {
    Set<String> sinks = new HashSet<>();
    
    for(String vertexID : graph.getVertexIDs()) {
      if(graph.getEdges(vertexID).size() == 0) {
        sinks.add(vertexID);
      }
    }
    
    return sinks;
  }
  
  
  /** Returns the set of nodes reachable from a particular node. Completes in O(n) time. */
  public static Set<String> reachableNodes(DirectedGraph graph, String rootID) {  
    Set<String> visited = new HashSet<>();
    Stack<String> dfs = new Stack<>();
    
    dfs.push(rootID);
    while(!dfs.empty()) {
      String vertexID = dfs.pop();
      
      if(!visited.contains(vertexID)) {
        visited.add(vertexID);
        
        for(String otherID : graph.getEdges(vertexID)) {
          dfs.push(otherID);
        }
      }
    }
    
    return visited;
  }
  
  
  
  /** 
   * Calculates the topology of the graph starting from a single "top" node. 
   * This information is returned as a map from the nodes to their depths.
   * Nodes that aren't reachable from the top node have a depth of null, 
   * the top node has a depth of 0, and all other nodes have a depth equal to 
   * their distance from the top node. 
   * Completes in O(n) time.
   */
  public static Map<String, Integer> simpleTopology(DirectedGraph graph, String topID) {
    Map<String, Integer> depths = new HashMap<>();
    
    Set<String> visited = new HashSet<>();
    Queue<String> bfsNodes = new LinkedList<>();
    Queue<Integer> bfsDepths = new LinkedList<>();
    
    bfsNodes.add(topID);
    bfsDepths.add(0);
    while(!bfsNodes.isEmpty()) {
      String vertexID = bfsNodes.remove();
      int depth = bfsDepths.remove();
      
      if(visited.contains(vertexID)) {
        continue;
      }
      visited.add(vertexID);
      depths.put(vertexID, depth);
      
      for(String otherID : graph.getEdges(vertexID)) {
        bfsNodes.add(otherID);
        bfsDepths.add(depth + 1);
      }
    }
    
    return depths;
  }
  
  
  /** 
   * Calculates the reverse topology of the graph starting from a single "bottom" node. 
   * This information is returned as a map from the nodes to their depths.
   * Nodes that aren't reachable from the bottom node have a depth of null, 
   * the bottom node has a depth of 0, and all other nodes have a depth equal to 
   * their distance from the bottom node. 
   * Completes in O(n) time.
   */
  public static Map<String, Integer> simpleReverseTopology(DirectedGraph graph, String bottomID) {
    Map<String, Integer> depths = new HashMap<>();
    
    Set<String> visited = new HashSet<>();
    Queue<String> bfsNodes = new LinkedList<>();
    Queue<Integer> bfsDepths = new LinkedList<>();
    
    bfsNodes.add(bottomID);
    bfsDepths.add(0);
    while(!bfsNodes.isEmpty()) {
      String vertexID = bfsNodes.remove();
      int depth = bfsDepths.remove();
      
      if(visited.contains(vertexID)) {
        continue;
      }
      visited.add(vertexID);
      depths.put(vertexID, depth);
      
      for(String otherID : graph.getBackwardEdges(vertexID)) {
        bfsNodes.add(otherID);
        bfsDepths.add(depth + 1);
      }
    }
    
    return depths;
  }
  
  
  
  
  /**
   * Computes the bipartiteness of a graph.
   * A list of 3 sets are returned. The first two are the bipartite sets.
   * The third set is the set of odd-cycle vertices.
   * There may be multiple solutions to show a graph's bipartiteness. This 
   * algorithm only returns 1 solution though.
   * Completes in O(n) time.
   */
  public static List<Set<String>> bicolorGraph(DirectedGraph graph, String startID) {
    List<Set<String>> result = new ArrayList<>();
    Set<String> redSet = new HashSet<>();
    Set<String> greenSet = new HashSet<>();
    Set<String> oddSet = new HashSet<>();
    result.add(redSet);
    result.add(greenSet);
    result.add(oddSet);
    
    Set<String> visited = new HashSet<>();
    Queue<String> startingNodes = new LinkedList<>(graph.getVertexIDs());
    
    // get our first starting vertex.
    String vertexID = startID;
    if(vertexID == null) {
      vertexID = startingNodes.remove();
    }
    
    while(!startingNodes.isEmpty()) {
      if(!visited.contains(vertexID)) {
        _bicolorPartial(graph, vertexID, visited, result);
      }
      
      // get our next starting vertex.
      vertexID = startingNodes.remove();
    }
    
    // We might have one vertex left over. Be sure to color it too.
    if(!visited.contains(vertexID)) {
      _bicolorPartial(graph, vertexID, visited, result);
    }
    
    return result;
  }
  
  
  /** Computes the bipartness of part of a graph by bfs. */
  private static void _bicolorPartial(DirectedGraph graph, String vertexID, Set<String> visited, List<Set<String>> result) {
    int RED = 0;
    int GREEN = 1;
    int ODD = -1;
    
    Set<String> redSet = result.get(0);
    Set<String> greenSet = result.get(1);
    Set<String> oddSet = result.get(2);
    
    Queue<String> bfsNodes = new LinkedList<>();
    Queue<Integer> bfsDest = new LinkedList<>();
    
    // Ready our bfs queues.
    bfsNodes.add(vertexID);
    bfsDest.add(RED);
    
    // Do a breadth-first search do compute the graph coloring.
    while(!bfsNodes.isEmpty()) {
      vertexID = bfsNodes.remove();
      int color = bfsDest.remove();
      
      // process the node.
      if(!visited.contains(vertexID)) {
        visited.add(vertexID);
        if(color == RED) {
          redSet.add(vertexID);
          for(String neighborID : graph.getNeighbors(vertexID)) {
            if(redSet.contains(neighborID)) {
              redSet.remove(neighborID);
              oddSet.add(neighborID);
            }
            else if(!greenSet.contains(neighborID)){
              bfsNodes.add(neighborID);
              bfsDest.add(GREEN);
            }
          }
        }
        else if(color == GREEN) {
          greenSet.add(vertexID);
          for(String neighborID : graph.getNeighbors(vertexID)) {
            if(greenSet.contains(neighborID)) {
              greenSet.remove(neighborID);
              oddSet.add(neighborID);
            }
            else if(!redSet.contains(neighborID)){
              bfsNodes.add(neighborID);
              bfsDest.add(RED);
            }
          }
        }
      }
    }
  }
  
  
  
  /** 
   * Returns true iff any component has cycles. 
   * Completes in O(n) time. 
   */
  public static boolean hasCycles(DirectedGraph graph, boolean isDirected) {
    if(isDirected) {
      // Nodes can be marked either 0 or 1. 
      // 0 means that the node's descending paths are being explored. 
      // 1 means that the node's descending paths have been completely 
      // explorered and found to contain no cycles.
      Map<String, Integer> mark = new HashMap<>();
      
      for(String firstID : graph.getVertexIDs()) {
        if(!mark.containsKey(firstID)) {
          if(_hasCyclesDirected(graph, firstID, mark)) {
            return true;
          }
        }
      }
      return false;
    }
    else {
      return _hasCyclesUndirected(graph);
    }
  }
  
  /** 
   * A directed graph has a cycle iff a depth-first search finds a back edge. 
   * This is a bit more complicated than detecting a cycle in an undireceted graph. 
   */
  private static boolean _hasCyclesDirected(DirectedGraph graph, String vertexID, Map<String, Integer> mark) {
    // Mark it 0 to show that we've visited the node, but we're still exploring 
    // its descending paths.
    mark.put(vertexID, 0);
    
    // explore the node's adjacency list.
    for(String nextID : graph.getEdges(vertexID)) {
      if(!mark.containsKey(nextID) && _hasCyclesDirected(graph, nextID, mark)) {
        return true;
      }
      else if(mark.get(nextID) == 0) {
        // We've revisited a node in a path currently being explored. 
        // This means we've encountered a back-edge and therefore encountered 
        // a cycle.
        return true;
      }
    }
    
    // We've completely explored the node's adjacency list without encountering
    // a back-edge. Mark it as 1.
    mark.put(vertexID, 1);
    return false;
  }
  
  private static boolean _hasCyclesUndirected(DirectedGraph graph) {
    Set<String> visited = new HashSet<>();
    
    for(String startID : graph.getVertexIDs()) {
      if(!visited.contains(startID)) {
        
        // do a depth first search from this node.
        Stack<String> dfs = new Stack<>();
        Stack<String> dfsPrev = new Stack<>();
        dfs.push(startID);
        dfsPrev.push(null);
        
        while(!dfs.isEmpty()) {
          String vertexID = dfs.pop();
          String prevID = dfsPrev.pop();
          
          if(visited.contains(vertexID)) {
            return true;
          }
          else {
            visited.add(vertexID);
            
            // traverse our edges (except to the node that we came from).
            for(String nextID : graph.getNeighbors(vertexID)) {
              if(!nextID.equals(prevID)) {
                dfs.push(nextID);
                dfsPrev.push(vertexID);
              }
            }
          }
        }
      }
    }
    return false;
  }
  
  
  /** 
   * Returns true iff no undirected cycles exist in any component of our graph 
   * and each vertex has at most 1 backward edge.
   * Completes in O(n) time.
   */
  public static boolean isTree(DirectedGraph graph) {
    if(graph.size() == 0 || _hasCyclesUndirected(graph)) {
      return false;
    }
    else {
      for(String vertexID : graph.getVertexIDs()) {
        if(graph.getBackwardEdges(vertexID).size() > 1) {
          return false;
        }
      }
      return true;
    }
  }
  
  
  
  /** 
   * Uses duplicate nodes to construct a tree representation of a graph that 
   * is not necessarily a tree.
   * Iff the graph is made of multiple components, then a forest of trees is produced.
   */
  public static DirectedGraph convertToTree(DirectedGraph graph) {
    Set<String> roots = findRoots(graph);
    int nodeNum = 0;
    
    // The graph could become several individual trees.
    DirectedGraph forest = new DirectedGraph();
    
    // Marks nodes as visited.
    // A node is marked 0 if its "subtree" is currently being explored.
    // A node is marked 1 if its "subtree" has been completely explored without encountering a cycle.
    // A node is marked 2 if it is encountered again in a cycle.
    Map<String, Integer> mark = new HashMap<>();
    
    // We'll pass an integer by reference to produce unique IDs for duplicate nodes.
    IntPointer dupID = new IntPointer(0);
    
    // Produce a tree for each possible root.
    for(String rootID : roots) {
      _convertToTree(forest, graph, rootID, null, mark, dupID);
    }
    
    return forest;
  }
  
  private static void _convertToTree(DirectedGraph forest, DirectedGraph graph, String vertexID, String prevID, Map<String, Integer> mark, IntPointer dupID) {
    mark.put(vertexID, 0);
    
    // copy the node into the tree.
    String treeVertexID;
    if(!forest.hasVertex(vertexID)) {
      treeVertexID = vertexID;
      forest.addVertex(treeVertexID, graph.getObject(vertexID));
    }
    else {
      treeVertexID = "dup;" + vertexID + ";" + dupID.value;
      forest.addVertex(treeVertexID, graph.getObject(vertexID));
      dupID.value++;
    }
    
    
    if(prevID != null) {
      forest.addEdge(prevID, treeVertexID);
    }
    
    for(String nextID : graph.getEdges(vertexID)) {
      if(!mark.containsKey(nextID) || mark.get(nextID) == 1) {
        // explore the "subtree".
        _convertToTree(forest, graph, nextID, treeVertexID, mark, dupID);
      }
      else if(mark.get(nextID) == 0 || mark.get(nextID) == 2) {
        // A cycle! Copy the cycle node, but don't explore its children.
        mark.put(nextID, 2);
        
        String cycleVertexID = "ref;" + nextID + ";" + dupID.value;
        forest.addVertex(cycleVertexID, cycleVertexID);
        dupID.value++;
        
        forest.addEdge(treeVertexID, cycleVertexID);
      }
    }
    
    // mark the path as safe.
    if(mark.get(vertexID) != 2) {
      mark.put(vertexID, 1);
    }
  }
  
  
  /** 
   * An object used to pass an integer by reference. 
   */
  private static class IntPointer {
    int value;
    
    public IntPointer(int value) {
      this.value = value;
    }
  }
}

