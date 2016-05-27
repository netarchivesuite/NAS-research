#!/bin/bash
ProgDir=`dirname "$0"`
. "${ProgDir}/env.sh"

MAIN_CLASS=dk.netarkivet.research.NASExtractCDXForURLs

# echo "NAS_SETTINGS: " $NAS_SETTINGS
# echo "MAIN_CLASS: " $MAIN_CLASS
# echo "JAVA: " $JAVA
# echo "CP: " $CP
# echo "JAVA_OPTS: " $JAVA_OPTS

cd ${assembly.home.env.name.ref}

"${JAVA}" ${JAVA_OPTS} -D${assembly.home.env.name}="${assembly.home.env.name.ref}" -cp "$CP" $MAIN_CLASS "$@"
