package cazgraphs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import pwnee.*;

import cazgraphs.graph.*;
import cazgraphs.graph.layout.*;
import cazgraphs.graph.style.*;

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


/** The menubar for the Graph Maker application. */
class GraphMakerMenuBar extends JMenuBar implements ActionListener {
  
  public JMenu fileMenu = new JMenu("file");
    public JMenuItem newItem = new JMenuItem("New");
    public JMenuItem randomItem = new JMenuItem("Random");
    public JMenuItem openItem = new JMenuItem("Open");
    public JMenuItem saveItem = new JMenuItem("Save");
    public JMenuItem exitItem = new JMenuItem("Exit");
  
  public JMenu layoutMenu = new JMenu("layout");
    public JRadioButtonMenuItem layoutNoneItem = new JRadioButtonMenuItem("None");
    public JRadioButtonMenuItem layoutForceItem = new JRadioButtonMenuItem("Force-directed");
    public JRadioButtonMenuItem layoutCircleItem = new JRadioButtonMenuItem("Circular");
    public JRadioButtonMenuItem layoutBipartiteItem = new JRadioButtonMenuItem("Bipartite");
    
  public JMenu styleMenu = new JMenu("style");
    public JRadioButtonMenuItem styleNoneItem = new JRadioButtonMenuItem("None");
    public JRadioButtonMenuItem styleTopoItem = new JRadioButtonMenuItem("Topology");
    public JRadioButtonMenuItem styleAncestryItem = new JRadioButtonMenuItem("Ancestry");
    public JRadioButtonMenuItem styleBipartiteItem = new JRadioButtonMenuItem("Bipartite");
  
  public GraphMakerMenuBar() {
    super();
    init();
  }
  
  private void init() {
    this.add(fileMenu);
    
    fileMenu.add(newItem);
    newItem.addActionListener(this);
    newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    
    fileMenu.add(randomItem);
    randomItem.addActionListener(this);
    randomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    
    fileMenu.add(openItem);
    openItem.addActionListener(this);
    openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    
    fileMenu.add(saveItem);
    saveItem.addActionListener(this);
    saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    
    fileMenu.add(exitItem);
    exitItem.addActionListener(this);
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
    
    
    this.add(layoutMenu);
    ButtonGroup group = new ButtonGroup();
    
    layoutMenu.add(layoutNoneItem);
    group.add(layoutNoneItem);
    layoutNoneItem.addActionListener(this);
    
    layoutMenu.add(layoutForceItem);
    group.add(layoutForceItem);
    layoutForceItem.addActionListener(this);
    
    layoutMenu.add(layoutCircleItem);
    group.add(layoutCircleItem);
    layoutCircleItem.addActionListener(this);
    
    layoutMenu.add(layoutBipartiteItem);
    group.add(layoutBipartiteItem);
    layoutBipartiteItem.addActionListener(this);
    
    layoutNoneItem.setSelected(true);
    
    
    this.add(styleMenu);
    group = new ButtonGroup();
    
    styleMenu.add(styleNoneItem);
    group.add(styleNoneItem);
    styleNoneItem.addActionListener(this);
    
    styleMenu.add(styleTopoItem);
    group.add(styleTopoItem);
    styleTopoItem.addActionListener(this);
    
    styleMenu.add(styleAncestryItem);
    group.add(styleAncestryItem);
    styleAncestryItem.addActionListener(this);
    
    styleMenu.add(styleBipartiteItem);
    group.add(styleBipartiteItem);
    styleBipartiteItem.addActionListener(this);
    
    styleNoneItem.setSelected(true);
  }
  
  
  
  public void actionPerformed(ActionEvent e) {
    // File menu
    if(e.getSource() == newItem) {
      GraphMakerMain.instance.graphPanel.reset();
      layoutNoneItem.setSelected(true);
      styleNoneItem.setSelected(true);
    }
    if(e.getSource() == randomItem) {
      GraphMakerMain.instance.graphPanel.reset();
      int nodes = 0;
      double connectivity = 0;
      try {
        nodes = Integer.parseInt(JOptionPane.showInputDialog("# nodes:"));
        connectivity = Double.parseDouble(JOptionPane.showInputDialog("avg edges per node:"));
      }
      catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Hello, I am ERROR.");
        ex.printStackTrace();
      }
      GraphMakerMain.instance.graphPanel.graph = RandomGraphFactory.randomGraph(nodes, connectivity, new ForceDirectedGraphLayout());
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
      
      layoutNoneItem.setSelected(true);
      styleNoneItem.setSelected(true);
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
    
    // Layout menu
    if(e.getSource() == layoutNoneItem) {
      GraphMakerMain.instance.graphPanel.graph.layoutAlgorithm = new DefaultGraphLayout();
    }
    if(e.getSource() == layoutForceItem) {
      ForceDirectedGraphLayout layout = new ForceDirectedGraphLayout();
      GraphMakerMain.instance.graphPanel.graph.layoutAlgorithm = layout;
      
      for(GNodeSprite node : GraphMakerMain.instance.graphPanel.graph.nodes.values()) {
        node.dx = 0;
        node.dy = 0;
      }
      
      try {
        layout.ANTIGRAV *= Double.parseDouble(JOptionPane.showInputDialog("antigravity scale:"));
        layout.NSPRING *= Double.parseDouble(JOptionPane.showInputDialog("neighbor spring scale:"));
      }
      catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Hello, I am ERROR.");
        ex.printStackTrace();
      }
      
      
    }
    if(e.getSource() == layoutCircleItem) {
      GraphMakerMain.instance.graphPanel.graph.layoutAlgorithm = new NGonGraphLayout();
    }
    if(e.getSource() == layoutBipartiteItem) {
      try {
        GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
        int hspace = Integer.parseInt(JOptionPane.showInputDialog("horizontal spacing:"));
        int vspace = Integer.parseInt(JOptionPane.showInputDialog("vertical spacing:"));
        
        String startNodeID = null;
        if(graph.selectedNode != null) {
          startNodeID = graph.selectedNode.id;
        }
          
        BipartiteGraphStyle bStyle = new BipartiteGraphStyle();
        graph.style = bStyle;
        bStyle.computeBipartiteness(graph, startNodeID);
        graph.layoutAlgorithm = new BipartiteGraphLayout(startNodeID, hspace, vspace);
        
        styleBipartiteItem.setSelected(true);
      }
      catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Hello, I am ERROR.");
        ex.printStackTrace();
      }
    }
    
    
    // Style menu
    if(e.getSource() == styleNoneItem) {
      GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
      graph.style = new GraphStyle();
    }
    if(e.getSource() == styleTopoItem) {
      GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
      TopologyGraphStyle style = new TopologyGraphStyle();
      graph.style = style;
      
      style.setTopology(graph.selectedNode);
    }
    if(e.getSource() == styleAncestryItem) {
      GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
      AncestryGraphStyle style = new AncestryGraphStyle();
      graph.style = style;
      
      style.setAncestry(graph.selectedNode);
    }
    if(e.getSource() == styleBipartiteItem) {
      GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
      
      String startNodeID = null;
      if(graph.selectedNode != null) {
        startNodeID = graph.selectedNode.id;
      }
        
      BipartiteGraphStyle bStyle = new BipartiteGraphStyle();
      graph.style = bStyle;
      bStyle.computeBipartiteness(graph, startNodeID);
    }
  }
}

