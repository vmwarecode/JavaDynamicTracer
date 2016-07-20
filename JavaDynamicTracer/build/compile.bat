
rem Author: Kannan Balasubramanian
rem Last Updated date: 02/14/2008
rem Script for building javatracer library

@echo on

echo ""

if "%JAVA_HOME%" == "" goto DEFINE_JAVA_HOME

if "%BTRACE_HOME%" == ""  goto DEFINE_BTRACE_HOME

if NOT EXIST "%OUTPUTDIR%" set OUTPUTDIR=%CD%\..

set BUILDDIR=%CD%
set SRCDIR=%CD%\..\src
set CLASSESDIR=%OUTPUTDIR%\classes
set LIBDIR=%OUTPUTDIR%\lib
if EXIST %CLASSESDIR% rm -rf %CLASSESDIR%

mkdir %CLASSESDIR%
if EXIST "%LIBDIR%\javatracer.jar" del %LIBDIR%\javatracer.jar
if NOT EXIST "%LIBDIR%" mkdir %LIBDIR%

echo "creating class files at %CLASSESDIR%..."
"%JAVA_HOME%"\bin\javac -classpath "%BTRACE_HOME%\build\btrace-client.jar;%BTRACE_HOME%\lib\asm-3.0.jar;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\jconsole.jar;." -d %CLASSESDIR% %SRCDIR%\com\vmware\javatracer\*.java

rem if [ "$?" != "0" ]; then
rem     "echo compilation failed..."
rem     exit 1
rem fi 
copy %SRCDIR%\com\vmware\javatracer\names.properties %CLASSESDIR%
copy %SRCDIR%\com\vmware\javatracer\JavaTracer.html %LIBDIR%
cd %CLASSESDIR%
echo "creating jar file at %LIBDIR%..."
"%JAVA_HOME%"\bin\jar -cvf %LIBDIR%\javatracer.jar com\vmware\javatracer\*.class names.properties

echo ""
echo "javatracer.jar file built successfully at %LIBDIR%..."
cd %BUILDDIR%


:DEFINE_JAVA_HOME
echo "Please set JAVA_HOME..."
echo "e.g. set JAVA_HOME=d:\jdk1.6.0"
goto END

:DEFINE_BTRACE_HOME
echo "Please set BTRACE_HOME..."
echo "e.g. set BTRACE_HOME=d:\btrace"
goto END

:END

