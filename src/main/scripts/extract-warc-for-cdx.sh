#!/bin/bash
ProgDir=`dirname "$0"`
. "${ProgDir}/env.sh"

MAIN_CLASS=dk.netarkivet.research.NASExtractWarcFromCDX

if [ -z "${NAS_SETTINGS}" ]; then
  NAS_SETTINGS=$${assembly.config.env.name}/settings.xml
  if [ ! -f "${NAS_SETTINGS}" ]; then
    echo "A NAS settings file is needed. Either use environment variable \$NAS_SETTINGS or place it in $NAS_SETTINGS" 
    exit -1;
  fi
fi

# echo "NAS_SETTINGS: " $NAS_SETTINGS
# echo "MAIN_CLASS: " $MAIN_CLASS
# echo "JAVA: " $JAVA
# echo "CP: " $CP
# echo "JAVA_OPTS: " $JAVA_OPTS

cd ${assembly.home.env.name.ref}

"${JAVA}" ${JAVA_OPTS} -Ddk.netarkivet.settings.file=$NAS_SETTINGS -D${assembly.home.env.name}="${assembly.home.env.name.ref}" -cp "$CP" $MAIN_CLASS "$@"
