package cazgraphs.graph;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pwnee.*;
import pwnee.sprites.Sprite;
import pwnee.text.Tooltipable;

import cazgraphs.graph.style.*;
import cazgraphs.util.FontUtils;

/** 
 * A leaf node that represents a duplicate of some other node and stores 
 * a reference to that node. 
 */
public class ReferenceGNodeSprite extends GNodeSprite {
  
  private static Object lock = new Object();
  
  private static long dupIDs = 0;
  
  /** A reference to the node that this is a duplicate of. */
  public GNodeSprite peer;
  
  public ReferenceGNodeSprite(GNodeSprite peer) {
    super(peer.graph, appendID(peer), peer.object);
    this.peer = peer;
  }
  
  private static String appendID(GNodeSprite peer) {
    synchronized(lock) {
      return peer.id + ";ref" + dupIDs++;
    }
  }
  
  
  /** By definition, reference nodes don't have forward edges. */
  @Override
  public GNodeSprite addEdge(GNodeSprite n) {
    return this;
  }
}
