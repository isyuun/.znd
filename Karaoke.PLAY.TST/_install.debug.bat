cd /d "%~dp0"
REM cmd /c _adb.disconnect.bat
REM cmd /c _adb.connect.bat
adb devices
cmd /c ant uninstall
REM cmd /c ant installd
for /r %%i in (*-debug.apk) do (set filename=%%~nxi)
adb install -r ./%filename%
cmd /c _install.start.bat
REM pause
