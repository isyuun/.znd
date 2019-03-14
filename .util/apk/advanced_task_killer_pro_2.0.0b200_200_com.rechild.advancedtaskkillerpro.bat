cd /d "%~dp0"
REM cmd /c _adb.connect.bat
adb shell am start -a android.intent.action.MAIN -n com.rechild.advancedtaskkillerpro/.AdvancedTaskKiller
REM pause
