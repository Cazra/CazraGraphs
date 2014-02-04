package cazgraphs.io.parse;

import java.util.Stack;

/** Utilities class for parsing syntax structures. */
public class ParseUtils {
  
  /** 
   * Finds the right character in a string matching the left character at, 
   * or closest to the right of, the given position. 
   * @return The index of the matching right character in src if it is found.
   *          Otherwise, it returns -1.
   */
  private static int _findRight(CharSequence src, int leftPos, char left, char right) {
    int depth = 0;
    
    for(int i = leftPos; i < src.length(); i++) {
      if(src.charAt(i) == left) {
        depth++;
      }
      else if (src.charAt(i) == right) {
        depth--;
        
        if(depth == 0) {
          return i;
        }
        else if(depth < 0) {
          return -1;
        }
      }
    }
    
    return -1;
  }
  
  
  
  /** 
   * Finds the right " in a string matching the left " at, 
   * or closest to the right of, the given position. 
   * @return The index of the matching right " in src if it is found.
   *          Otherwise, it returns -1.
   */
  private static int _findRightDQ(CharSequence src, int leftPos) {
    int count = 0;
    
    for(int i = leftPos; i < src.length(); i++) {
      if(src.charAt(i) == '\"') {
        count++;
        
        // Don't count escaped quotes.
        if(i > 0 && src.charAt(i-1) == '\\') {
          count--;
        }
        
        if(count == 2) {
          return i;
        }
      }
    }
    
    return -1;
  }
  
  
  
  /** 
   * Finds the right ' in a string matching the left ' at, 
   * or closest to the right of, the given position. 
   * @return The index of the matching right ' in src if it is found.
   *          Otherwise, it returns -1.
   */
  private static int _findRightSQ(CharSequence src, int leftPos) {
    int count = 0;
    
    for(int i = leftPos; i < src.length(); i++) {
      if(src.charAt(i) == '\'') {
        count++;
        
        // Don't count escaped quotes.
        if(i > 0 && src.charAt(i-1) == '\\') {
          count--;
        }
        
        if(count == 2) {
          return i;
        }
      }
    }
    
    return -1;
  }
  
  
  
  /** 
   * Finds the right-side matching character for some left-side character at
   * the given position. Supported left characters are {, [, <, (, ", and '.
   * @return The index of the matching character in src if it is found.
   *          Otherwise, it returns -1.
   */
  public static int findRight(CharSequence src, int leftPos) {
    char leftChar = src.charAt(leftPos);
    if(leftChar == '{') {
      return _findRight(src, leftPos, '{', '}');
    }
    else if(leftChar == '[') {
      return _findRight(src, leftPos, '[', ']');
    }
    else if(leftChar == '<') {
      return _findRight(src, leftPos, '<', '>');
    }
    else if(leftChar == '(') {
      return _findRight(src, leftPos, '(', ')');
    }
    else if(leftChar == '"') {
      return _findRightDQ(src, leftPos);
    }
    else if(leftChar == '\'') {
      return _findRightSQ(src, leftPos);
    }
    else {
      return -1;
    }
  }
  
  
  
  
  /** 
   * Returns true if the enclosing pair characters in src all match up. 
   * These character pairs are { and }, [ and ], < and >, and ( and ).
   * These characters are ignored if occur within pairs of "s or 's.
   */
  public static boolean enclosingCharsBalanced(CharSequence src) {
    Stack<Character> stack = new Stack<>();
    
    boolean inDQ = false;
    boolean inSQ = false;
    for(int i = 0; i < src.length(); i++) {
      if(inDQ) {
        if(src.charAt(i) == '"' && src.charAt(i-1) != '\\') {
          inDQ = false;
        }
      }
      else if(inSQ) {
        if(src.charAt(i) == '\'' && src.charAt(i-1) != '\\') {
          inSQ = false;
        }
      }
      else {
        
        if(src.charAt(i) == '{') {
          stack.push('{');
        }
        else if(src.charAt(i) == '[') {
          stack.push('[');
        }
        else if(src.charAt(i) == '<') {
          stack.push('<');
        }
        else if(src.charAt(i) == '(') {
          stack.push('(');
        }
        else if(src.charAt(i) == '"') {
          inDQ = true;
        }
        else if(src.charAt(i) == '\'') {
          inSQ = true;
        }
        else if(src.charAt(i) == '}') {
          if(stack.empty() || stack.pop() != '{') {
            return false;
          }
        }
        else if(src.charAt(i) == ']') {
          if(stack.empty() || stack.pop() != '[') {
            return false;
          }
        }
        else if(src.charAt(i) == '>') {
          if(stack.empty() || stack.pop() != '<') {
            return false;
          }
        }
        else if(src.charAt(i) == ')') {
          if(stack.empty() || stack.pop() != '(') {
            return false;
          }
        }
      }
    }
    
    return stack.empty() && !inDQ && !inSQ;
  }
  
  
  
  
  /** 
   * Removes all C++-style comments from a String. This includes both block 
   * comments and line comments. 
   */
  public static String removeCPP(String src) {
    boolean inDQ = false;
    boolean inSQ = false;
    
    String result = src;
    
    for(int i = 0; i < result.length()-1; i++) {
      char c = result.charAt(i);
      
      if(inDQ) {
        if(c == '\"' && result.charAt(i-1) != '\\') {
          inDQ = false;
        }
      }
      else if(inSQ) {
        if(c == '\'' && result.charAt(i-1) != '\\') {
          inSQ = false;
        }
      }
      else {
      
        // Line comment
        if(c == '/' && result.charAt(i+1) == '/') {
          int newLineIndex = result.indexOf("\n", i);
          
          result = result.substring(0, i) + result.substring(newLineIndex);
          i--;
        }
        
        // Block comment
        else if(c == '/' && result.charAt(i+1) == '*') {
          int endBlockIndex = result.indexOf("*/", i) + 2;
          
          result = result.substring(0, i) + result.substring(endBlockIndex);
          i--;
        }
        
        else if(c == '\"') {
          inDQ = true;
        }
        else if(c == '\'') {
          inSQ = true;
        }
      }
    }
    
    return result;
  }
  
}
