cd /d "%~dp0"
adb devices
adb shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n kr.keumyoung.mukin/kr.keumyoung.mukin.activity._SplashScreenActivity
REM pause
