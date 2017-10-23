package dk.netarkivet.research.webinterface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// pwid:archive.org:2016-01-22_11.20.29Z:page:http://www.dr.dk
public class PWID {
	
	public static void main(String[] args) throws ParseException, PwidParseException {
		String pwid1 = "pwid:archive.org:2016-01-22_11.20.29Z:page:http://www.dr.dk";
		
		PWID pwid = new PWID("archive.org", "http://www.dr.dk", new Date(), PwidCoverage.page);
		System.out.println(pwid);
		System.out.println(pwid1);
		pwid = PWID.parsePWID(pwid1);
		System.out.println(pwid);
		String pwid2 = "pwid:archive.org:2016-01-22_11.20.29Z:page:http://www.dr.dk";
		pwid = PWID.parsePWID(pwid2);
		System.out.println(pwid);
		Archive a = new Archive("dab.dk","http://kb-prod-dab-01.kb.dk:8080/wayback/");
		String dabDateString = "20150917090237";
		Date dabDate = getDateFormat(ARC_DATE_FORMAT).parse(dabDateString);
		//System.out.println(getDateFormat(ARC_DATE_FORMAT).format(dabDate));	
		String TIMESTAMP = getDateFormat(WARC_DATE_FORMAT).format(dabDate);
		System.out.println(TIMESTAMP);
		String dabPWIDString = "pwid:dab.dk:" + TIMESTAMP + ":page:http://www.statensnet.dk/betaenkninger/1001-1200/1179-1989/1179-1989_pdf/searchable_1179-1989.pdf";
		PWID dabPWID = PWID.parsePWID(dabPWIDString);
		String url = Archive.getUrlFromPwid(a, dabPWID);
		System.out.println(url);
	}	
	
	public static final String PWID_PREFIX = "pwid:";
	public static final String WARC_DATE_FORMAT = "yyyy-MM-dd_HH.mm.ss'Z'";
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
		return "pwid:" + archiveId + ":" + getDateFormat(WARC_DATE_FORMAT).format(timestamp) + ":" + coverage + ":" + uri; 
	}
	
	public static DateFormat getDateFormat(String pattern) {
		DateFormat warcDateFormat = new SimpleDateFormat(pattern);
		warcDateFormat.setLenient(false);
		warcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return warcDateFormat;
	}
	
	public static PWID parsePWID(String input) throws PwidParseException {
		String originalInput = input;
		// check1: string begins with "pwid:"
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
		// check3: contains a valid timestamp
		nextColonIndex = input.indexOf(':');
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
	
	
}
