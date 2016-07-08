#!/bin/bash
ProgDir=`dirname "$0"`  #dirname function på script navn ($0) - `` betyder at linux funktion skal kaldes - det er bin folder i installationsdir
. "${ProgDir}/env.sh"   #src main - er scripts der lægges under bin - sætter ande variable

MAIN_CLASS=dk.netarkivet.research.ExtLinkAnalyser

cd ${assembly.home.env.name.ref}

"${JAVA}" ${JAVA_OPTS} -D${assembly.home.env.name}="${assembly.home.env.name.ref}" -cp "$CP" $MAIN_CLASS "$@"  #$@ betyder alle argumenter der er givet til scriptet
