cd /d "%~dp0"
@echo OFF
call .build.title.bat
title %TITLE%%~n0
REM [antºôµå]
REM cmd /c ant debug
REM copy /Y ".\bin\*-debug.apk" .\
REM [gradleºôµå]
@echo ON
cmd /c ..\gradlew --stacktrace -x test :%TITLE%:assembleDebug %1
copy /Y ".\build\outputs\apk\debug\*-debug.apk" ..\.apk
cmd /c .beep.bat
REM pause
