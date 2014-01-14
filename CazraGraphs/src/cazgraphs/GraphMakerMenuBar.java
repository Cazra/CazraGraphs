package cazgraphs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import pwnee.*;

import cazgraphs.graph.*;
import cazgraphs.graph.layout.*;
import cazgraphs.graph.model.DirectedGraph;
import cazgraphs.graph.style.*;



/** The menubar for the Graph Maker application. */
public class GraphMakerMenuBar extends JMenuBar {
  
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
            JOptionPane.showMessageDialog(self, "Hello, I am ERROR.");
            ex.printStackTrace();
          }
          GraphMakerMain.instance.graphPanel.graph = RandomGraphFactory.randomGraph(nodes, connectivity, new ForceDirectedGraphLayout());
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
      openItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
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
          GraphMakerMain.instance.graphPanel.graph.layoutAlgorithm = new DefaultGraphLayout();
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
          GraphMakerMain.instance.graphPanel.graph.layoutAlgorithm = layout;
          
          for(VertexSprite node : GraphMakerMain.instance.graphPanel.graph.getSprites()) {
            node.dx = 0;
            node.dy = 0;
          }
          
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
          GraphMakerMain.instance.graphPanel.graph.layoutAlgorithm = new NGonGraphLayout();
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
            graph.layoutAlgorithm = new BipartiteGraphLayout(startNodeID, hspace, vspace);
            
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
          graph.setStyle(new GraphStyle());
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
          
          GraphSprite sprite = new GraphSprite(forest);
          sprite.layoutAlgorithm = oldSprite.layoutAlgorithm;
          sprite.setStyle(new CyclicTreeGraphStyle());
          
          
          GraphMakerMain.instance.graphPanel.reset();
          GraphMakerMain.instance.graphPanel.graph = sprite;
          styleNoneItem.setSelected(true);
        }
      });
    }
    return toTreeItem;
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

}