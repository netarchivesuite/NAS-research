# NAS-research

Contains among other thinks a prototype PWID validation service 

## PWID syntax is
``
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
