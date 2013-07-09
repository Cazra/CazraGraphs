package cazgraphs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

import pwnee.*;
import pwnee.text.BlitteredFont;

import cazgraphs.graph.*;
import cazgraphs.graph.layout.*;
import cazgraphs.graph.style.*;


/** 
 * The core Pwnee2D visualization component for the example.
 */
public class GraphMakerPanel extends GamePanel {
  
  /** The graph. */
  public GraphSprite graph;
  
  /** The camera allows the user to pan and zoom the view. */
  public Camera camera;
  
  
  public GNodeSprite makeEdgeFrom;
  public GNodeSprite makeEdgeTo;
  
  public boolean showingSelectRect = false;
  public double selectRectLeft = 0;
  public double selectRectTop = 0;
  
  /** A mapping of nodes being dragged to their drag offsets. */
  public Map<GNodeSprite, Point2D> draggedNodes = new HashMap<>();
  
  
  public GraphMakerPanel(String filepath) {
    super();
    reset();
    
    if(filepath != null) {
      loadFromFile(filepath);
    }
  }
  
  /** 
   * Resets the camera
   */
  public void reset() {
    // Create the camera
    initCamera();
    graph = new GraphSprite(true);
  }
  
  private void initCamera() {
    camera = new Camera(this);
    camera.focalX = 0;
    camera.x = 0;
    camera.focalY = 0;
    camera.y = 0;
  }
  
  
  
  //////// File io
  
  public void loadFromFile(String filepath) {
    graph.layoutAlgorithm = new NGonGraphLayout();
    try {
      BufferedReader br = new BufferedReader(new FileReader(filepath));
      String input = "";
      String line = br.readLine();
      while(line != null) {
        input += line + "\n";
        line = br.readLine();
      }
      
      System.out.println(input);
      
      String[] sections = input.split("--");
      
      // vertices
      System.out.println("vertices: ");
      String[] vertices = sections[2].split("\n");
      for(String vertex : vertices) {
        System.out.println(vertex);
        if(!vertex.equals("")) {
          graph.addNode(vertex.trim());
        }
      }
      
      graph.stepLayout();
      for(GNodeSprite node : graph.nodes.values()) {
        node.x /= 80;
        node.y /= 80;
        node.x += 320;
        node.y += 240;
      }
      
      // edges
      System.out.println("edges: ");
      String[] edges = sections[4].split("\n");
      for(String edge : edges) {
        System.out.println(edge);
        if(!edge.equals("")) {
          String[] ends = edge.split("->");
          String source = ends[0].trim();
          for(String target : ends[1].split(",")) {
            target = target.trim();
            graph.addEdge(source, target);
          }
        }
      }
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(this, "Could not read graph from file: " + filepath);
      e.printStackTrace();
    }
    graph.layoutAlgorithm = new DefaultGraphLayout();
  }
  
  
  
  
  
  //////////////// Control logic
  
