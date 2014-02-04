package cazgraphs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import pwnee.*;

import cazgraphs.CazgraphException;
import cazgraphs.graph.*;
import cazgraphs.graph.layout.*;
import cazgraphs.graph.model.DirectedGraph;
import cazgraphs.graph.style.*;
import cazgraphs.io.CazGraphTextIO;
import cazgraphs.io.GraphIO;
import cazgraphs.io.dot.DotIO;



/** The menubar for the Graph Maker application. */
public class GraphMakerMenuBar extends JMenuBar {
  
  private JFileChooser chooser = null;
    private FileNameExtensionFilter txtFilter = null;
    private FileNameExtensionFilter dotFilter = null;
  
  private JMenu fileMenu = null;
    private JMenuItem newItem = null;
    private JMenuItem randomItem = null;
    private JMenuItem openItem = null;
    private JMenuItem saveItem = null;
    private JMenuItem exitItem = null;
  
  private JMenu layoutMenu = null;
    private JRadioButtonMenuItem layoutNoneItem = null;
    private JRadioButtonMenuItem layoutForceItem = null;
    private JRadioButtonMenuItem layoutCircleItem = null;
    private JRadioButtonMenuItem layoutBipartiteItem = null;
    
  private JMenu styleMenu = null;
    private JRadioButtonMenuItem styleNoneItem = null;
    private JRadioButtonMenuItem styleTopoItem = null;
    private JRadioButtonMenuItem styleAncestryItem = null;
    private JRadioButtonMenuItem styleBipartiteItem = null;
  
  private JMenu algsMenu = null;
    private JMenuItem toTreeItem = null;
    private JMenuItem findRootsItem = null;
    private JMenuItem findComponentsItem = null;
  
  public GraphMakerMenuBar() {
    super();
    init();
  }
  
  private void init() {
    this.add(getFileMenu());
    this.add(getLayoutMenu());
    this.add(getStyleMenu());
    this.add(getAlgsMenu());
  }
  
  
  /** Gets the file chooser for the Graph Maker, which supports some standard graph file formats. */
  public JFileChooser getFileChooser() {
    if(chooser == null) {
      chooser = new JFileChooser(".");
      
      txtFilter = new FileNameExtensionFilter("CazGraphs TXT text", "txt");
      chooser.setFileFilter(txtFilter);
      
      dotFilter = new FileNameExtensionFilter("GraphVis DOT", "dot");
      chooser.addChoosableFileFilter(dotFilter);
    }
    return chooser;
  }
  
  
  
  public JMenu getFileMenu() {
    if(fileMenu == null) {
      fileMenu = new JMenu("File");
      fileMenu.add(getNewItem());
      fileMenu.add(getRandomItem());    
      fileMenu.add(getOpenItem());    
      fileMenu.add(getSaveItem());
      fileMenu.add(getExitItem());
    }
    return fileMenu;
  }
  
  
  
