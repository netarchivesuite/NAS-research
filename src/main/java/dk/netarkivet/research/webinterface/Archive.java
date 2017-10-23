package dk.netarkivet.research.webinterface;

public class Archive {
	String id;
	String waybackPrefix;
	
	public Archive(String id, String waybackPrefix){
		this.id = id;
		this.waybackPrefix = waybackPrefix;
	}
	
	public static String getUrlFromPwid(Archive a, PWID pwid) {
		return a.waybackPrefix + PWID.getDateFormat(PWID.ARC_DATE_FORMAT).format(pwid.timestamp) + "/" + pwid.uri; 
	}
	
}
