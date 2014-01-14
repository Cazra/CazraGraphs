package cazgraphs.graph.model;

/** 
 * A data structure for an undirected graph. 
 */
public class UndirectedGraph extends DirectedGraph {
  
  /** Creates an empty undirected graph. */
  public UndirectedGraph() {
    super();
  }
  
  
  /** Adds an undirected edge to the graph. */
  @Override
  public void addEdge(String vertexID1, String vertexID2) {
    super.addEdge(vertexID1, vertexID2);
    super.addEdge(vertexID2, vertexID1);
  }
  
  /** Removes an undirected edge from the graph. */
  @Override
  public void removeEdge(String vertexID1, String vertexID2) {
    super.removeEdge(vertexID1, vertexID2);
    super.removeEdge(vertexID2, vertexID1);
  }
}
