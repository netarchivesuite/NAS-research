# NAS-research

Contains among other thinks a prototype PWID validation service 

## PWID syntax is
```
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
```
## PWID examples
```
urn:pwid:archive.org:2018-04-10T14:04:11Z:part:https://img1.wsimg.com/isteam/ip/263b8134-2928-4547-b94d-de51b81134fc/311d8d80-cd79-47ee-92bf -99d2cb991f2c.jpg
urn:pwid:archive.org:2018-02-22T11:54:11Z:page:https://ipres2018.org/
```
## Running the PWID demo

Download the latest zip-file (research-version.zip) of this code from https://github.com/netarchivesuite/NAS-research/releases
```
$VERSION=version
unzip research-$VERSION.zip
unzip research-webpages-$VERSION.zip -d research-$VERSION/webpages/ 
cd research-$VERSION
```
Download the 5.2.2 distributions of netarchivesuite
```
wget https://sbforge.org/nexus/service/local/repositories/releases/content/org/netarchivesuite/distribution/5.2.2/distribution-5.2.2.zip
unzip distribution-5.2.2.zip -d /tmp/NAS-5.2.2
cp -av /tmp/NAS-5.2.2/lib/* .
```

## Starting the demo:
CLASSPATH="-cp lib/research-$VERSION.jar:lib/common-core-5.2.2.jar"
java $CLASSPATH -Ddk.netarkivet.settings.file=conf/settings_PwidResolverApplication.xml dk.netarkivet.common.webinterface.GUIApplication &
```

After this you can see the prototype on 
http://localhost:8075/PwidResolver/PwidResolver-PwidResolver.jsp

localhost can be replaced by the server, where you are running this program


