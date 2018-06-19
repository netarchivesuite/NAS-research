package dk.netarkivet.research.pwid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/** 
 * Class for creating and parsing PWIDs like
 * urn:pwid:archive.org:2016-01-22T11:20:29Z:page:http://www.dr.dk
 * 
 * PWID URN syntax is
        pwid-urn = "urn" ":" pwid-NID ":" pwid-NSS 
        pwid-NID = "pwid"
        pwid-NSS = archive-id ":" archival-time ":" coverage-spec ":" archived-item
        archive-id = +( unreserved )
        archival-time = full-date datetime-delim full-pwid-time
        datetime-delim = "T"
        full-pwid-time = time-hour ":" time-minute ":" time-second "Z"
        coverage-spec = "part" / "page" / "subsite" / "site" 
                 / "collection" / "recording" / "snapshot"  / "other"
        archived-item = URI / archived-item-id
        archived-item-id = +( unreserved )

   Examples: 
 * urn:pwid:archive.org:2018-04-10T14:04:11Z:part:https://img1.wsimg.com/isteam/ip/263b8134-2928-4547-b94d-de51b81134fc/311d8d80-cd79-47ee-92bf -99d2cb991f2c.jpg
 * urn:pwid:archive.org:2018-02-22T11:54:11Z:page:https://ipres2018.org/
 * 
 * 
 * @author svc
 
 */
public class PWID {
    
    /** 
     * Main function to test if a given string is a valid PWID
     */
    public static void main(String[] args) throws PwidParseException {
      System.out.println("Args.length:"+ args.length );
      if (args.length != 1) {
          System.err.println("Missing pwid argument");
          System.exit(1);
      } else {
          String input = args[0];
          try {
              PWID pwid = PWID.parsePWID(input);
              System.out.println("The input '" + input + "' is a valid PWID: " + pwid.toString());
          } catch (PwidParseException e) {
              e.printStackTrace();
          }
      }
    }	

    public static final String PWID_PREFIX = "urn:pwid:";
    public static final String WARC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ARC_DATE_FORMAT = "yyyyMMddHHmmss";
    String archiveId;
    PwidCoverage coverage;
    Date timestamp;
    String uri;

    public PWID(String archiveId, String uri, Date timestamp, PwidCoverage coverage) {
        this.archiveId = archiveId;
        this.uri = uri;
        this.timestamp = timestamp;
        this.coverage = coverage;
    }

    public String toString() {
        return "urn:pwid:" + archiveId + ":" + getDateFormat(WARC_DATE_FORMAT).format(timestamp) + ":" + coverage + ":" + uri; 
    }

    public String getArchiveId() {
        return archiveId;
    }

    public PwidCoverage getcoverage() {
        return coverage;
    }

    public String getUri() {
        return uri;
    }

    public Date getTimestamp() {
        return (Date) timestamp.clone();
    }


    public static DateFormat getDateFormat(String pattern) {
        DateFormat warcDateFormat = new SimpleDateFormat(pattern);
        warcDateFormat.setLenient(false);
        warcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return warcDateFormat;
    }
    /**
     * Try to recognize the given input string a valid PWID 
     * @param input a given string
     * @return a correct PWID object
     * @throws PwidParseException If not valid PWID
     */
    public static PWID parsePWID(String input) throws PwidParseException {
        String originalInput = input;
        // check1: string begins with "urn:pwid:"
        if (!input.startsWith(PWID_PREFIX)) {
            throw new PwidParseException("The input '" + input + "' is not a valid pwid. It does not begin with 'pwid:'");
        }

        input = input.substring(PWID_PREFIX.length(), input.length());
        // check2: contains a valid archive_id"
        int nextColonIndex = input.indexOf(':');
        if (nextColonIndex == -1) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a archiveId");
        }
        String archiveId = input.substring(0, nextColonIndex);
        if (archiveId.isEmpty()) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a archiveId");
        }

        input = input.substring(nextColonIndex+1, input.length());
        // check3: contains a valid timestamp e.g '2018-04-10T14:04:11Z'
        nextColonIndex = input.indexOf(':');
        if (nextColonIndex == -1) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a timestamp");
        }
        nextColonIndex = input.indexOf(':', nextColonIndex+1);
        if (nextColonIndex == -1) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a timestamp");
        }
        nextColonIndex = input.indexOf(':', nextColonIndex+1);
        if (nextColonIndex == -1) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a timestamp");
        }
        String dateString = input.substring(0, nextColonIndex);
        if (dateString.isEmpty()) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a valid timestamp");
        }
        Date timestamp = null;
        try {
            timestamp = getDateFormat(WARC_DATE_FORMAT).parse(dateString);
        } catch (ParseException e) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a valid timestamp:" + e);
        }
        input = input.substring(nextColonIndex+1, input.length());
        // check3: contains a valid coverage
        nextColonIndex = input.indexOf(':');
        if (nextColonIndex == -1) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain coverage information");
        }
        String coverage = input.substring(0, nextColonIndex);
        if (coverage.isEmpty()) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. It does not contain a valid coverage");
        }
        PwidCoverage c = null;
        try {
            c = PwidCoverage.valueOf(coverage);
        } catch (IllegalArgumentException e) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. The string '" + coverage + "' is not a valid coverage.");
        }
        input = input.substring(nextColonIndex+1, input.length());
        String uri = input;
        if (uri.isEmpty()) {
            throw new PwidParseException("The input '" + originalInput + "' is not a valid pwid. The string '" + uri + "' is not a valid Uris.");
        }
        return new PWID(archiveId, uri, timestamp, c);
    }
    
    public static Date getDate(String timestamp) throws ParseException {
        return getDateFormat(WARC_DATE_FORMAT).parse(timestamp);
    }
    
}
