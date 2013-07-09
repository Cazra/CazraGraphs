package cazgraphs.graph;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pwnee.GameMath;

import cazgraphs.graph.layout.*;
import cazgraphs.graph.style.*;

public class RandomGraphFactory {
  
  public static GraphSprite randomGraph(int numNodes, double connectivity, GraphLayout layout) {
    GraphSprite graph = new GraphSprite(true);
    graph.layoutAlgorithm = layout;
    
    // produce the nodes.
    int nodesMade = 0;
    Set<String> usedNames = new HashSet<>();
    for(int i = 0; i < numNodes; i++) {
      nodesMade++;
      
      String name = randomNodeName();
      if(usedNames.contains(name)) {
        name += nodesMade;
      }
      usedNames.add(name);
      GNodeSprite node = graph.addNode(name);
      
      // random style
      /*
      int shapeIndex = GameMath.rand.nextInt(3);
      if(shapeIndex == 1) {
        node.style = new RectangleNodeStyle();
      }
      else if(shapeIndex == 2) {
        node.style = new DiamondNodeStyle();
      }*/
      
      node.style = new EllipseNodeStyle();
    }
    
    // randomly produce the edges.
    for(GNodeSprite node : graph.nodes.values()) {
      for(GNodeSprite other : graph.nodes.values()) {
        if(node == other) {
          continue;
        }
        
        if(GameMath.rand.nextDouble() <= connectivity/numNodes) {
          graph.addEdge(node, other);
        }
      }
    }
    
    return graph;
  }
  
  
  /** Produces a random node name. */
  public static String randomNodeName() {
    List<String> names = new ArrayList<>();
    names.add("a node");
    names.add("my node");
    names.add("suddenly node");
    names.add("node basket");
    names.add("nodelicious");
    names.add("I am a node");
    names.add("killer node");
    names.add("hello node");
    names.add("my little node");
    names.add("lonely node");
    names.add("node again");
    names.add("warm node");
    names.add("sleepy node");
    names.add("fuzzy node");
    names.add("all of the node");
    names.add("senor node");
    names.add("node sensei");
    names.add("boss node");
    names.add("mini node");
    names.add("node around");
    names.add("node!");
    names.add("fiendish node");
    names.add("fishy node");
    names.add("nodes alive");
    names.add("I are node");
    names.add("botcy");
    names.add("cutie node");
    names.add("node forever");
    names.add("noderon");
    names.add("mr node");
    names.add("ms node");
    names.add("prof node");
    names.add("mi amore, node");
    names.add("node4sale");
    names.add("nodes r us");
    names.add("can you node?");
    names.add("cat planet!");
    
    return names.get(GameMath.rand.nextInt(names.size()));
  }
  
}
