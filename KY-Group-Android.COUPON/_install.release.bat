cls
cd /d "%~dp0"
adb devices
REM cmd /c _adb.disconnect.bat
REM cmd /c _adb.connect.bat
REM cmd /c .\_install.uninstall.bat
for /r %%i in (.\build\outputs\apk\release\*-release.apk) do (set filename=%%~nxi)
adb install -r .\build\outputs\apk\release\%filename%
cmd /c _install.start.bat
REM pause
