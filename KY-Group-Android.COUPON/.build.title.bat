cd /d "%~dp0"
@echo OFF
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
REM @echo ON
REM pause
