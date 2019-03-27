cd /d "%~dp0"
adb devices
adb shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n kr.keumyoung.karaoke.mukin/kr.keumyoung.karaoke.mukin.apps._main
REM pause
