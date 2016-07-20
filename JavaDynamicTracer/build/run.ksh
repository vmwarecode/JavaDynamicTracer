
#Author: Kannan Balasubramanian
#Last Updated date: 02/14/2008
#Script for launching javatracer

set -x

echo ""
if [ -z "${JAVA_HOME}" ]; then
     echo "Please set JAVA_HOME..."
     echo "e.g. set JAVA_HOME=d:/jdk1.6.0"
     exit 1;
fi

if [ -z "${BTRACE_HOME}" ]; then
     echo "Please set BTRACE_HOME..."
     echo "e.g. set BTRACE_HOME=d:/btrace"
     exit 1;
fi

if [ -z "${JAVATRACE_HOME}" ]; then
     JAVATRACE_HOME=../lib/javatracer.jar
fi

osname=`uname -s`
if [ "$osname" = "Windows_NT" ]; then
     PATH_SEP=";"
else
     PATH_SEP=:
fi

SYS_FLAG=$*

${JAVA_HOME}/bin/java -cp "${BTRACE_HOME}/build/btrace-client.jar${PATH_SEP}${BTRACE_HOME}/lib/asm-3.0.jar${PATH_SEP}${JAVA_HOME}/lib/tools.jar${PATH_SEP}${JAVA_HOME}/lib/jconsole.jar${PATH_SEP}${JAVATRACE_HOME}${PATH_SEP}." -D${SYS_FLAG} com.vmware.javatracer.JavaTracer