  /** Creates a new empty graph. */
  public JMenuItem getNewItem() {
    if(newItem == null) {
      newItem = new JMenuItem("New");
      newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
      newItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphMakerMain.instance.graphPanel.reset();
          layoutNoneItem.setSelected(true);
          styleNoneItem.setSelected(true);
        }
        
      });
    }
    return newItem;
  }
  
  
  
  /** Randomly generates a graph. */
  public JMenuItem getRandomItem() {
    if(randomItem == null) {
      randomItem = new JMenuItem("Random");
      randomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
      final Component self = this;
      randomItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphMakerMain.instance.graphPanel.reset();
          int nodes = 0;
          double connectivity = 0;
          try {
            nodes = Integer.parseInt(JOptionPane.showInputDialog("# nodes:"));
            connectivity = Double.parseDouble(JOptionPane.showInputDialog("avg edges per node:"));
          }
          catch(Exception ex) {
            showErrorMessage(ex.getMessage());
          }
          GraphMakerMain.instance.graphPanel.setGraph(RandomGraphFactory.randomGraph(nodes, connectivity));
        }
        
      });
    }
    return randomItem;
  }
  
  
  /** Interprets a graph from a text file. */
  public JMenuItem getOpenItem() {
    if(openItem == null) {
      openItem = new JMenuItem("Open");
      openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      final Component self = this;
      openItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          int returnVal = getFileChooser().showOpenDialog(GraphMakerMain.instance);
          if(returnVal == JFileChooser.APPROVE_OPTION) {
            
            try {
              String path = chooser.getSelectedFile().getPath();
              
              GraphIO loader = getChooserIO(chooser.getFileFilter());
              GraphSprite graph = loader.loadFromFile(path);
              
              GraphMakerMain.instance.graphPanel.setGraph(graph);
              
              layoutNoneItem.setSelected(true);
              styleNoneItem.setSelected(true);
            }
            catch(Exception ex) {
              showErrorMessage("Could not load graph: " + ex.getMessage());
              ex.printStackTrace();
            }
          }
        }
        
      });
    }
    return openItem;
  }
  
  
  /** Saves the graph to a text file. */
  public JMenuItem getSaveItem() {
    if(saveItem == null) {
      saveItem = new JMenuItem("Save");
      saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
      saveItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          int returnVal = getFileChooser().showSaveDialog(GraphMakerMain.instance);
          if(returnVal == JFileChooser.APPROVE_OPTION) {
            try {
              String path = chooser.getSelectedFile().getPath();
              GraphIO loader = getChooserIO(chooser.getFileFilter());
              
              if(!path.toLowerCase().endsWith("." + loader.getDefaultFileExtension())) {
                path += "." + loader.getDefaultFileExtension();
              }
              
              loader.saveToFile(GraphMakerMain.instance.graphPanel.graph, path);
            }
            catch(Exception ex) {
              showErrorMessage("Could not save graph: " + ex.getMessage());
            }
          }
        }
        
      });
    }
    return saveItem;
  }
  
  
  /** Exits the application. */
  public JMenuItem getExitItem() {
    if(exitItem == null) {
      exitItem = new JMenuItem("Exit");
      exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
      exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
      });
    }
    return exitItem;
  }
  
  
  
  
  public JMenu getLayoutMenu() {
    if(layoutMenu == null) {
      layoutMenu = new JMenu("layout");
      ButtonGroup group = new ButtonGroup();
    
      layoutMenu.add(getLayoutNoneItem());
      group.add(getLayoutNoneItem());
      
      layoutMenu.add(getLayoutForceItem());
      group.add(getLayoutForceItem());
      
      layoutMenu.add(getLayoutCircleItem());
      group.add(getLayoutCircleItem());
      
      layoutMenu.add(getLayoutBipartiteItem());
      group.add(getLayoutBipartiteItem());
      
      getLayoutNoneItem().setSelected(true);
    }
    return layoutMenu;
  }
  
  
  /** Sets the graph to not use any layout algorithm. */
  public JRadioButtonMenuItem getLayoutNoneItem() {
    if(layoutNoneItem == null) {
      layoutNoneItem = new JRadioButtonMenuItem("None");
      layoutNoneItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphMakerMain.instance.graphPanel.graph.setLayout(new DefaultGraphLayout());
        }
      });
    }
    return layoutNoneItem;
  }
  
  
  /** Sets the graph to use a force-directed layout algorithm. */
  public JRadioButtonMenuItem getLayoutForceItem() {
    if(layoutForceItem == null) {
      layoutForceItem = new JRadioButtonMenuItem("Force-directed");
      final Component self = this;
      layoutForceItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ForceDirectedGraphLayout layout = new ForceDirectedGraphLayout();
          GraphMakerMain.instance.graphPanel.graph.setLayout(layout);
          
          try {
            layout.ANTIGRAV *= Double.parseDouble(JOptionPane.showInputDialog("antigravity scale:"));
            layout.NSPRING *= Double.parseDouble(JOptionPane.showInputDialog("neighbor spring scale:"));
          }
          catch(Exception ex) {
            JOptionPane.showMessageDialog(self, "Hello, I am ERROR.");
            ex.printStackTrace();
          }
        }
      });
    }
    return layoutForceItem;
  }
  
  
  /** Sets the graph to use the N-gon layout algorithm. */
  public JRadioButtonMenuItem getLayoutCircleItem() {
    if(layoutCircleItem == null) {
      layoutCircleItem = new JRadioButtonMenuItem("Circular");
      layoutCircleItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphMakerMain.instance.graphPanel.graph.setLayout(new NGonGraphLayout());
        }
        
      });
    }
    return layoutCircleItem;
  }
  
  
  /** Sets the graph to use the bipartite layout algorithm. */
  public JRadioButtonMenuItem getLayoutBipartiteItem() {
    if(layoutBipartiteItem == null) {
      layoutBipartiteItem = new JRadioButtonMenuItem("Bipartite");
      final Component self = this;
      layoutBipartiteItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
            int hspace = Integer.parseInt(JOptionPane.showInputDialog("horizontal spacing:"));
            int vspace = Integer.parseInt(JOptionPane.showInputDialog("vertical spacing:"));
            
            String startNodeID = null;
            if(graph.selectedNode != null) {
              startNodeID = graph.selectedNode.getID();
            }
              
            BipartiteGraphStyle style = new BipartiteGraphStyle();
            graph.setStyle(style);
            style.computeBipartiteness(graph, startNodeID);
            graph.setLayout(new BipartiteGraphLayout(startNodeID, hspace, vspace));
            
            styleBipartiteItem.setSelected(true);
          }
          catch(Exception ex) {
            JOptionPane.showMessageDialog(self, "Hello, I am ERROR.");
            ex.printStackTrace();
          }
        }
      });
    }
    return layoutBipartiteItem;
  }
  
  
  
  public JMenu getStyleMenu() {
    if(styleMenu == null) {
      styleMenu = new JMenu("style");
      ButtonGroup group = new ButtonGroup();
    
      styleMenu.add(getStyleNoneItem());
      group.add(getStyleNoneItem());
      
      styleMenu.add(getStyleTopoItem());
      group.add(getStyleTopoItem());
      
      styleMenu.add(getStyleAncestryItem());
      group.add(getStyleAncestryItem());
      
      styleMenu.add(getStyleBipartiteItem());
      group.add(getStyleBipartiteItem());
      
      // Start with the default style.
      getStyleNoneItem().setSelected(true);
    }
    return styleMenu;
  }
  
  
  /** Sets the graph to use the default graph style. */
  public JRadioButtonMenuItem getStyleNoneItem() {
    if(styleNoneItem == null) {
      styleNoneItem = new JRadioButtonMenuItem("Default");
      styleNoneItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
          graph.setStyle(new DefaultGraphStyle());
        }
        
      });
    }
    return styleNoneItem;
  }
  
  
  /** Sets the graph to use the topology graph style. */
  public JRadioButtonMenuItem getStyleTopoItem() {
    if(styleTopoItem == null) {
      styleTopoItem = new JRadioButtonMenuItem("Topology");
      styleTopoItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
          TopologyGraphStyle style = new TopologyGraphStyle();
          graph.setStyle(style);
          
          style.setTopology(graph.selectedNode);
        }
        
      });
    }
    return styleTopoItem;
  }
  
  
  /** Sets the graph to use the ancestry graph style. */
  public JRadioButtonMenuItem getStyleAncestryItem() {
    if(styleAncestryItem == null) {
      styleAncestryItem = new JRadioButtonMenuItem("Ancestry");
      styleAncestryItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
          AncestryGraphStyle style = new AncestryGraphStyle();
          graph.setStyle(style);
          
          style.setAncestry(graph.selectedNode);
        }
      });
    }
    return styleAncestryItem;
  }
  
  
  /** Sets the graph to use the bipartite graph style. */
  public JRadioButtonMenuItem getStyleBipartiteItem() {
    if(styleBipartiteItem == null) {
      styleBipartiteItem = new JRadioButtonMenuItem("Bipartite");
      styleBipartiteItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
          
          String startNodeID = null;
          if(graph.selectedNode != null) {
            startNodeID = graph.selectedNode.getID();
          }
            
          BipartiteGraphStyle style = new BipartiteGraphStyle();
          graph.setStyle(style);
          style.computeBipartiteness(graph, startNodeID);
        }
        
      });
    }
    return styleBipartiteItem;
  }
  
  
  
  public JMenu getAlgsMenu() {
    if(algsMenu == null) {
      algsMenu = new JMenu("Algorithms");
      algsMenu.add(getToTreeItem());
      algsMenu.add(getFindRootsItem());
      algsMenu.add(getFindComponentsItem());
    }
    return algsMenu;
  }
  
  
  /** Converts a cyclic graph into a tree by using duplicates and reference nodes. */
  public JMenuItem getToTreeItem() {
    if(toTreeItem == null) {
      toTreeItem = new JMenuItem("Convert to tree");
      toTreeItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          GraphSprite oldSprite = GraphMakerMain.instance.graphPanel.graph;
          DirectedGraph forest = GraphSolver.convertToTree(oldSprite.getGraph());
          
          GraphSprite treeSprite = new GraphSprite(forest);
          treeSprite.setLayout(oldSprite.getLayout());
          treeSprite.setStyle(new CyclicTreeGraphStyle());
          
          // Nest the vertices under any noots. 
          for(String rootID : treeSprite.findRoots()) {
            _treeNest(treeSprite.getSprite(rootID), 1);
          }
          
          GraphMakerMain.instance.graphPanel.reset();
          GraphMakerMain.instance.graphPanel.graph = treeSprite;
          styleNoneItem.setSelected(true);
        }
      });
    }
    return toTreeItem;
  }
  
  
  private void _treeNest(VertexSprite v, int depth) {
    for(String toID : v.getEdges()) {
      VertexSprite other = v.getGraph().getSprite(toID);
      v.addChild(other);
      v.setEdgeLabel(toID, "" + depth);
      _treeNest(other, depth + 1);
    }
  }
  
  
  
  /** Identifies the root vertices in the graph and highlights them. */
  public JMenuItem getFindRootsItem() {
    if(findRootsItem == null) {
      findRootsItem = new JMenuItem("Find roots");
      findRootsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
          graph.setStyle(new HighlightedSetGraphStyle(graph.findRoots()));
        }
      });
    }
    return findRootsItem;
  }
  
  
  
  /** Identifies the separate components of the graph and color-codes them. */
  public JMenuItem getFindComponentsItem() {
    if(findComponentsItem == null) {
      findComponentsItem = new JMenuItem("Find components");
      findComponentsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          GraphSprite graph = GraphMakerMain.instance.graphPanel.graph;
          graph.setStyle(new PartitionGraphStyle(graph.findComponents()));
        }
      });
    }
    return findComponentsItem;
  }
  
  
  /** Displays an error pop-up message. */
  private void showErrorMessage(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Hello, I am ERROR.", JOptionPane.ERROR_MESSAGE);
  }
  
  
  /** Returns the appropriate GraphIO object for loading or saving the last selected file. */
  private GraphIO getChooserIO(String path) {
    String ext = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
    
    if(ext.equals(CazGraphTextIO.defaultFileExtension())) {
      return CazGraphTextIO.getInstance();
    }
    else if(ext.equals(DotIO.defaultFileExtension())) {
      return DotIO.getInstance();
    }
    else {
      throw new CazgraphException("Unsupported file type: " + ext);
    }
  }
  
  
  private GraphIO getChooserIO(FileFilter filter) {
    if(filter == txtFilter) {
      return CazGraphTextIO.getInstance();
    }
    else if(filter == dotFilter) {
      return DotIO.getInstance();
    }
    else {
      throw new CazgraphException("Unsupported file filter");
    }
  }
  
  
  /** Returns the extension of a file path. */
  private String getFileExtension(String path) {
    
    return path.substring(path.lastIndexOf(".") + 1).toLowerCase();
  }
  
}