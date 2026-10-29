@echo off
setlocal
echo ===============================================================
echo   Compiling Dietary Calorie Intake ^& Nutrition Log System...
echo ===============================================================

cd /d "%~dp0"

if exist "oracleJdk-27\bin\javac.exe" (
    set "JAVAC=%~dp0oracleJdk-27\bin\javac.exe"
) else (
    set "JAVAC=javac"
)

if not exist "target\classes" mkdir "target\classes"

del /q "target\sources.txt" 2>nul
for /r "src\main\java" %%F in (*.java) do (
    set "FILEPATH=%%F"
    setlocal enabledelayedexpansion
    set "FILEPATH=!FILEPATH:\=/!"
    echo "!FILEPATH!" >> "target\sources.txt"
    endlocal
)

"%JAVAC%" -encoding UTF-8 -d "target\classes" @"target\sources.txt"
if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] Compilation Successful!
) else (
    echo.
    echo [ERROR] Compilation Failed! Error code: %ERRORLEVEL%
)
