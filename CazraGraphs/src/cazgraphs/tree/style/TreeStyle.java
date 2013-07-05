package cazgraphs.tree.style;

import java.awt.*;


/** 
 * Provides colors and font metrics to be used by nodes in a GraphSprite. 
 */
public class TreeStyle {
  
  /** The font used by nodes of this tree. */
  public Font font = new Font("Lucida Console", java.awt.Font.PLAIN, 12);
  
  /** Default text color */
  public Color textColor = new Color(0x000000);
  
  
  /** The amount of spacing (in pixels) between lines of text in nodes' labels. */
  public int lineSpacing = 1;
  
  /** Amount of padding between a node's border and its rendered object. */
  public int padding = 10;
  
  /** The amount of vertical spacing between sibling nodes. */
  public int spacing = 10;
  
  /** The maximum content width of a node. */
  public int maxNodeWidth = 300;
  
  
  /** Default stroke color */
  public Color strokeColor = new Color(0x5555aa);
  
  /** Default fill color */
  public Color fillColor = new Color(0xddddff);
  
  /** Selected stroke color */
  public Color selectedStrokeColor = new Color(0xaaaa55);
  
  /** Selected fill color */
  public Color selectedFillColor = new Color(0xffffcc);
  
  /** Leaf fill color */
  public Color leafFillColor = new Color(0xf8f8ff);
  
  /** The default color for edges. */
  public Color edgeColor = new Color(0x5555aa);
  
  
  /** The line thickness of edges in the tree. */
  public int edgeThickness = 2;
}
