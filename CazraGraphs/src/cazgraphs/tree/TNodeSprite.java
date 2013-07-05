package cazgraphs.tree;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import pwnee.sprites.Sprite;


/** 
 * A sprite for an individual node in a TreeSprite.
 */
public class TNodeSprite extends Sprite {
  
  /** The tree this node belongs in. */
  public TreeSprite tree;
  
  /** The object stored in this node. */
  public Object object;
  
  /** The String representation of this node, if it is not renderable as an image. */
  public String label = "";
  
  /** The height for a line of text in this node's label. */
  public int lineHeight = 12;
  
  /** The parent of this node. Null if this is a root node. */
  public TNodeSprite parent = null;
  
  /** The list of this node's children. */
  public List<TNodeSprite> children = new ArrayList<>();
  
  /** Whether this node is compressed. A compressed node doesn't render its children. */
  public boolean compressed = false;
  
  /** 
   * Cached subtree height. 
   * This is updated any time that part of this node's subtree gets updated. 
   */
  public double subtreeHeight = -1;
  
  
  /** Used for smooth expansion animation. */
  protected double targetRelX = 0;
  
  /** Used for smooth expansion animation. */
  protected double targetRelY = 0;
  
  /** Used for smooth expansion animation. */
  protected double actualRelX = 0;
  
  /** Used for smooth expansion animation. */
  protected double actualRelY = 0;
  
  /** Used for smooth expansion animation. */
  protected double expandingScalar = 0.0;
  
  /** 
   * Creates a node storing some object and specifies whether or not to allow
   * this node to have children. 
   * @param tree    The TreeSprite this node belongs in.
   * @param object  The object stored in and rendered by this node.
   */
  public TNodeSprite(TreeSprite tree, Object object) { 
    super(0, 0);
    this.tree = tree;
    setObject(object);
  }
  
  
  //////// model logic
  
  /** Appends a node to this node's list of children. Child calls setParent. */
  public void add(TNodeSprite child) {
    children.add(child);
    child.setParent(this);
  }
  
  /** Adds a child node into this node's list of children at a particular index. */
  public void add(int index, TNodeSprite child) {
    children.add(index, child);
    child.setParent(this);
  }
  
  /** Sets this node's parent node. */
  public void setParent(TNodeSprite newParent) {
    this.parent = newParent;
  }
  
  /** Sets the object stored in this node. */
  public void setObject(Object object) {
    this.object = object;
    
    // compute the width/height of this node based on its object.
    updateView();
  }
  
  /** 
   * Updates the dimensions of this node, based on the expected dimensions of 
   * the stored object.
   */
  public void updateView() {
    if(object instanceof Image) {
      Image img = (Image) object;
      
      width = img.getWidth(null);
      height = img.getHeight(null);
      
      while(width < 0 || height < 0) {
        width = img.getWidth(null);
        height = img.getHeight(null);
      }
    }
    else {
      String str = object.toString();
      label = str;
      FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(tree.style.font);
      lineHeight = fm.getHeight();
      
      width = 0;
      height = 0;
      
      String[] lines = str.split("\n");
      for(String line : lines) {
        height += lineHeight + 1;
        int w = fm.stringWidth(line);
        
        if(w > width) {
          width = w;
        }
      }
    }
    
    width += tree.style.padding*2;
    height += tree.style.padding*2;
    
    subtreeHeight = -1;
  }
  
  /** True iff this node has no children. */
  public boolean isLeaf() {
    return children.isEmpty(); 
  }
  
  
  /** Returns the path from the root to this node. */
  public List<TNodeSprite> getPath() {
    List<TNodeSprite> path = new ArrayList<>();
    TNodeSprite prev = this;
    while(prev != null) {
      path.add(prev);
      prev = prev.parent;
    }
    Collections.reverse(path);
    return path;
  }
  
  
  /** Sets whether this node is compressed or not. This dirties the node's subtreeHeight. */
  public void setCompressed(boolean compress) {
    compressed = compress;
    subtreeHeight = -1;
    
    if(compressed) {
      for(TNodeSprite child : children) {
        child.setCompressed(true);
      }
    }
  }
  
  
  /** Returns the height of this node's currently expanded subtree. */
  public double getSubtreeHeight() {
    // If a cached subtreeHeight is unavailable, recompute it.
    if(subtreeHeight <= 0) {
      if(this.isLeaf() || this.compressed) {
        subtreeHeight = height;
      }
      else {
        subtreeHeight = 0;
        boolean first = true;
        for(TNodeSprite child : children) {
          if(first) {
            first = false;
          }
          else {
            subtreeHeight += tree.style.spacing;
          }
          
          subtreeHeight += child.getSubtreeHeight();
        }
      }
    }
    
    return subtreeHeight;
  }
  
  //////// mouse collision/bounding area logic
  
  /** 
   * If the mouse is over a node in this subtree, that node is returned. 
   * Else, null is returned. 
   */
  public TNodeSprite getMouseOver(Point2D screenP) {
    Point2D worldP = screen2Node(screenP);
    
    // Don't bother if the mouse is before this node. 
    // It will be before all the nodes in this subtree too.
    if(worldP.getX() < 0) {
      return null;
    }
    
    // Is the mouse over this node?
    else if(containsPoint(worldP)) {
      return this;
    }
    
    // If the mouse is above or below the subtree, don't bother.
    else if(worldP.getY() < 0-getSubtreeHeight()/2) {
      return null;
    }
    
    else if(worldP.getY() > getSubtreeHeight()/2) {
      return null;
    }
    
    // Is the mouse over one of this node's children?
    else {
      for(TNodeSprite child : children) {
        TNodeSprite result = child.getMouseOver(screenP);
        if(result != null) {
          return result;
        }
      }
      
      return null;
    }
  }
  
