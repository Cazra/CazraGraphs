package cazgraphs.graph.layout;

/** LayoutPhysics which provides mass, and x/y velocity. */
public class ParticlePhysics implements LayoutPhysics {
  
  /** Code for X velocity. */
  public static int DX = 0;
  
  /** Code for Y velocity. */
  public static int DY = 1;
  
  /** Code for mass. */
  public static int MASS = 2;
  
  private double dx;
  private double dy;
  private double mass;
  
  /** Creates a ParticlePhysics with 0 velocity and 1 unit of mass. */
  public ParticlePhysics() {
    this.dx = 0;
    this.dy = 0;
    this.mass = 1;
  }
  
  
  public double getProp(int code) {
    if(code == DX) {
      return dx;
    }
    else if(code == DY) {
      return dy;
    }
    else if(code == MASS) {
      return mass;
    }
    else {
      return -1;
    }
  }
  
  
  
  public void setProp(int code, double value) {
    if(code == DX) {
      dx = value;
    }
    else if(code == DY) {
      dy = value;
    }
    else if(code == MASS) {
      mass = value;
    }
  }
}


