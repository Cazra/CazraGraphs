package cazgraphs.graph;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Map;
import javax.swing.JOptionPane;

import pwnee.*;
import pwnee.text.BlitteredFont;

import cazgraphs.graph.layout.*;
import cazgraphs.graph.style.*;


/** 
 * The core Pwnee2D visualization component for the example.
 */
public class GraphPanel extends GamePanel {
  
  /** The graph. */
  public GraphSprite graph;
  
  /** The camera allows the user to pan and zoom the view. */
  public Camera camera;
  
  
  public GNodeSprite draggedNode;
  
  public GNodeSprite makeEdgeFrom;
  public GNodeSprite makeEdgeTo;
  
  public double dragNodeX = 0;
  public double dragNodeY = 0;
  
  
  public GraphPanel() {
    super();

    reset();
  }
  
  /** 
   * Resets the camera
   */
  public void reset() {
    // Create the camera
    initCamera();
    
    int numNodes = Integer.parseInt(JOptionPane.showInputDialog("# nodes:"));
    double connectivity = Double.parseDouble(JOptionPane.showInputDialog("% connected:"));
    graph = RandomGraphFactory.randomGraph(numNodes, connectivity, new ForceDirectedGraphLayout());
    
    graph.style = new AncestryGraphStyle();
  }
  
  private void initCamera() {
    camera = new Camera(this);
    camera.focalX = 320;
    camera.x = 0;
    camera.focalY = 240;
    camera.y = 0;
  }
  
  
  
  
  //////////////// Control logic
  
  public void logic() {
    camera.update();
    Point2D mouseWorld = camera.screenToWorld(mouse.position);
    

    // select and drag nodes.
    if(mouse.justLeftPressed) {
      GNodeSprite node = graph.getNodeAtPoint(mouse.position);
      graph.selectNode(node);
      draggedNode = node;
      if(node != null) {
        dragNodeX = node.x - mouseWorld.getX();
        dragNodeY = node.y - mouseWorld.getY();
      }
    }
    
    if(mouse.isLeftPressed && draggedNode != null) {
      draggedNode.x = mouseWorld.getX() + dragNodeX;
      draggedNode.y = mouseWorld.getY() + dragNodeY;
      graph.layoutAlgorithm.setFrozen(false);
    }
    
    if(mouse.justLeftClicked) {
      draggedNode = null;
    }
    
    graph.stepLayout();
    
    
    
    // Camera controls for panning/zooming
    if(mouse.justLeftPressed)
      camera.endDrag();
      
    if(mouse.isLeftPressed && draggedNode == null)
      camera.drag(mouse.position);
      
    if(mouse.justLeftClicked)
      camera.endDrag();
      
    if(mouse.wheel < 0)
      camera.zoomAtScreen(1.25, mouse.position);

    if(mouse.wheel > 0)
      camera.zoomAtScreen(0.75, mouse.position);
      
      
    if(keyboard.justPressed(KeyEvent.VK_F5)) {
      reset();
    }
    
    if(mouse.doubleClicked) {
      GNodeSprite node = graph.getNodeAtPoint(mouse.position);
      
      
      if(graph.style instanceof TopologyGraphStyle) {
        TopologyGraphStyle style = (TopologyGraphStyle) graph.style;
        style.setTopology(node);
        
        if(style.topology != null) {
          for(GNodeSprite sprite : graph.nodes.values()) {
            sprite.setVisible(style.topology.containsKey(sprite));
          } 
        }
        else {
          for(GNodeSprite sprite : graph.nodes.values()) {
            sprite.setVisible(true);
          } 
        }
      }
      else if(graph.style instanceof AncestryGraphStyle) {
        AncestryGraphStyle style = (AncestryGraphStyle) graph.style;
        style.setAncestry(node);
        
        if(style.descendants != null) {
          for(GNodeSprite sprite : graph.nodes.values()) {
            sprite.setVisible(style.descendants.containsKey(sprite) || style.ancestors.containsKey(sprite));
          } 
        }
        else {
          for(GNodeSprite sprite : graph.nodes.values()) {
            sprite.setVisible(true);
          } 
        }
      }
      
      
      
      if(node == null) {
        String name = RandomGraphFactory.randomNodeName() + graph.nodes.size();
      
        node = graph.addNode(name);
        node.x = mouseWorld.getX();
        node.y = mouseWorld.getY(); 
      }
    }
    
    if(mouse.justRightPressed) {
      makeEdgeFrom = graph.getNodeAtPoint(mouse.position);
    }
    if(mouse.justRightClicked) {
      makeEdgeTo = graph.getNodeAtPoint(mouse.position);
      
      if(makeEdgeFrom != null && makeEdgeTo != null) {
        makeEdgeFrom.addEdge(makeEdgeTo);
        makeEdgeFrom = null;
        makeEdgeTo = null;
      }
    }
    
    if(keyboard.justPressed(KeyEvent.VK_C)) {
      System.out.println("------------");
      for(Object component : graph.findComponents()) {
        System.out.println(component + "\n");
      }
    }
    
    if(keyboard.justPressed(KeyEvent.VK_R)) {
      System.out.println("------------");
      for(Object root : graph.findRoots()) {
        System.out.println(root + "\n");
      }
    }
  }
  
  
  
  
  
  //////////////// Rendering
  
  public void paint(Graphics g) {
    Graphics2D g2D = (Graphics2D) g;
    Point2D mouseWorld = camera.screenToWorld(mouse.position);
    
    // set the background color to white.
    this.setBackground(new Color(0xFFFFFF));
    
    // clear the panel with the background color.
    super.paint(g);
    
    // Save the original transform and then apply the camera's transform.
    AffineTransform origTrans = g2D.getTransform();
    g2D.setTransform(camera.trans);
    
  //  drawTestGrid(g2D);
    graph.render(g2D);
    
    if(makeEdgeFrom != null) {
      g.setColor(new Color(0xAA8855));
      Line2D line = new Line2D.Double(makeEdgeFrom.x, makeEdgeFrom.y, mouseWorld.getX(), mouseWorld.getY());
      g2D.draw(line);
    }
    
    // HUD text
    
    // Set our drawing color
    g2D.setColor(new Color(0x777777));
    
    // restore our original transform
    g2D.setTransform(origTrans);
    
    // display the current frame rate.
    g2D.drawString("" + timer.fpsCounter, 10, 32);
    
    // display the mouse's world coordinates.
  //  Point2D mouseWorld = camera.screenToWorld(mouse.position);
  //  g2D.drawString("Mouse world coordinates: (" + mouseWorld.getX() + ", " + mouseWorld.getY() + ")", 10, 47);
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
