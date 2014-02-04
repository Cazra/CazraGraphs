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
  
  public static GraphSprite randomGraph(int numNodes, double connectivity) {
    GraphSprite graph = new GraphSprite();
    
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
      VertexSprite node = graph.addVertex(name);
      
    }
    
    // randomly produce the edges.
    for(String from : graph.getVertexIDs()) {
      for(String to : graph.getVertexIDs()) {
        if(from.equals(to)) {
          continue;
        }
        
        if(GameMath.rand.nextDouble() <= connectivity/numNodes) {
          graph.addEdge(from, to);
        }
      }
    }
    
    return graph;
  }
  
  
  /** Produces a random node name. */
  public static String randomNodeName() {
    List<String> names = new ArrayList<>();
    names.add("a vertex");
    names.add("my vertex");
    names.add("suddenly vertex");
    names.add("vertex basket");
    names.add("vertexlicious");
    names.add("I am a vertex");
    names.add("killer vertex");
    names.add("hello vertex");
    names.add("my little vertex");
    names.add("lonely vertex");
    names.add("vertex again");
    names.add("warm vertex");
    names.add("sleepy vertex");
    names.add("fuzzy vertex");
    names.add("all of the vertex");
    names.add("senor vertex");
    names.add("vertex sensei");
    names.add("boss vertex");
    names.add("mini vertex");
    names.add("vertex around");
    names.add("vertex!");
    names.add("fiendish vertex");
    names.add("fishy vertex");
    names.add("vertexs alive");
    names.add("I are vertex");
    names.add("xetrev");
    names.add("cutie vertex");
    names.add("vertex forever");
    names.add("vertexron");
    names.add("mr vertex");
    names.add("ms vertex");
    names.add("prof vertex");
    names.add("mi amore, vertex");
    names.add("vertex4sale");
    names.add("vertexs r us");
    names.add("can you vertex?");
    names.add("cat planet!");
    
    return names.get(GameMath.rand.nextInt(names.size()));
  }
  
}
