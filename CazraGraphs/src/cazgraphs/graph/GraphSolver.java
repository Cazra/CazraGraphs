package cazgraphs.graph;

import java.util.*;

/** 
 * Provides various static methods for solving various graph problems
 * with GraphSprites. 
 */
public class GraphSolver {
  
  /** Finds the connected components of a graph. Completes in O(n) time. */
  public static List<List<GNodeSprite>> findComponents(GraphSprite graph) {
    List<List<GNodeSprite>> components = new ArrayList<>();
    
    // keep track of which which nodes have already been visited, along with their 
    Set<GNodeSprite> visited = new HashSet<>();
    for(GNodeSprite node : graph.nodes.values()) {
      if(visited.contains(node)) {
        continue;
      }
      
      List<GNodeSprite> component = new ArrayList<>();
      
      // do a depth-first-search to populate the component.
      Stack<GNodeSprite> dfs = new Stack<>();
      dfs.push(node);
      while(!dfs.empty()) {
        GNodeSprite curNode = dfs.pop();
        if(!visited.contains(curNode)) {
          component.add(curNode);
          visited.add(curNode);
          
          for(GNodeSprite other : curNode.getNeighbors()) {
            dfs.push(other);
          }
        }
      }
      
      components.add(component);
    }
    
    return components;
  }
  
  
  
  /** 
   * Finds the roots of the connected components in a graph. 
   * If a component is a tree, then its source is returned.
   * Otherwise, it is not guaranteed what node will be returned for the component. 
   * Completes in O(n) time.
   */
  public static List<GNodeSprite> findRoots(GraphSprite graph) {
    List<GNodeSprite> roots = new ArrayList<>();
    
    List<List<GNodeSprite>> components = findComponents(graph);
    
    // Do a depth-first-search on each component to find the roots.
    for(List<GNodeSprite> component : components) {
      // Keep track of which which nodes have already been visited. 
      // Our root nodes will sort of just bubble up as we eliminate visited nodes.
      Set<GNodeSprite> visited = new HashSet<>();
      GNodeSprite root = null;
      
      for(GNodeSprite node : component) {
        if(visited.contains(node)) {
          continue;
        }
        root = node;
        
        Stack<GNodeSprite> dfs = new Stack<>();
        dfs.push(node);
        while(!dfs.empty()) {
          GNodeSprite curNode = dfs.pop();
          if(!visited.contains(curNode)) {
            visited.add(curNode);
            
            for(GNodeSprite other : curNode.getEdges()) {
              dfs.push(other);
            }
          }
        }
      }
      
      if(root != null) {
        roots.add(root);
      }
    }
    
    return roots;
  }
  
  
  /** Returns the list of nodes reachable from a particular node. Completes in O(n) time. */
  public static List<GNodeSprite> reachableNodes(GNodeSprite root) {  
    Set<GNodeSprite> visited = new HashSet<>();
    Stack<GNodeSprite> dfs = new Stack<>();
    
    dfs.push(root);
    while(!dfs.empty()) {
      GNodeSprite node = dfs.pop();
      
      if(!visited.contains(node)) {
        visited.add(node);
        
        for(GNodeSprite other : node.getEdges()) {
          dfs.push(other);
        }
      }
    }
    
    return new ArrayList<>(visited);
  }
  
  
  
