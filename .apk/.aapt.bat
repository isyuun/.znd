cls
cd /d "%~dp0"
@ECHO off
if NOT exist "%1" goto :EXIT
REM for /F "usebackq tokens=1,2 delims==" %%i in (`wmic os get LocalDateTime /VALUE 2^>NUL`) do if '.%%i.'=='.LocalDateTime.' set ldt=%%j
REM @ECHO %ldt%
REM set ldt=%ldt:~0,4%%ldt:~4,2%%ldt:~6,2%
REM @ECHO [확인]Local date is [%ldt%]
REM set filedate=%ldt%

REM @ECHO %DATE:~10,4%-%DATE:~7,2%-%DATE:~4,2% %TIME:~0,2%:%TIME:~3,2%:%TIME:~6,2%
@ECHO [확인][원본]%1
set filepath=
set filename=
set fileextension=
set filedate=
set modidate=
set yyyy=
set mm=
set dd=
set hh=
set mn=
set ss=

FOR /f %%i IN ("%1") DO (
	@ECHO File Name Only       : %%~ni
	@ECHO File Extension       : %%~xi
	@ECHO Name in 8.3 notation : %%~sni
	@ECHO File Attributes      : %%~ai
	@ECHO Located on Drive     : %%~di
	@ECHO File Size            : %%~zi
	@ECHO Last-Modified Date   : %%~ti
	@ECHO Parent Folder        : %%~dpi
	@ECHO Fully Qualified Path : %%~fi
	@ECHO FQP in 8.3 notation  : %%~sfi

	set filepath=%%~dpi
	set filename=%%~ni
	set fileextension=%%~xi
	set filedate=%%~ti

	REM @ECHO %filedate%
	REM @ECHO %filepath%
	REM @ECHO %filename%
	REM @ECHO %fileextension%
)

set modidate=%filedate%
set modidate=%modidate: =%
set modidate=%modidate:-=%
set modidate=%modidate::=%
set modidate=%modidate:오전=%
set modidate=%modidate:오후=%
REM @ECHO %modidate%

set yyyy=%modidate:~0,4%
set mm=%modidate:~4,2%
set dd=%modidate:~6,2%
set hh=%modidate:~8,2%
set mn=%modidate:~10,2%
set ss=
REM @ECHO %yyyy%
REM @ECHO %mm%
REM @ECHO %dd%
REM @ECHO %hh%%
REM @ECHO %mn%%
REM @ECHO %ss%

REM set filedate=%filedate:~0,4%%filedate:~5,2%%filedate:~8,2%
set filedate=%modidate%
set filename=%filename:.=_%
set filename=%filename:-=_%
set filename=%filename%_%filedate%

REM set fullpath=%filepath%%filename%%fileextension%
REM @ECHO [확인]APK][path][%fullpath%]

@ECHO [확인]APK][date][%filedate%]
@ECHO [확인]APK][name][%filename%]
@ECHO [확인][%1]to[%filename%.apk]

REM pause

if exist %filename%.txt del "%filename%.txt"
if exist %filename%.cnt del "%filename%.cnt"

@ECHO on
@ECHO [확인][파일생성]
copy /Y "%1" "%filename%.apk"
aapt dump badging "%filename%.apk" >> "%filename%.txt"
java -jar .dex-method-counts.jar "%filename%.apk" >> "%filename%.cnt"
@ECHO [확인][파일날짜]
powershell (ls "%filename%.txt").LastWriteTime = (Get-Item "%filename%.apk").LastWriteTime
powershell (ls "%filename%.cnt").LastWriteTime = (Get-Item "%filename%.apk").LastWriteTime
REM pause
goto :EOF
:EXIT
@ECHO [오류]APK file not Exist [%1%]
pause
