package cazgraphs.tree;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import pwnee.*;
import pwnee.text.BlitteredFont;



/** 
 * The core Pwnee2D visualization component for the example.
 */
public class TreePanel extends GamePanel {
  
  /** The TreeSprite used by this example. */
  public TreeSprite treeSprite;
  
  /** The camera allows the user to pan and zoom the view. */
  public Camera camera;
  
  /** Whether the camera is currently following the selected node. */
  public boolean followSelected = false;
  
  public TreePanel() {
    super();
    
    // cache the sprites' images.
  //  TreeSprite.loadImages(this.imgLoader);
    
    reset();
    
    treeSprite = makeTestTree();
    treeSprite.camera = this.camera;
  }
  
  /** 
   * Resets the camera
   */
  public void reset() {
    // Create the camera
    initCamera();
  }
  
  private void initCamera() {
    camera = new Camera(this);
    camera.focalX = 320;
    camera.x = 0;
    camera.focalY = 240;
    camera.y = 0;
  }
  
  
  /** For testing purposes. */
  private TreeSprite makeTestTree() {
    TreeSprite tree = new TreeSprite();
    tree.setRoot(new TNodeSprite(tree, "root"));
    
      TNodeSprite n1 = new TNodeSprite(tree, "herp");
      tree.root.add(n1);
      
        TNodeSprite n11 = new TNodeSprite(tree, "Hello, I am a node.");
        n1.add(n11);
        
        TNodeSprite n12 = new TNodeSprite(tree, "I am also a node,\nbut I span\nseveral lines.");
        n1.add(n12);
      
      TNodeSprite n2 = new TNodeSprite(tree, "derp");
      tree.root.add(n2);
      
        TNodeSprite n21 = new TNodeSprite(tree, "testing...");
        n2.add(n21);
          
          TNodeSprite n210 = new TNodeSprite(tree, Toolkit.getDefaultToolkit().createImage("C:/Users/sl5/Pictures/derpy.png"));
          n21.add(n210);
          
          TNodeSprite n211 = new TNodeSprite(tree, "1");
          n21.add(n211);
          
          TNodeSprite n212 = new TNodeSprite(tree, "2");
          n21.add(n212);
          
            TNodeSprite n2121 = new TNodeSprite(tree, "aaaaaaaaaaa");
            n212.add(n2121);
            
            TNodeSprite n2122 = new TNodeSprite(tree, "bbbbb\nbbbbb\nbbbbb\nbbbbb\nbbbbb");
            n212.add(n2122);
            
            TNodeSprite n2123 = new TNodeSprite(tree, "c");
            n212.add(n2123);
          
          TNodeSprite n213 = new TNodeSprite(tree, "3");
          n21.add(n213);
          
    tree.selectNode(tree.root);
    
    return tree;
  }
  
  
  //////////////// Control logic
  
  public void logic() {
    // Camera controls for panning/zooming
    camera.update();
    if(mouse.justLeftPressed)
      camera.endDrag();
      
    if(mouse.isLeftPressed)
      camera.drag(mouse.position);
      
    if(mouse.justLeftClicked)
      camera.endDrag();
      
    if(mouse.wheel < 0)
      camera.zoomAtScreen(1.25, mouse.position);

    if(mouse.wheel > 0)
      camera.zoomAtScreen(0.75, mouse.position);
     
     
    // If we clicked a node, select it.  
    if(mouse.justLeftClicked) {
      followSelected = false;
      
      TNodeSprite mouseOverNode = treeSprite.getMouseOver(mouse.position);
      if(mouseOverNode != null) {
        followSelected = true;
        camera.moveCenter(camera.w2s(mouseOverNode.getWorldCoordinates()));
        
        treeSprite.selectNode(mouseOverNode);
      }
    }
    
    // stop following the selected node if there is mouse input.
    if(mouse.justLeftPressed || mouse.wheel < 0 || mouse.wheel > 0) {
      followSelected = false;
    }
    
    // After a node is selected, the camera will follow it.
    if(followSelected) {
      Point2D selectedPos = treeSprite.selectedNode.getWorldCoordinates();
      
      camera.x = selectedPos.getX();
      camera.y = selectedPos.getY();
    }
  }
  
  
  
  
  
  //////////////// Rendering
  
  public void paint(Graphics g) {
    Graphics2D g2D = (Graphics2D) g;
    
    // set the background color to white.
    this.setBackground(new Color(0xFFFFFF));
    
    // clear the panel with the background color.
    super.paint(g);
    
    // Save the original transform and then apply the camera's transform.
    AffineTransform origTrans = g2D.getTransform();
    g2D.setTransform(camera.trans);
    
    drawTestGrid(g2D);
    treeSprite.render(g2D);
    
    // HUD text
    
    // Set our drawing color
    g2D.setColor(new Color(0x777777));
    
    // restore our original transform
    g2D.setTransform(origTrans);
    
    // display the current frame rate.
    g2D.drawString("" + timer.fpsCounter, 10, 32);
    
    // display the mouse's world coordinates.
    Point2D mouseWorld = camera.screenToWorld(mouse.position);
    g2D.drawString("Mouse world coordinates: (" + mouseWorld.getX() + ", " + mouseWorld.getY() + ")", 10, 47);
  }
  
  
  /** 
   * Used to test drawing by drawing a red grid centered at the origin. 
   */
  private void drawTestGrid(Graphics2D g) {
    Color origColor = g.getColor();
    g.setColor(new Color(0xFFAAAA));
    
    // not really a radius since the grid is a square...
    int radius = 300;
    
    for(int i = 0; i <= radius; i+= 20) {
      g.drawLine(0-radius, i, radius, i);
      g.drawLine(0-radius, 0-i, radius, 0-i);
      g.drawLine(i, 0-radius, i, radius);
      g.drawLine(0-i, 0-radius, 0-i, radius);
    }
    
    g.setColor(new Color(0xAA0000));
    g.drawLine(0, 0-radius, 0, radius);
    g.drawLine(0-radius, 0, radius, 0);
    
    g.setColor(origColor);
  }
  
}
