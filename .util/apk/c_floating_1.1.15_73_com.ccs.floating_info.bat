cd /d "%~dp0"
REM cmd /c _adb.connect.bat
adb shell am start -a android.intent.action.MAIN -n com.ccs.floating_info/com.ccs.floating_info.ActivityMain
REM pause
