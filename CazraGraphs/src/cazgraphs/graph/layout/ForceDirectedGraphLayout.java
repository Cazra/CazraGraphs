package cazgraphs.graph.layout;

import java.awt.*;
import java.awt.geom.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pwnee.*;

import cazgraphs.graph.*;



/** 
 * A layout that simulates physical forces. Vertices are simulated as 
 * like-charged particles and edges are simulated as springs.
 */
public class ForceDirectedGraphLayout extends GraphLayout {
  /** 
   * The energy threshold (sum of absolute values of the 
   * nodes' velocity components) for the graph under which the 
   * force-directed layout becomes inactive.
   */
  public double THRESHOLD = 0;
  
  /** The repelling anti-gravity-like constant for the force-directed layout. */
  public double ANTIGRAV = 2000;
  
  /** The spring constant for neighbors in the force-directed layout. */
  public double NSPRING = 0.001;
  
  /** The spring constant for nodes being attracted to the origin. */
  public double OSPRING = 0.0001;
  
  /** The dampening constant for the node velocities. */
  public double DAMP = 0.5;
  
  /** 
   * The speed of the force-directed layout. 
   * A higher value will make the layout settle more quickly.
   */
  public double SPEED = 100;
  
  /** Used for scattering overlapping nodes that are at the exact same position. */
  public double scatterAngle = 0;
  
  public double MAXVELOCITY = 50;
  
  
  private int visibleSize = 0;
  
  
  public ForceDirectedGraphLayout() {
  }
  
  
  /** 
   * Performs one step through the layout algorithm.
   * The default implementation uses a force-directed layout.
   * Running this on each frame displays a nice (but sometimes chaotic) 
   * animation!
   */
  public void stepLayout(GraphSprite graph) {
    if(isFrozen()) {
      return;
    }
    visibleSize = 0;
    for(VertexSprite vertex : graph.getSprites()) {
      if(vertex.isActive()) {
        visibleSize++;
      }
    }
    
    // Do physics!
    applyLayoutForces(graph);
    
    // Move the nodes and dampen their velocity.
    moveNodes(graph);
    dampenNodes(graph);
    
    // If the energy drops below our threshold, deactivate the layout algorithm
    // to save computation time.
    double energy = getGraphEnergy(graph);
    if(energy < THRESHOLD) {
      setFrozen(true);
    }
  }
  
  
  public void moveNodes(GraphSprite graph) {
    for(VertexSprite vertex : graph.getSprites()) {
      if(!vertex.isActive()) {
        continue;
      }
      
      vertex.x += vertex.dx;
      vertex.y += vertex.dy;
    }
  }
  
  /** Dampens the velocity of the vertices as if they are moving through a viscous fluid. */
  public void dampenNodes(GraphSprite graph) {
    for(VertexSprite vertex : graph.getSprites()) {
      vertex.dx *= DAMP;
      vertex.dy *= DAMP;
    }
  }
  
  
  
  /** 
   * Applies all relevant forces for the layout to influence the velocities 
   * of the nodes.
   * Subclasses can override this method to extend its functionality
   */
  public void applyLayoutForces(GraphSprite graph) {
    // All the nodes repel each other like same-charged particles. 
    repelNodes(graph);
    
    // Neighbor nodes are attracted to each other by a spring force. 
    attractNeighbors(graph);
    
    // Nodes are attracted to the origin by a spring force.
    attractOrigin(graph);
  }
  
  
  /** Applies forces to cause all nodes to repel each other. */
  public void repelNodes(GraphSprite graph) {
    Collection<VertexSprite> vertices = graph.getSprites();
    double antigrav = ANTIGRAV + 100*visibleSize;
    
    for(VertexSprite vertex : vertices) {
      if(!vertex.isActive()) {
        continue;
      }
      for(VertexSprite other : vertices) {
        if(vertex == other || !other.isActive()) {
          continue;
        }
        
        // Scatter the nodes if they occupy the same point.
        if(vertex.x == other.x && vertex.y == other.y) {
          vertex.x += 1*GameMath.cos(scatterAngle);
          vertex.y += 1*GameMath.sin(scatterAngle);
          scatterAngle += 31;
        }
        
        double dist2 = Math.max(32*32, GameMath.distSq(vertex.x, vertex.y, other.x, other.y));
        double dist = Math.sqrt(dist2);
        
        double nodeAccel = -1 * antigrav*SPEED * other.mass / dist2;
        double xUnit = (other.x - vertex.x)/dist;
        double yUnit = (other.y - vertex.y)/dist;
        
        vertex.dx += nodeAccel * xUnit;
        vertex.dy += nodeAccel * yUnit;
      }
    }
  }
  
  
  
  /** Applies forces to cause nodes to be attracted to their neighbors by spring forces. */
  public void attractNeighbors(GraphSprite graph) {
    for(VertexSprite vertex : graph.getSprites()) {
      if(!vertex.isActive()) {
        continue;
      }
      
      // Get the nodes set of neighbors.
      Set<String> neighbors = vertex.getNeighbors();
      
      // attract!
      for(String otherID : neighbors) {
        VertexSprite other = graph.getSprite(otherID);
        if(vertex == other || !other.isActive()) {
          continue;
        }
        
        // It is important that the edge applies equal force to both of its end nodes!
        double springForce = NSPRING/Math.max(1, Math.max(neighbors.size(), other.getNeighbors().size()));
        
        double dist = Math.max(1,GameMath.dist(vertex.x, vertex.y, other.x, other.y));
        
        double nodeAccel = springForce*SPEED * dist / vertex.mass;
        double xUnit = (other.x - vertex.x)/dist;
        double yUnit = (other.y - vertex.y)/dist;
        
        vertex.dx += nodeAccel * xUnit;
        vertex.dy += nodeAccel * yUnit;
      }
    }
  }
  
  
  
  /** Applies forces to cause all nodes to be attracted to the graph's origin. */
  public void attractOrigin(GraphSprite graph) {
    for(VertexSprite vertex : graph.getSprites()) {
      if(!vertex.isActive()) {
        continue;
      }
      
      double dist = Math.max(1, GameMath.dist(vertex.x, vertex.y, 0, 0));
        
      double nodeAccel = OSPRING*SPEED * dist;
      double xUnit = (0 - vertex.x)/dist;
      double yUnit = (0 - vertex.y)/dist;
      
      vertex.dx += nodeAccel * xUnit;
      vertex.dy += nodeAccel * yUnit;
    }
  }
  
  
  /** Computes the "energy" of the graph. Here, this is the sum of the magnitude of all its nodes' velocities. */
  public double getGraphEnergy(GraphSprite graph) {
    double energy = 0;
    for(VertexSprite vertex : graph.getSprites()) {
      if(!vertex.isActive()) {
        continue;
      }
      
      double nodeE = Math.sqrt(vertex.dx*vertex.dx + vertex.dy*vertex.dy);
      energy += nodeE / visibleSize;
    }
    return energy;
  }
}
