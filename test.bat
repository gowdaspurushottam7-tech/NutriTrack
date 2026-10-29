@echo off
setlocal
echo ===============================================================
echo   Running NutriTrack Automated System Test Suite...
echo ===============================================================

cd /d "%~dp0"

if exist "oracleJdk-27\bin\java.exe" (
    set "JAVA=%~dp0oracleJdk-27\bin\java.exe"
    set "JAVAC=%~dp0oracleJdk-27\bin\javac.exe"
) else (
    set "JAVA=java"
    set "JAVAC=javac"
)

if not exist "target\classes\com\nutrition\Main.class" (
    echo Building main project sources first...
    call "%~dp0build.bat"
)

if not exist "target\test-classes" mkdir "target\test-classes"

del /q "target\test_sources.txt" 2>nul
for /r "src\test\java" %%F in (*.java) do (
    set "FILEPATH=%%F"
    setlocal enabledelayedexpansion
    set "FILEPATH=!FILEPATH:\=/!"
    echo "!FILEPATH!" >> "target\test_sources.txt"
    endlocal
)

"%JAVAC%" -encoding UTF-8 -cp "target\classes" -d "target\test-classes" @"target\test_sources.txt"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Test compilation failed!
    exit /b %ERRORLEVEL%
)

"%JAVA%" -cp "target\classes;target\test-classes" com.nutrition.SystemTestSuite
