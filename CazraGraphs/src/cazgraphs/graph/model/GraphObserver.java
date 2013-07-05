package cazgraphs.graph.model;


public interface GraphObserver {
  
  public void handleGraphEvent(GraphEvent evt);
  
}