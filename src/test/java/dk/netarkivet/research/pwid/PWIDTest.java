package dk.netarkivet.research.pwid;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import dk.netarkivet.common.utils.Settings;
import junit.framework.TestCase;

public class PWIDTest extends TestCase {
    
    String pwid_sample = "urn:pwid:archive.org:2016-01-22T11:20:29Z:page:http://www.dr.dk";
    String date = "2016-01-22T11:20:29Z";
    
    @Before 
    public void setUp() {
        
        String settings = "settings_PwidResolverApplication.xml";
        File testSettings = getTestResourceFile(settings);
        System.setProperty("dk.netarkivet.settings.file", testSettings.getAbsolutePath());
        Settings.reload();
    }
    @Test
    public void testPwid() throws PwidParseException, ParseException {
        Date date1 = PWID.getDate(date);
        PWID pwid = new PWID("archive.org", "http://www.dr.dk", date1, PwidCoverage.page);
        PWID pwid1 = PWID.parsePWID(pwid_sample);
        Assert.assertTrue(pwid1.toString().equals(pwid.toString()), "the two pwids are different, but shouldn't be");
    }
    
    @Test
    public void testArchive() throws ParseException, PwidParseException {
        String waybackPrefix = "http://kb-prod-dab-01.kb.dk:8080/wayback/";
        String id = "dab.dk";
        Archive a = new Archive(id, waybackPrefix);
        Assert.assertTrue(a.getId().equals(id));
        Assert.assertTrue(a.getWaybackPrefix().equals(waybackPrefix));
        
        String dabDateString = "20150917090237";
        Date dabDate = PWID.getDateFormat(PWID.ARC_DATE_FORMAT).parse(dabDateString);   
        String TIMESTAMP = PWID.getDateFormat(PWID.WARC_DATE_FORMAT).format(dabDate);
        String dabPWIDString = "urn:pwid:dab.dk:" + TIMESTAMP + ":page:http://www.statensnet.dk/betaenkninger/1001-1200/1179-1989/1179-1989_pdf/searchable_1179-1989.pdf";
        PWID dabPWID = PWID.parsePWID(dabPWIDString);
        Assert.assertTrue(dabPWIDString.equals(dabPWID.toString()), "the two pwids are different, but shouldn't be");
        
        String url = Archive.getUrlFromPwid(a, dabPWID);
        String aUrl = a.getWaybackPrefix() + PWID.getDateFormat(PWID.ARC_DATE_FORMAT).format(dabPWID.getTimestamp()) + "/" + dabPWID.getUri(); 
        Assert.assertTrue(url.equals(aUrl));
    }
    
    @Test
    public void testArchiveSupplemental() {
        Map<String,Archive> archiveMap  = Archive.readKnownArchives();
        assertTrue(archiveMap.size() == 2);
        //System.out.println("Found " + archiveMap.size() + " archives:");
        assertTrue(archiveMap.containsKey("netarkivet.dk"));
        assertTrue(archiveMap.containsKey("dab.dk"));
    }
    
    public static File getTestResourceFile(String path) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url != null) {
            File file = new File(url.getPath());
            return file;
        } 
        return null;
    }
}