  /** 
   * Calculates the topology of the graph starting from a single "top" node. 
   * This information is returned as a map from the nodes to their depths.
   * Nodes that aren't reachable from the top node have a depth of null, 
   * the top node has a depth of 0, and all other nodes have a depth equal to 
   * their distance from the top node. 
   * Completes in O(n) time.
   */
  public static Map<GNodeSprite, Integer> simpleTopology(GNodeSprite top) {
    Map<GNodeSprite, Integer> depths = new HashMap<>();
    
    Set<GNodeSprite> visited = new HashSet<>();
    Queue<GNodeSprite> bfsNodes = new LinkedList<>();
    Queue<Integer> bfsDepths = new LinkedList<>();
    
    bfsNodes.add(top);
    bfsDepths.add(0);
    while(!bfsNodes.isEmpty()) {
      GNodeSprite node = bfsNodes.remove();
      int depth = bfsDepths.remove();
      
      if(visited.contains(node)) {
        continue;
      }
      visited.add(node);
      depths.put(node, depth);
      
      for(GNodeSprite other : node.getEdges()) {
        bfsNodes.add(other);
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
  public static Map<GNodeSprite, Integer> simpleReverseTopology(GNodeSprite top) {
    Map<GNodeSprite, Integer> depths = new HashMap<>();
    
    Set<GNodeSprite> visited = new HashSet<>();
    Queue<GNodeSprite> bfsNodes = new LinkedList<>();
    Queue<Integer> bfsDepths = new LinkedList<>();
    
    bfsNodes.add(top);
    bfsDepths.add(0);
    while(!bfsNodes.isEmpty()) {
      GNodeSprite node = bfsNodes.remove();
      int depth = bfsDepths.remove();
      
      if(visited.contains(node)) {
        continue;
      }
      visited.add(node);
      depths.put(node, depth);
      
      for(GNodeSprite other : node.getFromEdges()) {
        bfsNodes.add(other);
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
  public static List<Set<GNodeSprite>> bicolorGraph(GraphSprite graph, String startNodeID) {
    List<Set<GNodeSprite>> result = new ArrayList<>();
    Set<GNodeSprite> redSet = new HashSet<>();
    Set<GNodeSprite> greenSet = new HashSet<>();
    Set<GNodeSprite> oddSet = new HashSet<>();
    result.add(redSet);
    result.add(greenSet);
    result.add(oddSet);
    
    Set<GNodeSprite> visited = new HashSet<>();
    Queue<GNodeSprite> startingNodes = new LinkedList<>(graph.nodes.values());
    
    // get our first starting node.
    GNodeSprite node = graph.getNode(startNodeID);
    if(node == null) {
      node = startingNodes.remove();
    }
    
    while(!startingNodes.isEmpty()) {
      if(!visited.contains(node)) {
        bicolorPartial(graph, node, visited, result);
      }
      
      // get our next starting node.
      node = startingNodes.remove();
    }
    
    // We might have one node left over. Be sure to color it too.
    if(!visited.contains(node)) {
      bicolorPartial(graph, node, visited, result);
    }
    
    return result;
  }
  
  
  /** Computes the bipartness of part of a graph by bfs. */
  private static void bicolorPartial(GraphSprite graph, GNodeSprite node, Set<GNodeSprite> visited, List<Set<GNodeSprite>> result) {
    int RED = 0;
    int GREEN = 1;
    int ODD = -1;
    
    Set<GNodeSprite> redSet = result.get(0);
    Set<GNodeSprite> greenSet = result.get(1);
    Set<GNodeSprite> oddSet = result.get(2);
    
    Queue<GNodeSprite> bfsNodes = new LinkedList<>();
    Queue<Integer> bfsDest = new LinkedList<>();
    
    // Ready our bfs queues.
    bfsNodes.add(node);
    bfsDest.add(RED);
    
    // Do a breadth-first search do compute the graph coloring.
    while(!bfsNodes.isEmpty()) {
      node = bfsNodes.remove();
      int color = bfsDest.remove();
      
      // process the node.
      if(!visited.contains(node)) {
        visited.add(node);
        if(color == RED) {
          redSet.add(node);
          for(GNodeSprite toNode : node.getNeighbors()) {
            if(redSet.contains(toNode)) {
              redSet.remove(toNode);
              oddSet.add(toNode);
            }
            else if(!greenSet.contains(toNode)){
              bfsNodes.add(toNode);
              bfsDest.add(GREEN);
            }
          }
        }
        else if(color == GREEN) {
          greenSet.add(node);
          for(GNodeSprite toNode : node.getNeighbors()) {
            if(greenSet.contains(toNode)) {
              greenSet.remove(toNode);
              oddSet.add(toNode);
            }
            else if(!redSet.contains(toNode)){
              bfsNodes.add(toNode);
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
  public static boolean hasCycles(GraphSprite graph) {
    if(graph.isDirected) {
      // Nodes can be marked either 0 or 1. 
      // 0 means that the node's descending paths are being explored. 
      // 1 means that the node's descending paths have been completely 
      // explorered and found to contain no cycles.
      Map<GNodeSprite, Integer> mark = new HashMap<>();
      
      for(GNodeSprite first : graph.nodes.values()) {
        if(!mark.containsKey(first)) {
          if(_hasCyclesDirected(first, mark)) {
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
  private static boolean _hasCyclesDirected(GNodeSprite cur, Map<GNodeSprite, Integer> mark) {
    // Mark it 0 to show that we've visited the node, but we're still exploring 
    // its descending paths.
    mark.put(cur, 0);
    
    // explore the node's adjacency list.
    for(GNodeSprite next : cur.getEdges()) {
      if(!mark.containsKey(next) && _hasCyclesDirected(next, mark)) {
        return true;
      }
      else if(mark.get(next) == 0) {
        // We've revisited a node in a path currently being explored. 
        // This means we've encountered a back-edge and therefore encountered 
        // a cycle.
        return true;
      }
    }
    
    // We've completely explored the node's adjacency list without encountering
    // a back-edge. Mark it as 1.
    mark.put(cur, 1);
    return false;
  }
  
  private static boolean _hasCyclesUndirected(GraphSprite graph) {
    Set<GNodeSprite> visited = new HashSet<>();
    
    for(GNodeSprite source : graph.nodes.values()) {
      if(!visited.contains(source)) {
        
        // do a depth first search from this node.
        Stack<GNodeSprite> dfs = new Stack<>();
        Stack<GNodeSprite> dfsPrev = new Stack<>();
        dfs.push(source);
        dfsPrev.push(new GNodeSprite(null, "dummy"));
        
        while(!dfs.isEmpty()) {
          GNodeSprite cur = dfs.pop();
          GNodeSprite prev = dfsPrev.pop();
          
          if(visited.contains(cur)) {
            return true;
          }
          else {
            visited.add(cur);
            
            // traverse our edges (except to the node that we came from).
            for(GNodeSprite next : cur.getNeighbors()) {
              if(next != prev) {
                dfs.push(next);
                dfsPrev.push(cur);
              }
            }
          }
        }
      }
    }
    return false;
  }
  
  
  /** 
   * Returns true iff no cycles exist in any component of our graph 
   * if we treat it as an undirected graph. 
   * Completes in O(n) time.
   */
  public static boolean isTree(GraphSprite graph) {
    return !_hasCyclesUndirected(graph);
  }
}

