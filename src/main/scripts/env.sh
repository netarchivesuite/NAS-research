#!/bin/bash

# Sets the environment variables for the java processes of the Research tools.

# If JAVA_HOME is not set, use the java in the execution path
if [ ${JAVA_HOME} ] ; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# ${assembly.home.env.name} must point to home directory.
PRG="$0"

# need this for relative symlinks
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
done

${assembly.home.env.name}=`dirname "$PRG"`/..

# make it fully qualified
${assembly.home.env.name}=`cd "${assembly.home.env.name.ref}" && pwd`

# Set config folder
if [ -z "${assembly.config.env.name.ref}" ] ; then
  ${assembly.config.env.name}="${assembly.home.env.name.ref}/conf"
fi

# CP must contain a colon-separated list of resources used.
CP=${assembly.home.env.name.ref}/:${assembly.home.env.name.ref}/conf/
for i in `ls ${assembly.home.env.name.ref}/lib/*.*`
do
  CP=${CP}:${i}
done

if [ -z "${JAVA_OPTS}" ]; then
  JAVA_OPTS="-Xms256m -Xmx1024m"
fi
