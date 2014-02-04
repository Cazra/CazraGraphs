package cazgraphs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

import pwnee.*;
import pwnee.text.BlitteredFont;
import pwnee.text.Tooltip;

import cazgraphs.graph.*;
import cazgraphs.graph.layout.*;
import cazgraphs.graph.style.*;
import cazgraphs.io.CazGraphTextIO;


/** 
 * The core Pwnee2D visualization component for the example.
 * This panel is part of an application using the CazGraphs framework
 * that allows the user to construct graphs, lay them out in different ways,
 * apply different coloring styles to them, and also run many kinds of 
 * graph theory algorithms on them.
 *
 * This panel also serves as a sort of testing ground for new features to 
 * the framework.
 */
public class GraphMakerPanel extends GamePanel {
  
  /** The graph. */
  public GraphSprite graph;
  
  /** The camera allows the user to pan and zoom the view. */
  public Camera camera;
  
  
  public VertexSprite makeEdgeFrom;
  public VertexSprite makeEdgeTo;
  
  public boolean showingSelectRect = false;
  public double selectRectLeft = 0;
  public double selectRectTop = 0;
  
  public Tooltip tooltip = new Tooltip(new Color(0xCCCCAA), new Color(0xFFFFCC), new Color(0x222222), 1000);
  
  /** A mapping of nodes being dragged to their drag offsets. */
  public Map<VertexSprite, Point2D> draggedNodes = new HashMap<>();
  
  
  public GraphMakerPanel(String filepath) {
    super();
    reset();
    Debug.debugEnabled = true;
    
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
    graph = new GraphSprite();
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
    reset();
    
    try {
      graph = CazGraphTextIO.getInstance().loadFromFile(filepath);
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(this, "Could not read graph from file: " + filepath);
      e.printStackTrace();
    }
    graph.setLayout(new ForceDirectedGraphLayout());
  }
  
  
  public void setGraph(GraphSprite graph) {
    reset();
    this.graph = graph;
    graph.setLayout(new ForceDirectedGraphLayout());
  }
  
  
  
  /** 
   * Saves the current graph to the destination file path. 
   * The vertex and edge data in the output file are sorted alphabetically. 
   */
  public void saveToFile(String destPath) {
    try {
      CazGraphTextIO.getInstance().saveToFile(graph, destPath);
    }
    catch(Exception e) {
      JOptionPane.showMessageDialog(this, "Could not save to file: " + destPath);
      e.printStackTrace();
    }
  }
  
  
  //////////////// Control logic
  
  public void logic() {
    camera.update();
    Point2D mouseWorld = camera.screenToWorld(mouse.position);
    
    
    // mouse click interaction.
    VertexSprite node = graph.getNodeAtPoint(mouse.position);
    
    
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
    
    
    // node interaction
    if(node == null) {
      // Double-clicking in a blank area produces a new vertex. 
      if(mouse.doubleClicked) {
        String name = JOptionPane.showInputDialog("vertex name:");
        if(name != null && !graph.hasVertex(name)) {
          boolean wasPaused = graph.getLayout().isPaused();
          node = graph.addVertex(name);
          if(node != null) {
            node.x = mouseWorld.getX();
            node.y = mouseWorld.getY();
          }
          graph.getLayout().setPaused(wasPaused);
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
          graph.unselectAll();
        }
        
      }
    }
    else {
      // left-clicking does node selection.
      if(mouse.justLeftPressed) {
        // shift-click for multiple selection. 
        if(keyboard.isPressed(KeyEvent.VK_SHIFT)) {
          graph.selectVertex(node);
        }
        else if(!graph.selectedNodes.contains(node)) {
          graph.selectSingleVertex(node);
        }
        
        draggedNodes.clear();
        for(VertexSprite draggedNode : graph.selectedNodes) {
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
          makeEdgeFrom.addEdge(makeEdgeTo.getID());
          makeEdgeFrom = null;
          makeEdgeTo = null;
        }
      }
      
      // double-clicking does neat things depending on the current graph style/layout.
      if(mouse.doubleClicked) {
        if(graph.getStyle() instanceof TopologyGraphStyle) {
          TopologyGraphStyle style = (TopologyGraphStyle) graph.getStyle();
          style.setTopology(node);
        }
        else if(graph.getStyle() instanceof AncestryGraphStyle) {
          AncestryGraphStyle style = (AncestryGraphStyle) graph.getStyle();
          style.setAncestry(node);
        }
        else if(graph.getStyle() instanceof BipartiteGraphStyle) {
          BipartiteGraphStyle style = (BipartiteGraphStyle) graph.getStyle();
          style.computeBipartiteness(graph, node.getID());
          
          int hspace = 600;
          int vspace = 40;
          if(graph.getLayout() instanceof BipartiteGraphLayout) {
            BipartiteGraphLayout layout = (BipartiteGraphLayout) graph.getLayout();
            hspace = layout.hspacing;
            vspace = layout.vspacing;
          }
          
          graph.setLayout(new BipartiteGraphLayout(node.getID(), hspace, vspace));
        }
        else if(node.getChildren().size() > 0) {
          node.setExpanded(!node.isExpanded());
        }
      }
    }
    
    // Tooltips
    tooltip.updateComponent(node);
    
    // node dragging
    if(mouse.justLeftClicked) {
      draggedNodes.clear();
    }
    if(mouse.isLeftPressed) {
      
      for(VertexSprite draggedNode : draggedNodes.keySet()) {
        Point2D offset = draggedNodes.get(draggedNode);
        draggedNode.x = mouseWorld.getX() + offset.getX();
        draggedNode.y = mouseWorld.getY() + offset.getY();
      }
    }
    
    // selection rectangle
    if(mouse.justLeftClicked && showingSelectRect) {
      Rectangle2D rect = getSelectRect(mouseWorld);
      for(VertexSprite sprite : graph.getSprites()) {
        if(rect.contains(sprite.x, sprite.y)) {
          graph.selectVertex(sprite);
        }
      }
      showingSelectRect = false;
    }
    
    // Deleting nodes
    if(keyboard.justPressed(KeyEvent.VK_DELETE)) {
      // delete all currently selected nodes.
      for(VertexSprite sprite : graph.selectedNodes) {
        graph.removeVertex(sprite.getID());
      }
      graph.unselectAll();
    }
    
    // Pausing/Unpausing the layout algorithm
    if(keyboard.justPressed(KeyEvent.VK_P)) {
      graph.getLayout().setPaused(!graph.getLayout().isPaused());
    }
    
    
    graph.stepLayout();
    
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
    
    // Draw graphics from user interaction.
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
    
    tooltip.render(g2D);
    
    // HUD text
    
    // Set our drawing color
    g2D.setColor(new Color(0x777777));
    
    // restore our original transform
    g2D.setTransform(origTrans);
    
    // display the current frame rate.
  //  g2D.drawString("" + timer.fpsCounter, 10, 32);
    
    // display the mouse's world coordinates.
    g2D.drawString("Contains cycles? : " + graph.hasCycles(), 10, 47);
    g2D.drawString("Is a tree? : " + graph.isTree(), 10, 62);
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
