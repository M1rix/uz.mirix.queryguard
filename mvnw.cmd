@echo off
setlocal enabledelayedexpansion
set MAVEN_VERSION=3.9.11
set DIST_ROOT=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_HOME=%DIST_ROOT%\apache-maven-%MAVEN_VERSION%
set MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd
if not exist "%MAVEN_BIN%" (
  if not exist "%DIST_ROOT%" mkdir "%DIST_ROOT%"
  set ARCHIVE=%DIST_ROOT%\apache-maven-%MAVEN_VERSION%-bin.tar.gz
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.tar.gz' -OutFile '%ARCHIVE%'"
  if errorlevel 1 exit /b 1
  tar -xzf "%ARCHIVE%" -C "%DIST_ROOT%"
  if errorlevel 1 exit /b 1
)
call "%MAVEN_BIN%" %*
