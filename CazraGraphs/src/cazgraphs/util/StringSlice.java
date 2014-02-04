package cazgraphs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** 
 * A section of a CharSequence defined by the original CharSequence and an 
 * index range [start, end). 
 */
public class StringSlice implements CharSequence {
  
  private CharSequence src;
  
  private int start;
  
  private int end;
  
  /** 
   * Constructs a slice for a section of a source String formed from the 
   * range [start, end). 
   */
  public StringSlice(CharSequence src, int start, int end) {
    this.src = src;
    this.start = start;
    this.end = end;
    
    if(start < 0) {
      throw new ArrayIndexOutOfBoundsException(start);
    }
    if(end > src.length()) {
      throw new ArrayIndexOutOfBoundsException(end);
    }
    if(start > end) {
      throw new ArrayIndexOutOfBoundsException("Slice start " + start + " cannot be > end " + end);
    }
    
  }
  
  /** 
   * Constructs a slice for a section of a source String ranging from a start 
   * index to the end of the source String.
   */
  public StringSlice(CharSequence src, int start) {
    this(src, start, src.length());
  }
  
  
  /** Constructs a slice for an entire source String. */
  public StringSlice(CharSequence src) {
    this(src, 0, src.length());
  }
  
  
  /** Returns the source sequence that this is a slice into.*/
  public CharSequence getSource() {
    return src;
  }
  
  /** Returns the start index of this slice into its source sequence. */
  public int start() {
    return start;
  }
  
  /** Returns the end index of this slice into its source sequence. */
  public int end() {
    return end;
  }
  
  
  
  
  /** Returns the char value at the specified index inside the slice. */
  @Override
  public char charAt(int index) {
    return src.charAt(start + index);
  }
  
  /** Returns the length of the slice. */
  @Override
  public int length() {
    return end - start;
  }
  
  /** Returns a subSequence of this slice. */
  @Override
  public CharSequence subSequence(int start, int end) {
    return new StringSlice(this, start, end);
  }
  
  /** Returns the slice as a String. */
  @Override
  public String toString() {
    if(length() == 0) {
      return "";
    }
    else {
      char[] arr = new char[length()];
      for(int i = 0; i < length(); i++) {
        arr[i] = charAt(i);
      }
      return new String(arr);
    }
  }
  
  
  
  /** Returns true iff the slice contains the specified sequence of char values. */
  public boolean contains(CharSequence s) {
    return (indexOf(s) != -1);
  }
  
  /** 
   * Returns the index within this slice of the first occurence of the
   * specified sequence of char values.
   * If it could not be found, -1 is returned.
   */
  public int indexOf(CharSequence s) {
    return indexOf(s, 0);
  }
  
  /** 
   * Returns the index within this slice, starting from the specified index, 
   * of the first occurence of the specified sequence of char values.
   * If it could not be found, -1 is returned.
   */
  public int indexOf(CharSequence s, int fromIndex) {
    int index = fromIndex;
    while(this.length() - index >= s.length()) {
      if(regionMatches(index, s, 0, s.length())) {
        return index;
      }
      index++;
    }
    return -1;
  }
  
  
  /** 
   * Returns the index within this slice of the last occurence of the 
   * specified sequence of char values. If it could not be found, 
   * -1 is returned.
   */
  public int lastIndexOf(CharSequence s) {
    return lastIndexOf(s, length()-s.length());
  }
  
  /** 
   * Returns the index within this slice, starting from the specified index, 
   * of the last occurence of the specified sequence of char values.
   * If it could not be found, -1 is returned.
   */
  public int lastIndexOf(CharSequence s, int fromIndex) {
    int index = fromIndex;
    while(index >= 0) {
      if(regionMatches(index, s, 0, s.length())) {
        return index;
      }
      index--;
    }
    return -1;
  }
  
  
  /** 
   * Tests if two string regions are equal. 
   * See http://docs.oracle.com/javase/6/docs/api/java/lang/String.html#regionMatches(int, java.lang.String, int, int). 
   */
  public boolean regionMatches(boolean ignoreCase, int toffset, CharSequence other, int ooffset, int len) {
    if(toffset < 0 || ooffset < 0 || toffset + len > this.length() || ooffset + len > other.length()) {
      return false;
    }
    for(int i=0; i < len; i++) {
      int ct = this.charAt(toffset+i);
      int co = other.charAt(ooffset+i);
      
      if(ignoreCase) {
        if(ct >= 'A' && ct <= 'Z') {
          ct = 'a' + (ct - 'A');
        }
        if(co >= 'A' && co <= 'Z') {
          co = 'a' + (co - 'A');
        }
      }
      
      if(ct != co) {
        return false;
      }
    }
    return true;
  }
  
