
#Author: Kannan Balasubramanian
#Last Updated date: 02/14/2008
#Script for building javatracer library

#set -x

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

if [ ! -d "${OUTPUTDIR}" ]; then
     OUTPUTDIR=`pwd`/..
fi

SRCDIR=`pwd`/../src
CLASSESDIR=${OUTPUTDIR}/classes
LIBDIR=${OUTPUTDIR}/lib
if [ -d ${CLASSESDIR} ]; then
     rm -rf ${CLASSESDIR}
fi
mkdir -p "${CLASSESDIR}"
if [ -d ${LIBDIR} ]; then
     rm ${LIBDIR}/javatracer.jar
else
     mkdir -p "${LIBDIR}"
fi

osname=`uname -s`
if [ "$osname" = "Windows_NT" ]; then
     PATH_SEP=";"
else
     PATH_SEP=:
fi

echo "creating class files at ${CLASSESDIR}..."
${JAVA_HOME}/bin/javac -classpath "${BTRACE_HOME}/build/btrace-client.jar${PATH_SEP}${BTRACE_HOME}/lib/asm-3.0.jar${PATH_SEP}${JAVA_HOME}/lib/tools.jar${PATH_SEP}${JAVA_HOME}/lib/jconsole.jar${PATH_SEP}." -d ${CLASSESDIR} ${SRCDIR}/com/vmware/javatracer/*.java

if [ "$?" != "0" ]; then
     "echo compilation failed..."
     exit 1
fi 
cd ${CLASSESDIR}
echo "creating jar file at ${LIBDIR}..."
${JAVA_HOME}/bin/jar -cvf ${LIBDIR}/javatracer.jar com/vmware/javatracer/*.class

echo ""
echo "javatracer.jar file built successfully at ${LIBDIR}..."
