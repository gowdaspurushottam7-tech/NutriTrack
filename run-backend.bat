@echo off
setlocal
title NutriTrack Spring Boot Backend (Port 8080)
echo ===============================================================
echo   Starting NutriTrack Spring Boot REST API Backend...
echo   Backend will listen on: http://0.0.0.0:8080/api
echo ===============================================================

cd /d "%~dp0"

set "JAVA_HOME=%~dp0oracleJdk-27"
set "PATH=%~dp0oracleJdk-27\bin;%PATH%"

"%~dp0maven\bin\mvn.cmd" spring-boot:run
pause
