cls
cd /d "%~dp0"
@echo off
REM for %%a in (*.apk) do (
REM echo cmd /c .aapt.bat %%a
REM )


setlocal EnableDelayedExpansion

rem Populate the array with existent files in folder
set i=0
for %%a in (*.apk) do (
   set /A i+=1
   set list[!i!]=%%a
)
set Filesx=%i%

rem Display array elements
for /L %%i in (1,1,%Filesx%) do (
echo file number %%i: "!list[%%i]!"
cmd /c .aapt.bat !list[%%i]!
cmd /c .beep.bat
)
pause