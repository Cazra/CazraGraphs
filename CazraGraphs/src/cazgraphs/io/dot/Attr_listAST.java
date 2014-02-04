package cazgraphs.io.dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cazgraphs.CazgraphException;
import cazgraphs.Debug;
import cazgraphs.io.parse.AST;
import cazgraphs.io.parse.ParseFail;
import cazgraphs.io.parse.ParseResult;
import cazgraphs.io.parse.ParseSuccess;
import cazgraphs.util.StringSlice;

/** attr_list node for a DOT AST. See: http://www.graphviz.org/content/dot-language */
public class Attr_listAST implements AST {
  
  private static Pattern pattern = null;
  
  private List<AssignmentAST> assigns;
  
  
  public Attr_listAST(List<AssignmentAST> assigns) {
    this.assigns = assigns;
    
    Debug.debugln("Produced attr_list");
  }
  
  
  /** Returns the list of assignments. */
  public List<AssignmentAST> getAssigns() {
    return assigns;
  }
  
  
  /** Returns a Map representation of the assignment list. */
  public Map<String, String> toMap() {
    Map<String, String> result = new HashMap<>();
    
    for(AssignmentAST assign : assigns) {
      result.put(assign.getLHS(), assign.getRHS());
    }
    
    return result;
  }
  
  
  
  /** Returns the regex for this AST. */
  public static Pattern getRegex() {
    if(pattern == null) {
    //  "(\\[ *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))( *(;|,) *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)))* *\\])+( *\\[ *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>))( *(;|,) *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)) *= *(([a-zA-Z\200-\377_]+[a-zA-Z\200-\377_0-9]*|[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)|(?<!\\\\)\"(.)*?(?<!\\\\)\"|<.*>)))* *\\])*"
      pattern = Pattern.compile("(\\[ *" + AssignmentAST.getRegex().pattern() + "( *(;|,) *" + AssignmentAST.getRegex().pattern() + ")* *\\])+( *\\[ *" + AssignmentAST.getRegex().pattern() + "( *(;|,) *" + AssignmentAST.getRegex().pattern() + ")* *\\])*");
    }
    return pattern;
  }
  
  
  
  /** Creates an AST from a source string. */
  public static ParseResult<Attr_listAST> parse(StringSlice src) {
    Debug.debugln("Parsing attr_list from: \n" + src);
    
    if(src == null) {
      return new ParseFail<Attr_listAST>("Could not parse null.");
    }
    src = src.trimLeft();
    
    Matcher matcher = getRegex().matcher(src);
    if(matcher.find() && matcher.start() == 0) {
      // Extract each assignment using our assignment regex.
      List<AssignmentAST> assigns = new ArrayList<>();
      
      Matcher assignMatcher = AssignmentAST.getRegex().matcher(src);
      while(assignMatcher.find()) {
        ParseResult<AssignmentAST> assign = AssignmentAST.parse(new StringSlice(assignMatcher.group()));
        assigns.add(assign.getAST());
      }
      
      Attr_listAST result = new Attr_listAST(assigns);
      return new ParseSuccess<Attr_listAST>(result, src.start(), src.start() + matcher.end());
    }
    else {
      return new ParseFail<Attr_listAST>("Could not parse DOT attr_list in:" + src);
    }
  }
  
  
  
  /** 
   * Returns a copy of the provided attributes, with the attributes in this 
   * AST added in (and possibly overriding existing attributes). 
   */
  public Map<String, String> mergeWith(Map<String, String> srcAttrs) {
    Map<String, String> newAttrs = new HashMap<>(srcAttrs);
    newAttrs.putAll(this.toMap());
    return newAttrs;
  }
  
  
  
  @Override
  public String stringify() {
    String result = "";
    result += "[";
    
    boolean first = true;
    for(AssignmentAST assign : getAssigns()) {
      if(first) {
        first = false;
      }
      else {
        result += ",";
      }
      result += assign.stringify();
    }
    
    result += "]";
    return result;
  }
}

