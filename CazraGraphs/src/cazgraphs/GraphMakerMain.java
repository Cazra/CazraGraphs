package cazgraphs;

import java.awt.*;
import javax.swing.*;

import pwnee.*;

public class GraphMakerMain extends JFrame {
    
    public static GraphMakerMain instance;
    
    public GraphMakerPanel graphPanel;
    
    public GraphMakerMain(String filepath) {
        super("Graph visualization maker");
        int screenX = 1024;    
        int screenY = 800;
        this.setSize(screenX,screenY);
        
        // Set up the visualization's panel
        JPanel borderPanel = new JPanel(new BorderLayout());
        this.add(borderPanel);
        graphPanel = new GraphMakerPanel(filepath); 
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
    public static GraphMakerMain launchGlobal(String arg) {
      instance = new GraphMakerMain(arg);
      return instance;
    }
    
    
    
    
    /** Launches the example. */
    public static void main(String[] args) {
      if(args == null || args.length < 1) {
        GraphMakerMain window = GraphMakerMain.launchGlobal(null);
      }
      else {
        GraphMakerMain window = GraphMakerMain.launchGlobal(args[0]);
      }
    }

}
