package cazgraphs.io.dot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cazgraphs.CazgraphException;


/** ID regex matcher for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class IDAST {
  
  //private static Pattern reg1 = Pattern.compile("[a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*");
  //private static Pattern reg2 = Pattern.compile("[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)");
  //private static Pattern reg3 = Pattern.compile("(?<!\\\\)\"(.)*?(?<!\\\\)\"");
  //private static Pattern reg4 = Pattern.compile("<.*>");
  
  private static Pattern pattern = null;

  
  public static Pattern getRegex() {
    if(pattern == null) {
      pattern = Pattern.compile("([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)");
    }
    return pattern;
  }
}

