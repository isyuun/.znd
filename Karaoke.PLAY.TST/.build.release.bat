cd /d "%~dp0"
@echo OFF
call .build.title.bat
title %TITLE%%~n0
REM [ant網萄]
REM cmd /c ant release
REM copy /Y ".\bin\*-release.apk" .\
REM [gradle網萄]
@echo ON
cmd /c ..\gradlew --stacktrace -x test :%TITLE%:assembleRelease %1
copy /Y ".\build\outputs\apk\release\*-release.apk" ..\.apk
REM [PROGAUARD寥機]
@echo off
for /F "usebackq tokens=1,2 delims==" %%i in (`wmic os get LocalDateTime /VALUE 2^>NUL`) do if '.%%i.'=='.LocalDateTime.' set ldt=%%j
REM set ldt=%ldt:~0,4%-%ldt:~4,2%-%ldt:~6,2% %ldt:~8,2%:%ldt:~10,2%:%ldt:~12,6%
set ldt=%ldt:~0,4%%ldt:~4,2%%ldt:~6,2%
@echo Local date is [%ldt%]
set filedate=%ldt%
set filename=.\proguard\mapping.%filedate%.txt
mkdir .\proguard
REM [ant網萄]
REM copy /Y .\bin\proguard\mapping.txt %filename%
REM copy /Y .\bin\proguard\seeds.txt .\proguard\
REM copy /Y .\bin\proguard\usage.txt .\proguard\
REM [gradle寍圇]
copy /Y .\build\outputs\mapping\release\mapping.txt %filename%
copy /Y .\build\outputs\mapping\release\seeds.txt .\proguard\
copy /Y .\build\outputs\mapping\release\usage.txt .\proguard\
cmd /c .beep.bat
REM pause
