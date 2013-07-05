package cazgraphs.tree;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pwnee.Camera;
import pwnee.sprites.Sprite;

import cazgraphs.tree.style.*;

/** 
 * A sprite representation of a general tree data structure. 
 * This class has a root TNodeSprite representing the root element of the 
 * tree, and it is also meant to provide some general look & feel properties
 * for the visualization.
 */
public class TreeSprite extends Sprite {
  
  /** The root element in the tree. */
  public TNodeSprite root = null;
  
  /** 
   * The Camera used to compute the clipping region for this tree. 
   * If null, then clipping won't be applied.
   */
  public Camera camera;
  
  /** The general style information for tree. */
  public TreeStyle style = new TreeStyle();
  
  
  /** The currently selected node. Null if no node is selected. */
  public TNodeSprite selectedNode = null;
  
  /** The expansion distance of nodes from the selection path. */
  public int expansionDepth = 1;
  
  
  public TreeSprite(double x, double y) {
    super(x,y);
  }
  
  public TreeSprite() {
    this(0, 0);
  }
  
  
  //////// tree logic
  
  /** Sets the root node for this tree. */
  public void setRoot(TNodeSprite root) {
    this.root = root;
    root.tree = this;
  }
  
  /** Selects a node in this tree. This causes the tree's expansion state to change. */
  public void selectNode(TNodeSprite node) {
    selectedNode = node;
    
    // Create the list of nodes in the path from node to the root.
    List<TNodeSprite> path = new ArrayList<>();
    TNodeSprite prev = node;
    while(prev != null) {
      path.add(prev);
      prev = prev.parent;
    }
    
    // going from node to the root, change the expansion state of the tree.
    for(TNodeSprite cur : path) {
      expandSubtree(cur, prev, expansionDepth);
      prev = cur;
    }
  }
  
  /** Helper for selectNode. */
  private void expandSubtree(TNodeSprite subroot, TNodeSprite ignoreChild, int depth) {
    if(depth == 0) {
      subroot.setCompressed(true);
    }
    else {
      subroot.setCompressed(false);
      for(TNodeSprite child : subroot.children) {
        if(child != ignoreChild) {
          expandSubtree(child, null, depth - 1);
        }
      }
    }
  }
  
  
  /** Sets the expansion depth for the tree's visualization. */
  public void setExpansionDepth(int depth) {
    expansionDepth = depth;
    
    selectNode(selectedNode);
  }
  
  //////// interactivity logic
  
  /** If the mouse is over a node in this tree, that node is returned. Else, null is returned. */
  public TNodeSprite getMouseOver(Point2D mousePt) {
    return root.getMouseOver(mousePt);
  }
  
  
  //////// rendering logic
  
  public void setClippingCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void draw(Graphics2D g) {
    if(root != null) {
      root.render(g);
    }
  }
}
