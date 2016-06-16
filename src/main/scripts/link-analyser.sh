#!/bin/bash
ProgDir=`dirname "$0"`
. "${ProgDir}/env.sh"

MAIN_CLASS=dk.netarkivet.research.ExtLinkAnalyser

cd ${assembly.home.env.name.ref}

"${JAVA}" ${JAVA_OPTS} -D${assembly.home.env.name}="${assembly.home.env.name.ref}" -cp "$CP" $MAIN_CLASS "$@"
