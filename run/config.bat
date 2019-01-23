set JDK_DIR="C:\Program Files\Java\jdk1.8.0_131"

set PRJ_NAME=LeaderBoardServer
set PRJ_HOME=%cd%
set JAVAC=%JDK_DIR%\bin\javac
set JAVA=%JDK_DIR%\bin\java
set JAR=%JDK_DIR%\bin\jar
set DIR_TOOL=%PRJ_HOME%\tools
set DIR_SRC=%PRJ_HOME%\src
set DIR_BIN=%PRJ_HOME%\bin
set DIR_LIB=%PRJ_HOME%\libs
set DIR_REDIS=%DIR_TOOL%\Redis-x64-3.2.100
set DIR_DATABASE=%PRJ_HOME%\_database
set DIR_RELEASE=%PRJ_HOME%\_release_
set DIR_RELEASE_LOG=%DIR_RELEASE%\_log

set BUILD_VERSION=0.0.1
set JAR_NAME=%PRJ_NAME%-%BUILD_VERSION%
set MAIN_CLASS=MainClass

set LIBRARY=%DIR_LIB%\netty-all-4.1.22.Final.jar
set LIBRARY=%LIBRARY%;%DIR_LIB%\commons-pool2-2.4.2.jar
set LIBRARY=%LIBRARY%;%DIR_LIB%\gson-2.8.2.jar
set LIBRARY=%LIBRARY%;%DIR_LIB%\jedis-2.9.0.jar
set LIBRARY=%LIBRARY%;%DIR_LIB%\jettison-1.3.8.jar
