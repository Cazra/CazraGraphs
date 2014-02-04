package cazgraphs;


/** Used to perform debug printing. */
public class Debug {
  
  /** Whether to allow debugging text to be printed. */
  public static boolean debugEnabled = false;
  
  /** Prints debugging messages if debug printing is enabled. */
  public static void debugln(String str) {
    if(debugEnabled) {
      System.out.println(str);
    }
  }
  
}
