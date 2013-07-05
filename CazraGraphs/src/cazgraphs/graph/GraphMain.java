package cazgraphs.graph;

import java.awt.*;
import javax.swing.*;

import pwnee.*;


/** The main window for this example. */
public class GraphMain extends JFrame {
    
    public static GraphMain instance;
    
    public GraphPanel graphPanel;
    
    public GraphMain() {
        super("Force-directed Graph visualization");
        int screenX = 1024;    
        int screenY = 800;
        this.setSize(screenX,screenY);
        
        // Set up the visualization's panel
        JPanel borderPanel = new JPanel(new BorderLayout());
        this.add(borderPanel);
        graphPanel = new GraphPanel(); 
        borderPanel.add(graphPanel, BorderLayout.CENTER);
        
        // finishing touches on Game window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        graphPanel.start();
        
        // Request focus so that we can poll keyboard input in our game.
        graphPanel.requestFocusInWindow();
    }
    
    /** 
     * Launches the singleton instance.
     */
    public static GraphMain launchGlobal() {
      instance = new GraphMain();
      return instance;
    }
    
    
    
    
    /** Launches the example. */
    public static void main(String[] args) {
        GraphMain window = GraphMain.launchGlobal();
    }

}