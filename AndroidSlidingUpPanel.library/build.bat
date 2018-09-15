cd /d "%~dp0"
@echo ON
REM 폴더명가져오리
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
@echo [프로젝트명확인]
@echo %TITLE%
@echo ON
cd /d "%~dp0"
REM [ant빌드]
REM del build.xml
REM cmd /c android update project -p ./ -n %TITLE% -s
REM pause
REM cmd /c ant clean
REM cmd /c ant release
REM copy /Y ".\bin\*.jar" .\%TITLE%.jar
REM [클리닝빌드]
REM cmd /c ..\gradlew :%TITLE%:clean
rd build /s /q
pause
REM [gradle빌드]
start "%TITLE%.DEBUG" ..\gradlew :%TITLE%:assembleDebug
start "%TITLE%.RELEASE" ..\gradlew :%TITLE%:assembleRelease
REM pause
