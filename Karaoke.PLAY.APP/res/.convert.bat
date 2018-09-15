REM @ECHO OFF
cd /d "%~dp0"
for /r %%i in (*.png) do (convert -strip %%i %%i)
pause