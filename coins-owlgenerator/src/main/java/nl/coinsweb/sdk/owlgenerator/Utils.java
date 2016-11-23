package nl.coinsweb.sdk.owlgenerator;

import com.hp.hpl.jena.ontology.OntModelSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Utils {

  protected static final Logger log = LoggerFactory.getLogger(Utils.class);



  /** List of Java reserved keywords, see <a href="http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html">this list</a>. */
  public static final String[] JAVA_KEYWORDS = {
      "abstract",    "continue",    "for",         "new",         "switch",           //"Object",
      "assert",      "default",     "goto",        "package",     "synchronized",     //"List",
      "boolean",     "do",          "if",          "private",     "this",
      "break",       "double",      "implements",  "protected",   "throw",
      "byte",        "else",        "import",      "public",      "throws",
      "case",        "enum",        "instanceof",  "return",      "transient",
      "catch",       "extends",     "int",         "short",       "try",
      "char",        "final",       "interface",   "static",      "void",
      "class",       "finally",     "long",        "strictfp",    "volatile",
      "const",       "float",       "native",      "super",       "while"
  };


  private static List<String> KEYWORD_LIST;
  static {
    KEYWORD_LIST = Arrays.asList(JAVA_KEYWORDS);
  }





  /** Converts to a legal Java identifier; capitalise first char if cap is true */
  public static String asLegalJavaID( String s, boolean cap ) {

    if(s == null || s.isEmpty()) {
      return null;
    }

    StringBuffer buf = new StringBuffer();
    int i = 0;

    // treat the first character specially - must be able to start a Java ID, may have to up-case
    try {
      for (; !Character.isJavaIdentifierStart( s.charAt( i )); i++) { /**/ }
    }
    catch (StringIndexOutOfBoundsException e) {
      return null;
    }
    buf.append( cap ? Character.toUpperCase( s.charAt( i ) ) : Character.toLowerCase(s.charAt(i)) );

    boolean doCapNow = false;
    // copy the remaining characters - replace non-legal chars with '_'
    for (++i; i < s.length(); i++) {
      char c = s.charAt( i );

      buf.append( (Character.isJavaIdentifierPart( c ) ? (doCapNow? Character.toUpperCase(c): c) : "")  );
      doCapNow = !Character.isJavaIdentifierPart( c );
    }

    // check for illegal keyword
    if (KEYWORD_LIST.contains( buf.toString() )) {
      buf.append( '_' );
    }


    String normalized = Normalizer.normalize(buf.toString(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    return normalized;
  }

  public static String capitalise(String input) {
    if(input == null) {
      return "";
    }

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);

      // No chars other than letters or numbers
      if(!Character.isLetter(c) && !Character.isDigit(c)) {
        continue;
      }

      if(Character.isUpperCase(c)) {
        buf.append("_"+c);
      } else {
        buf.append(Character.toUpperCase(c));
      }
    }
    String result = buf.toString();
    if(result.startsWith("_")) {
      return result.substring(1);
    }
    return result;
  }

  public static String cleanUp(String input) {
    if(input == null) {
      return "";
    }
    String noQuotes = input.replace("\"","").replace("\\","");
    String noNewlines = noQuotes.replace("\n"," ").replace("\r"," ");
    return noNewlines;
  }

  public static String namespaceUriToPackage(String input) {
    try {
      URL inputUrl = new URL(input);
      String result = "";

      // First part comes from host in url
      Iterator<String> parts = Arrays.asList(inputUrl.getHost().split("\\.")).iterator();
      while(parts.hasNext()) {
        String part = onlyAllowedChars(parts.next());
        if("www".equals(part)) {
          continue;
        }
        if("".equals(part)) {
          continue;
        }
        result = part + "." + result;
      }
      if(result.endsWith(".")) {
        result = result.substring(0, result.length()-1);
      }

      // Second part comes from path
      parts = Arrays.asList(inputUrl.getPath().split("(/|\\.)")).iterator();
      while(parts.hasNext()) {
        String part = onlyAllowedChars(parts.next());
        if("".equals(part) || "rdf".equals(part) || "owl".equals(part) || "xml".equals(part) || "html".equals(part)|| "htm".equals(part)) {
          continue;
        }
        result = result + "." + part;
      }

      if(result.length()>1) {
        return result;
      }
      return null;
    } catch (MalformedURLException e) {
      log.warn("Could not map "+input+" to namespace.");
      return null;
    }
  }

  public static String onlyAllowedChars(String input) {

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if(Character.isLetter(c)) {
        buf.append(Character.toLowerCase(c));
      }
      if(i>0 && Character.isDigit(c)) {
        buf.append(Character.toLowerCase(c));
      }
    }

    return buf.toString();

  }

  public static OntModelSpec reasonerFromString(String input) {
    if("OWL_MEM".equals(input)) {
      return OntModelSpec.OWL_MEM;
    }
    if("OWL_MEM_RDFS_INF".equals(input)) {
      return OntModelSpec.OWL_MEM_RDFS_INF;
    }
    if("OWL_MEM_TRANS_INF".equals(input)) {
      return OntModelSpec.OWL_MEM_TRANS_INF;
    }
    if("OWL_MEM_RULE_INF".equals(input)) {
      return OntModelSpec.OWL_MEM_RULE_INF;
    }
    if("OWL_MEM_MICRO_RULE_INF".equals(input)) {
      return OntModelSpec.OWL_MEM_MICRO_RULE_INF;
    }
    if("OWL_MEM_MINI_RULE_INF".equals(input)) {
      return OntModelSpec.OWL_MEM_MINI_RULE_INF;
    }
    if("OWL_DL_MEM".equals(input)) {
      return OntModelSpec.OWL_DL_MEM;
    }
    if("OWL_DL_MEM_RDFS_INF".equals(input)) {
      return OntModelSpec.OWL_DL_MEM_RDFS_INF;
    }
    if("OWL_DL_MEM_TRANS_INF".equals(input)) {
      return OntModelSpec.OWL_DL_MEM_TRANS_INF;
    }
    if("OWL_DL_MEM_RULE_INF".equals(input)) {
      return OntModelSpec.OWL_DL_MEM_RULE_INF;
    }
    if("OWL_LITE_MEM".equals(input)) {
      return OntModelSpec.OWL_LITE_MEM;
    }
    if("OWL_LITE_MEM_TRANS_INF".equals(input)) {
      return OntModelSpec.OWL_LITE_MEM_TRANS_INF;
    }
    if("OWL_LITE_MEM_RDFS_INF".equals(input)) {
      return OntModelSpec.OWL_LITE_MEM_RDFS_INF;
    }
    if("OWL_LITE_MEM_RULES_INF".equals(input)) {
      return OntModelSpec.OWL_LITE_MEM_RULES_INF;
    }
    if("RDFS_MEM".equals(input)) {
      return OntModelSpec.RDFS_MEM;
    }
    if("RDFS_MEM_TRANS_INF".equals(input)) {
      return OntModelSpec.RDFS_MEM_TRANS_INF;
    }
    if("RDFS_MEM_RDFS_INF".equals(input)) {
      return OntModelSpec.RDFS_MEM_RDFS_INF;
    }
    return null;
  }
}
