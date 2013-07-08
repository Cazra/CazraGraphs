package cazgraphs.graph;

import java.util.*;

/** 
 * Provides various static methods for solving various graph problems
 * with GraphSprites. 
 */
public class GraphSolver {
  
  /** Finds the connected components of a graph. */
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
  
  
  
  /** Finds the roots of the connected components in a graph. */
  public static List<GNodeSprite> findRoots(GraphSprite graph) {
    List<GNodeSprite> roots = new ArrayList<>();
    
    List<List<GNodeSprite>> components = findComponents(graph);
    
    // Do a depth-first-search on each component to find the roots.
    for(List<GNodeSprite> component : components) {
      // keep track of which which nodes have already been visited, along with their 
      Set<GNodeSprite> visited = new HashSet<>();
      GNodeSprite root = null;
      
      for(GNodeSprite node : new ArrayList<GNodeSprite>(component)) {
        if(visited.contains(node)) {
          continue;
        }
        root = node;
        
        Stack<GNodeSprite> dfs = new Stack<>();
        dfs.push(node);
        while(!dfs.empty()) {
          GNodeSprite curNode = dfs.pop();
          if(!visited.contains(curNode)) {
            component.add(curNode);
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
  
  
  /** Returns the list of nodes reachable from a particular node. */
  public static List<GNodeSprite> reachableNodes(GNodeSprite root) {
    List<GNodeSprite> result = new ArrayList<>();
  
    Set<GNodeSprite> visited = new HashSet<>();
    Stack<GNodeSprite> dfs = new Stack<>();
    
    dfs.push(root);
    while(!dfs.empty()) {
      GNodeSprite node = dfs.pop();
      
      if(!visited.contains(node)) {
        result.add(node);
        visited.add(node);
        
        for(GNodeSprite other : node.getEdges()) {
          dfs.push(other);
        }
      }
    }
    
    return result;
  }
  
  
  
  /** 
   * Calculates the topology of the graph starting from a single "top" node. 
   * This information is returned as a map from the nodes to their depths.
   * Nodes that aren't reachable from the top node have a depth of null, 
   * the top node has a depth of 0, and all other nodes have a depth equal to 
   * their distance from the top node. 
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
  
}
