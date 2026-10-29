@echo off
setlocal

echo ============================================================
echo  NutriTrack - Starting React Frontend (Dev Server)
echo ============================================================
echo.

REM Add portable Node.js to PATH so Vite/esbuild scripts can find 'node'
set "PATH=%~dp0nodejs;%PATH%"

REM Move into the frontend folder
cd /d "%~dp0frontend"

echo  Starting Vite dev server on http://localhost:5173
echo  For mobile access: http://YOUR_LAPTOP_IP:5173
echo.
echo  Press Ctrl+C to stop the server.
echo.

"..\nodejs\npm.cmd" run dev

pause
