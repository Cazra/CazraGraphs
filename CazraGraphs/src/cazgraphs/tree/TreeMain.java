package cazgraphs.tree;

import java.awt.*;
import javax.swing.*;

import pwnee.*;


/** The main window for this example. */
public class TreeMain extends JFrame {
    
    public static TreeMain instance;
    
    public TreePanel treePanel;
    
    public TreeMain() {
        super("Expandable Tree Visualization");
        int screenX = 1024;    
        int screenY = 800;
        this.setSize(screenX,screenY);
        
        // Set up the visualization's panel
        JPanel borderPanel = new JPanel(new BorderLayout());
        this.add(borderPanel);
        treePanel = new TreePanel(); 
        borderPanel.add(treePanel, BorderLayout.CENTER);
        
        // finishing touches on Game window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        treePanel.start();
        
        // Request focus so that we can poll keyboard input in our game.
        treePanel.requestFocusInWindow();
    }
    
    /** 
     * Launches the static instance of this window. 
     * This *might* not be good software engineering, but it's convenient.
     */
    public static TreeMain launchGlobal() {
      instance = new TreeMain();
      return instance;
    }
    
    
    
    
    /** Launches Feral. */
    public static void main(String[] args) {
        TreeMain window = TreeMain.launchGlobal();
    }

}