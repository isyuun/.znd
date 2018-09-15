SET "CDIR=%~dp0"
:: for loop requires removing trailing backslash from %~dp0 output
SET "CDIR=%CDIR:~0,-1%"
FOR %%i IN ("%CDIR%") DO SET "name=%%~nxi"

cd /d "%~dp0"
del build.xml
cmd /c android update project -p ./ -s  -n %name%
cmd /c ant clean
cmd /c ant release
pause
