package dk.netarkivet.research.webinterface;

import java.util.HashMap;
import java.util.Map;

import dk.netarkivet.common.utils.Settings;

/*
<pwid>
<archives>
<archive>
<archiveid>dab.dk</archiveid>
<waybackprefix>http://kb-prod-dab-01.kb.dk:8080/wayback/</waybackprefix>
</archive>
<archive>
<archiveid>netarkivet.dk</archiveid>
<waybackprefix>http://kb-prod-wayback.kb.dk:8080/wayback/</waybackprefix>
</archive>
</archives>
</pwid>
</settings>
*/
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
	
	public static Map<String,Archive> readKnownArchives() {
		Map<String,Archive> archiveMap = new HashMap<String, Archive>();
		
		String[] archiveIds = Settings.getAll("settings.pwid.archives.archive.archiveid");
		String[] waybackprefixes = Settings.getAll("settings.pwid.archives.archive.waybackprefix");
		for (int i=0; i < archiveIds.length; i++){
			archiveMap.put(archiveIds[i], new Archive(archiveIds[i], waybackprefixes[i]));
		}
		return archiveMap;
	}
	
	public String toString() {
		return "Archive w/id='" + id + "', and waybackPrefix='" + waybackPrefix + "'";
	}
	
	public static void main(String[] args) {
		Map<String,Archive> archiveMap  = Archive.readKnownArchives();
		System.out.println("Found " + archiveMap.size() + " archives:");
		for (String key: archiveMap.keySet()) {
			System.out.println(archiveMap.get(key));
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Archive other = (Archive) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
