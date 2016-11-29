package nl.coinsweb.sdk;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class CoinsResource {

  /*
   * Don't use the local date format, this is not accepted by Jena
   */
  public static Literal nowLocal(Model model) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    XSDDateTime dateTime = new XSDDateTime(calendar);
    Literal now = model.createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);
    return now;
  }

  public static Literal nowUTC(Model model) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    XSDDateTime dateTime = new XSDDateTime(calendar);
    Literal now = model.createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);
    return now;
  }
}
