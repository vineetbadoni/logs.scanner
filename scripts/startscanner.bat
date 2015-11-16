@echo off

rem %~dp0 is location of current script 
set _REALPATH=%~dp0

set JAVA_OPTS=%*

"%JAVA_HOME%/bin/java" -D%JAVA_OPTS% -jar sqlprofiler.jar