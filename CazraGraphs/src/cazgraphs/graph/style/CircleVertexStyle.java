package cazgraphs.graph.style;

import java.awt.*;
import java.awt.geom.*;

import pwnee.*;

import cazgraphs.graph.*;

/** A node rendering style which draws the node as a circle with its center at the node's origin. */
public class CircleVertexStyle extends EllipseVertexStyle {
  
  /** To make a circle, just make an ellipse that fits in a square bounding box. */
  public Dimension2D getDimensions(VertexSprite node) {
    Dimension2D dims = super.getDimensions(node);
    double maxSide = Math.max(dims.getWidth(), dims.getHeight());
    return new Dimension((int) maxSide, (int) maxSide);
  }
}
