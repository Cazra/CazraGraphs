package cazgraphs.io.dot;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.io.parse.AST;
import cazgraphs.io.parse.ParseFail;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.io.parse.ParseSuccess;
import cazgraphs.util.StringSlice;

/** A convenience AST for wrapping DOT expressions of the form ID '=' ID */
public class AssignmentAST implements AST {
  
  private static Pattern pattern = null;
  
  private String lhs;
  
  private String rhs;
  
  
  public AssignmentAST(String lhs, String rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
    
    Debug.debugln("Produced assignment");
    Debug.debugln("  LHS: " + lhs);
    Debug.debugln("  RHS: " + rhs);
  }
  
  /** Returns the left-hand term of the assignment. */
  public String getLHS() {
    return lhs;
  }
  
  /** Returns the right-hand term of the assignment. */
  public String getRHS() {
    return rhs;
  }
  
  
  /** Returns the regex for this AST. */
  public static Pattern getRegex() {
    if(pattern == null) {
      //  "(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))"
      pattern = Pattern.compile("(" + IDAST.getRegex().pattern() + ") *= *(" + IDAST.getRegex().pattern() + ")");
    }
    return pattern;
  }
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<AssignmentAST> parse(StringSlice src) {
    Debug.debugln("Parsing assignment from: \n" + src);
    
    if(src == null) {
      return new ParseFail<AssignmentAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    Matcher matcher = getRegex().matcher(src);
    if(matcher.find() && matcher.start() == 0) {
      String lhs = matcher.group(1);
      String rhs = matcher.group(6);
      
      AssignmentAST result = new AssignmentAST(lhs, rhs);
      return new ParseSuccess<AssignmentAST>(result, src.start(), src.start() + matcher.end());
    }
    else {
      return new ParseFail<AssignmentAST>("Could not parse DOT assignment in: " + src);
    }
  }
  
  
  
  
  @Override
  public String stringify() {
    String result = "";
    
    result += lhs + " = " + rhs;
    
    return result;
  }
}