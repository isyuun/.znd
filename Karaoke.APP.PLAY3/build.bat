cd /d "%~dp0"
@echo ON
REM ������������
set mydir="%~dp0"
SET mydir=%mydir:\=;%

for /F "tokens=* delims=;" %%i IN (%mydir%) DO call :FOLDER %%i
goto :BUILD

:FOLDER
if "%1"=="" (
    REM @echo %TITLE%
    goto :EOF
)

set TITLE=%1
SHIFT

goto :FOLDER

:BUILD
title %TITLE%
@echo [������Ʈ��Ȯ��]
@echo %TITLE%
@echo ON
cd /d "%~dp0"
REM [ant����]
REM del build.xml
REM cmd /c android update project -p ./ -n %TITLE% -s
REM pause
REM cmd /c ant clean
REM cmd /c ant release
REM copy /Y ".\bin\*.jar" .\%TITLE%.jar
REM [Ŭ���׺���]
REM cmd /c ..\gradlew :%TITLE%:clean
rd build /s /q
pause
REM [gradle����]
start "%TITLE%.DEBUG" ..\gradlew :%TITLE%:assembleDebug
start "%TITLE%.RELEASE" ..\gradlew :%TITLE%:assembleRelease
REM pause
