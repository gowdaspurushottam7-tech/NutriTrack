@echo off
setlocal
title NutriTrack - Dietary Calorie Intake & Nutrition Log System
echo ===============================================================
echo   NutriTrack: Dietary Calorie Intake ^& Nutrition Log System
echo ===============================================================

cd /d "%~dp0"

:: 1. Detect Java Runtime (Bundled JDK or system PATH)
if exist "%~dp0oracleJdk-27\bin\java.exe" (
    set "JAVA_EXE=%~dp0oracleJdk-27\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)

:: 2. Launch using executable JAR if present
if exist "%~dp0DietaryCalorieSystem.jar" (
    echo [INFO] Starting application from DietaryCalorieSystem.jar...
    "%JAVA_EXE%" -jar "%~dp0DietaryCalorieSystem.jar"
    goto :done
)

:: 3. Fallback to compiled class files
if not exist "%~dp0target\classes\com\nutrition\Main.class" (
    echo [INFO] Compiling project sources...
    call "%~dp0build.bat"
)

echo [INFO] Starting application from target/classes...
"%JAVA_EXE%" -cp "%~dp0target\classes" com.nutrition.Main

:done
if %ERRORLEVEL% neq 0 (
    echo.
    echo [NOTICE] Application closed. Return code: %ERRORLEVEL%
    pause
)
