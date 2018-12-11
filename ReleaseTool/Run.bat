@echo off
if "%PROCESSOR_ARCHITECTURE%"=="x86" goto x86
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" goto x64
exit
:x64
java -Xms40m -Xmx512m -jar RT_64.jar
exit

:x86
java -Xms40m -Xmx512m -jar RT_32.jar