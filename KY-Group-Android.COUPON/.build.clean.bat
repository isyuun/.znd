cd /d "%~dp0"
@echo OFF
call .build.title.bat
title %TITLE%%~n0
REM del build.xml
REM cmd /c android update project -p ./ -n %TITLE% -s
REM cmd /c ant clean
cmd /c ..\gradlew -x test :%TITLE%:clean
cmd /c .beep.bat
REM pause
