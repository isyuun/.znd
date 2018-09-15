@echo off
:START
REM adb shell com.google.android.youtube
adb shell dumpsys meminfo kr.kymedia.karaoke.play4
timeout /T 1
goto :START
REM pause