  /** 
   * Tests if two string regions are equal with case-sensitivity. 
   * See http://docs.oracle.com/javase/6/docs/api/java/lang/String.html#regionMatches(int, java.lang.String, int, int). 
   */
  public boolean regionMatches(int toffset, CharSequence other, int ooffset, int len) {
    return regionMatches(false, toffset, other, ooffset, len);
  }
  
  
  /** Tests if this slice ends with the specified suffix. */
  public boolean endsWith(CharSequence suffix) {
    return regionMatches(this.length() - suffix.length(), suffix, 0, suffix.length());
  }
  
  /** Tests if this slice begins with the specified prefix. */
  public boolean startsWith(CharSequence prefix) {
    return regionMatches(0, prefix, 0, prefix.length());
  }
  
  
  /** Returns a new slice that is a subslice of this slice. */
  public StringSlice subslice(int start, int end) {
    return new StringSlice(this, start, end);
  }
  
  
  /** Returns a new slice that is a subslice of this slice. */
  public StringSlice subslice(int start) {
    return new StringSlice(this, start);
  }
  
  
  /** Creates a new slice of this slice with leading and trailing whitespace removed. */
  public StringSlice trim() {
    int start = 0;
    int end = length();
    
    while(start < end && charAt(start) <= ' ') {
      start++;
    }
    while(end > start && charAt(end - 1) <= ' ') {
      end--;
    }
    
    return new StringSlice(this, start, end);
  }
  
  /** Creates a new slice of this slice with leading whitespace removed. */
  public StringSlice trimLeft() {
    int start = 0;
    int end = length();
    
    while(start < end && charAt(start) <= ' ') {
      start++;
    }
    
    return new StringSlice(this, start, end);
  }
  
  /** Creates a new slice of this slice with trailing whitespace removed. */
  public StringSlice trimRight() {
    int start = 0;
    int end = length();
    
    while(end > start && charAt(end - 1) <= ' ') {
      end--;
    }
    
    return new StringSlice(this, start, end);
  }
  
  /** Returns true iff length() is 0. */
  public boolean isEmpty() {
    return (length() == 0);
  }
  
  
  /** Splits this slice around matches of the given regular expression. */
  public StringSlice[] split(String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(this);
    
    List<StringSlice> tokens = new ArrayList<>();
    int tokenStart = 0;
    while(matcher.find()) {
      tokens.add(new StringSlice(this, tokenStart, matcher.start()));
      tokenStart = matcher.end();
    }
    tokens.add(new StringSlice(this, tokenStart, length()));
    
    // Discard trailing empty string tokens.
    for(int i = tokens.size()-1; i >= 0; i--) {
      if(tokens.get(i).equals("")) {
        tokens.remove(i);
      }
      else {
        break;
      }
    }
    
    return tokens.toArray(new StringSlice[0]);
  }
  
  
  
  /** 
   * Returns true iff o is a CharSequence representing the same sequence of 
   * characters as this. 
   */
  @Override
  public boolean equals(Object o) {
    if(o == null) {
      return false;
    }
    else if(o instanceof CharSequence) {
      CharSequence other = (CharSequence) o;
      
      if(this.length() == other.length()) {
        for(int i = 0; i < this.length(); i++) {
          if(this.charAt(i) != other.charAt(i)) {
            return false;
          }
        }
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }
  
  
  
  /** 
   * Returns the hash code for this slice. 
   * This is computed the same way hashcodes are computed for Strings. 
   */
  @Override
  public int hashCode() {
    int code = 0;
    for(int i=0; i < length(); i++) {
      int coeff = 1;
      for(int j = 1; j < length()-i; j++) {
        coeff*=31;
      }
      code += charAt(i)*coeff;
    }
    return code;
  }
}
