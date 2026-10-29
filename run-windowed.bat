@echo off
setlocal
cd /d "%~dp0"

if exist "oracleJdk-27\bin\javaw.exe" (
    set "JAVA=%~dp0oracleJdk-27\bin\javaw.exe"
) else (
    set "JAVA=javaw"
)

if not exist "target\classes\com\nutrition\Main.class" (
    call "%~dp0build.bat"
)

start "" "%JAVA%" -cp "target\classes" com.nutrition.Main
