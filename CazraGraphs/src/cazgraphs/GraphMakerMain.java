package cazgraphs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import pwnee.*;

import cazgraphs.graph.*;

public class GraphMakerMain extends JFrame {
    
    public static GraphMakerMain instance;
    
    public GraphMakerPanel graphPanel;
    
    public GraphMakerMenuBar menubar;
    
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
        
        menubar = new GraphMakerMenuBar();
        setJMenuBar(menubar);
        
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


class GraphMakerMenuBar extends JMenuBar implements ActionListener {
  
  public JMenu fileMenu = new JMenu("file");
    public JMenuItem newItem = new JMenuItem("New");
    public JMenuItem openItem = new JMenuItem("Open");
    public JMenuItem saveItem = new JMenuItem("Save");
    public JMenuItem exitItem = new JMenuItem("Exit");
  
  public GraphMakerMenuBar() {
    super();
    init();
  }
  
  private void init() {
    this.add(fileMenu);
    
    fileMenu.add(newItem);
    newItem.addActionListener(this);
    newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    
    fileMenu.add(openItem);
    openItem.addActionListener(this);
    openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    
    fileMenu.add(saveItem);
    saveItem.addActionListener(this);
    saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    
    fileMenu.add(exitItem);
    exitItem.addActionListener(this);
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
  }
  
  
  
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == newItem) {
      GraphMakerMain.instance.graphPanel.graph = new GraphSprite(true);
    }
    if(e.getSource() == openItem) {
      JFileChooser chooser = new JFileChooser(".");
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "TXT text file", "txt");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showOpenDialog(GraphMakerMain.instance);
      
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        GraphMakerMain.instance.graphPanel.loadFromFile(chooser.getSelectedFile().getPath());
      }
    }
    if(e.getSource() == saveItem) {
      JFileChooser chooser = new JFileChooser(".");
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "TXT text file", "txt");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showSaveDialog(GraphMakerMain.instance);
      
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        String path = chooser.getSelectedFile().getPath();
        if(!path.endsWith(".txt")) {
          path += ".txt";
        }
        GraphMakerMain.instance.graphPanel.saveToFile(path);
      }
    }
    if(e.getSource() == exitItem) {
      System.exit(0);
    }
  }
}

