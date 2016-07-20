
@ECHO ON

rem Author: Kannan Balasubramanian
rem Last Updated date: 02/19/2008
rem Script for launching javatracer on windows only

IF "%BTRACE_HOME%" == "" GOTO SETBTRACE
rem IF "%JAVA_HOME%" == "" GOTO SETJAVAHOME

"%JAVA_HOME%"\bin\java -DPluginPath=e:\contest\JavaTracer\plugins\plugin.jar; -classpath "%BTRACE_HOME%\build\btrace-client.jar;%BTRACE_HOME%\lib\asm-3.0.jar;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\jconsole.jar;..\lib\javatracer.jar;." com.vmware.javatracer.JavaTracer

goto END

:SETBTRACE
echo "Please set BTRACE_HOME"
echo "e.g. BTRACE_HOME=c:\btrace"
GOTO END

:SETJAVAHOME
echo "Please set JAVA_HOME"
echo "e.g. set JAVA_HOME=d:\jdk1.6.0"
GOTO END

:END
