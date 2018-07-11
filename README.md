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

Download the latest zip-file (research-version.zip), and the associated PwidResolver.war from https://github.com/netarchivesuite/NAS-research/releases
```
VERSION=version
unzip research-$VERSION.zip
mkdir research-$VERSION/webpages
cp PwidResolver.war research-$VERSION/webpages/
```
## Starting the demo:
```
cd research-$VERSION
CLASSPATH="-cp lib/research-$VERSION.jar:lib/common-core-5.2.2.jar"
java $CLASSPATH -Ddk.netarkivet.settings.file=conf/settings_PwidResolverApplication.xml dk.netarkivet.common.webinterface.GUIApplication &
```

After this you can see the prototype on
http://localhost:8074/PwidResolver/PwidResolver-PwidResolver.jsp

Note: localhost in the above url can be replaced by the name of the server, where you are running this program.