  /** 
   * True iff the given point (in coordinates relative to GamePanel's 
   * upper-left corner) is inside this node's bounding area. 
   */
  public boolean containsScreenPoint(Point2D screenP) {
    Point2D worldP = screen2Node(screenP);
    
    return containsPoint(worldP);
  }
  
  /** Converts a point from screen to node coordinates. */
  public Point2D screen2Node(Point2D screenP) {
    try {
      return curTrans.createInverse().transform(screenP, null);
    }
    catch(Exception e) {
      return screenP;
    }
  }
  
  /** 
   * True iff the given point (in coordinates using the same geometric 
   * system as this node) is inside this node's bounding area.
   */
  public boolean containsPoint(Point2D point) {
    return getBoundingArea().contains(point);
  }
  
  /**
   * Returns a Shape representing the bounding area of this node.
   * This default implementation returns a Rectangle2D. 
   */
  public Shape getBoundingArea() {
    return new Rectangle2D.Double(0, 0-height/2, width, height);
  }  
  
  
  /** Returns the world coordinates of this node. */
  public Point2D getWorldCoordinates() {
    double x = tree.x;
    double y = tree.y;
    
    // get the path from the root to this node.
    List<TNodeSprite> path = getPath();
    
    for(int i = 0; i < path.size(); i++) {
      TNodeSprite node = path.get(i);
      x += node.actualRelX;
      y += node.actualRelY;
      
      if(i < path.size() - 1) {
        x += node.width;
      }
    }
    
    return new Point2D.Double(x, y);
  }
  
  
  //////// converter logic
  
  /** Produces a swing TreeNode from this node. */
  public TreeNode toTreeNode() {
    return toTreeNodeRec(this);
  }
  
  /** Recursive helper for toTreeNode. */
  private MutableTreeNode toTreeNodeRec(TNodeSprite node) {
    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node.object);
    
    for(TNodeSprite child : node.children) {
      treeNode.add(toTreeNodeRec(child));
    }
    
    return treeNode;
  }
  
  
  //////// rendering logic 
  
  
  public void draw(Graphics2D g) {
    drawNode(g);
    
    if(compressed) {
      expandingScalar += (0-expandingScalar) * 0.05;
    }
    else {
      expandingScalar += (1-expandingScalar) * 0.05;
    }
    
    if(!compressed || expandingScalar > 0.01) {
      drawChildren(g);
    }
  }
  
  /** 
   * The default drawing implementation draws the object inside a 
   * rounded rectangle. 
   */
  public void drawNode(Graphics2D g) {
    AffineTransform origTrans = g.getTransform(); 
    
    // decide upon colors for this node.
    Color strokeColor = tree.style.strokeColor;
    Color fillColor = tree.style.fillColor;
    
    if(tree.selectedNode == this) {
      strokeColor = tree.style.selectedStrokeColor;
      fillColor = tree.style.selectedFillColor;
    }
    else if(this.isLeaf()) {
      fillColor = tree.style.leafFillColor;
    }
    
    // draw the box
    g.translate(0, 0-height/2);
    Shape box = new RoundRectangle2D.Double(0, 0, width, height, tree.style.padding, tree.style.padding);
    g.setColor(fillColor);
    g.fill(box);
    g.setColor(strokeColor);
    g.draw(box);
    
    // draw the stored object
    g.translate(tree.style.padding, tree.style.padding);
    drawObject(g);
    
    g.setTransform(origTrans);
  }
  
  
  /** 
   * Draws the stored object according to what type of object it is. 
   * The default implementation recognizes Images, and draws everything else 
   * as a String using its toString output.
   */
  public void drawObject(Graphics2D g) {
    if(object instanceof Image) {
      g.drawImage((Image) object, 0, 0, null);
    }
    else {
      g.setColor(tree.style.textColor);
      String[] lines = label.split("\n");
      for(int i =0; i < lines.length; i++) {
        String line = lines[i];
        g.drawString(line, 0, (lineHeight + 1) * (i+1) );
      }
    }
  }
  
  
  
  /** Draw this node's children and the edges to them. */
  public void drawChildren(Graphics2D g) {
    g.translate(width, 0);
    g.scale(expandingScalar, expandingScalar);
    
    double edgeWidth = getSubtreeHeight()*.25;
    double top = 0 - getSubtreeHeight()/2;
    
    for(TNodeSprite child : children) {
      child.targetRelX = edgeWidth;
      child.targetRelY = top + child.getSubtreeHeight()/2;
      
      child.actualRelX += (child.targetRelX - child.actualRelX)*.05; 
      child.actualRelY += (child.targetRelY - child.actualRelY)*.05; 
      
      double endX = child.actualRelX;
      double endY = child.actualRelY;
      
      // draw the edge.
      g.setColor(tree.style.strokeColor);
      CubicCurve2D curve = new CubicCurve2D.Double(0, 0, endX*0.25, 0, endX*.75, endY, endX, endY);
      g.draw(curve);
      
      // render the child.
      AffineTransform origTrans = g.getTransform();
      g.translate(endX, endY);
      child.render(g);
      g.setTransform(origTrans);
      
      top += child.getSubtreeHeight() + tree.style.spacing;
    }
  }
}