  public void logic() {
    camera.update();
    Point2D mouseWorld = camera.screenToWorld(mouse.position);
    
    
    // mouse click interaction.
    GNodeSprite node = graph.getNodeAtPoint(mouse.position);
    
    
    // Camera controls for panning/zooming
    if(mouse.justLeftPressed)
      camera.endDrag();
      
    if(mouse.isLeftPressed && node == null && draggedNodes.isEmpty() && !keyboard.isPressed(KeyEvent.VK_SHIFT) && !showingSelectRect)
      camera.drag(mouse.position);
      
    if(mouse.justLeftClicked) {
      camera.endDrag();
    }
      
    if(mouse.wheel < 0)
      camera.zoomAtScreen(1.25, mouse.position);

    if(mouse.wheel > 0)
      camera.zoomAtScreen(0.75, mouse.position);
      
      
    if(keyboard.justPressed(KeyEvent.VK_F5)) {
      reset();
    }
    
    
    // node interaction
    if(node == null) {
      // Double-clicking in a blank area produces a new vertex. 
      if(mouse.doubleClicked) {
        String name = JOptionPane.showInputDialog("vertex name:");
        if(!graph.nodes.containsKey(name)) {
          node = graph.addNode(name);
          if(node != null) {
            node.x = mouseWorld.getX();
            node.y = mouseWorld.getY();
          }
        }
      }
      
      if(mouse.justLeftPressed) {
        
        // Start a selection rectangle.
        if(keyboard.isPressed(KeyEvent.VK_SHIFT)) {
          showingSelectRect = true;
          selectRectLeft = mouseWorld.getX();
          selectRectTop = mouseWorld.getY();
        }
        
        // unselect the nodes. 
        else {
          graph.selectNode(null);
        }
        
      }
    }
    else {
      // left-clicking does node selection.
      if(mouse.justLeftPressed) {
        // shift-click for multiple selection. 
        if(keyboard.isPressed(KeyEvent.VK_SHIFT)) {
          graph.selectNode(node);
        }
        else if(!graph.selectedNodes.contains(node)) {
          graph.selectSingleNode(node);
        }
        
        draggedNodes.clear();
        for(GNodeSprite draggedNode : graph.selectedNodes) {
          double dragNodeX = draggedNode.x - mouseWorld.getX();
          double dragNodeY = draggedNode.y - mouseWorld.getY();
          
          draggedNodes.put(draggedNode, new Point2D.Double(dragNodeX, dragNodeY));
        }
      }
      
      // Dragging the right mouse button creates an edge from one node to another. 
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
    }
    
    // node dragging
    if(mouse.justLeftClicked) {
      draggedNodes.clear();
    }
    if(mouse.isLeftPressed) {
      
      for(GNodeSprite draggedNode : draggedNodes.keySet()) {
        Point2D offset = draggedNodes.get(draggedNode);
        draggedNode.x = mouseWorld.getX() + offset.getX();
        draggedNode.y = mouseWorld.getY() + offset.getY();
      }
    }
    
    // selection rectangle
    if(mouse.justLeftClicked && showingSelectRect) {
      Rectangle2D rect = getSelectRect(mouseWorld);
      for(GNodeSprite sprite : graph.nodes.values()) {
        if(rect.contains(sprite.x, sprite.y)) {
          graph.selectNode(sprite);
        }
      }
      showingSelectRect = false;
    }
    
    
    if(keyboard.justPressed(KeyEvent.VK_B)) {
      BipartiteGraphStyle bStyle = new BipartiteGraphStyle();
      graph.style = bStyle;
      
      String startNodeID = null;
      if(graph.selectedNode != null) {
        startNodeID = graph.selectedNode.id;
      }
      
      bStyle.computeBipartiteness(graph, startNodeID);
      graph.layoutAlgorithm = new BipartiteGraphLayout(startNodeID, 600, 40);
    }
    
    
    
    graph.layoutAlgorithm.stepLayout(graph);
    
  }
  
  
  
  public Rectangle2D getSelectRect(Point2D mouseWorld) {
    double x = Math.min(selectRectLeft, mouseWorld.getX());
    double y = Math.min(selectRectTop, mouseWorld.getY());
    double w = Math.abs(selectRectLeft - mouseWorld.getX());
    double h = Math.abs(selectRectTop - mouseWorld.getY());
    
    return new Rectangle2D.Double(x, y, w, h);
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
    
    drawOrigin(g2D);
    graph.render(g2D);
    
    if(makeEdgeFrom != null) {
      g.setColor(new Color(0xAA8855));
      Line2D line = new Line2D.Double(makeEdgeFrom.x, makeEdgeFrom.y, mouseWorld.getX(), mouseWorld.getY());
      g2D.draw(line);
    }
    
    if(showingSelectRect) {
      g.setColor(new Color(0xAAFFFF));
      Rectangle2D rect = getSelectRect(mouseWorld);
      g2D.draw(rect);
    }
    
    
    // HUD text
    
    // Set our drawing color
    g2D.setColor(new Color(0x777777));
    
    // restore our original transform
    g2D.setTransform(origTrans);
    
    // display the current frame rate.
  //  g2D.drawString("" + timer.fpsCounter, 10, 32);
    
    // display the mouse's world coordinates.
  //  Point2D mouseWorld = camera.screenToWorld(mouse.position);
  //  g2D.drawString("Mouse world coordinates: (" + mouseWorld.getX() + ", " + mouseWorld.getY() + ")", 10, 47);
  }
  
  
  /** 
   * Used to test drawing by drawing a red grid centered at the origin. 
   */
  private void drawOrigin(Graphics2D g) {
    Color origColor = g.getColor();
    g.setColor(new Color(0xFFAAAA));
    
    // not really a radius since the grid is a square...
    int radius = 32;
    
    g.setColor(new Color(0xAA0000));
    g.drawLine(0, 0-radius, 0, radius);
    g.drawLine(0-radius, 0, radius, 0);
    
    g.setColor(origColor);
  }
  
}
