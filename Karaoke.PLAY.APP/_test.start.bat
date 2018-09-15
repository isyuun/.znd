REM General
REM 	--help	Prints a simple usage guide.
REM 	-v	Each -v on the command line will increment the verbosity level. Level 0 (the default) provides little information beyond startup notification, test completion, and final results. Level 1 provides more details about the test as it runs, such as individual events being sent to your activities. Level 2 provides more detailed setup information such as activities selected or not selected for testing.
REM Events
REM 	-s <seed>	Seed value for pseudo-random number generator. If you re-run the Monkey with the same seed value, it will generate the same sequence of events.
REM 	--throttle <milliseconds>	Inserts a fixed delay between events. You can use this option to slow down the Monkey. If not specified, there is no delay and the events are generated as rapidly as possible.
REM 	--pct-touch <percent>	Adjust percentage of touch events. (Touch events are a down-up event in a single place on the screen.)
REM 	--pct-motion <percent>	Adjust percentage of motion events. (Motion events consist of a down event somewhere on the screen, a series of pseudo-random movements, and an up event.)
REM 	--pct-trackball <percent>	Adjust percentage of trackball events. (Trackball events consist of one or more random movements, sometimes followed by a click.)
REM 	--pct-nav <percent>	Adjust percentage of "basic" navigation events. (Navigation events consist of up/down/left/right, as input from a directional input device.)
REM 	--pct-majornav <percent>	Adjust percentage of "major" navigation events. (These are navigation events that will typically cause actions within your UI, such as the center button in a 5-way pad, the back key, or the menu key.)
REM 	--pct-syskeys <percent>	Adjust percentage of "system" key events. (These are keys that are generally reserved for use by the system, such as Home, Back, Start Call, End Call, or Volume controls.)
REM 	--pct-appswitch <percent>	Adjust percentage of activity launches. At random intervals, the Monkey will issue a startActivity() call, as a way of maximizing coverage of all activities within your package.
REM 	--pct-anyevent <percent>	Adjust percentage of other types of events. This is a catch-all for all other types of events such as keypresses, other less-used buttons on the device, and so forth.
REM Constraints
REM 	-p <allowed-package-name>	If you specify one or more packages this way, the Monkey will only allow the system to visit activities within those packages. If your application requires access to activities in other packages (e.g. to select a contact) you'll need to specify those packages as well. If you don't specify any packages, the Monkey will allow the system to launch activities in all packages. To specify multiple packages, use the -p option multiple times ? one -p option per package.
REM 	-c <main-category>	If you specify one or more categories this way, the Monkey will only allow the system to visit activities that are listed with one of the specified categories. If you don't specify any categories, the Monkey will select activities listed with the category Intent.CATEGORY_LAUNCHER or Intent.CATEGORY_MONKEY. To specify multiple categories, use the -c option multiple times ? one -c option per category.
REM Debugging
REM 	--dbg-no-events	When specified, the Monkey will perform the initial launch into a test activity, but will not generate any further events. For best results, combine with -v, one or more package constraints, and a non-zero throttle to keep the Monkey running for 30 seconds or more. This provides an environment in which you can monitor package transitions invoked by your application.
REM 	--hprof	If set, this option will generate profiling reports immediately before and after the Monkey event sequence. This will generate large (~5Mb) files in data/misc, so use with care. See Traceview for more information on trace files.
REM 	--ignore-crashes	Normally, the Monkey will stop when the application crashes or experiences any type of unhandled exception. If you specify this option, the Monkey will continue to send events to the system, until the count is completed.
REM 	--ignore-timeouts	Normally, the Monkey will stop when the application experiences any type of timeout error such as a "Application Not Responding" dialog. If you specify this option, the Monkey will continue to send events to the system, until the count is completed.
REM 	--ignore-security-exceptions	Normally, the Monkey will stop when the application experiences any type of permissions error, for example if it attempts to launch an activity that requires certain permissions. If you specify this option, the Monkey will continue to send events to the system, until the count is completed.
REM 	--kill-process-after-error	Normally, when the Monkey stops due to an error, the application that failed will be left running. When this option is set, it will signal the system to stop the process in which the error occurred. Note, under a normal (successful) completion, the launched process(es) are not stopped, and the device is simply left in the last state after the final event.
REM 	--monitor-native-crashes	Watches for and reports crashes occurring in the Android system native code. If --kill-process-after-error is set, the system will stop.
REM 	--wait-dbg	Stops the Monkey from executing until a debugger is attached to it.

cd /d "%~dp0"
@echo off
for /F "usebackq tokens=1,2 delims==" %%i in (`wmic os get LocalDateTime /VALUE 2^>NUL`) do if '.%%i.'=='.LocalDateTime.' set ldt=%%j
REM set ldt=%ldt:~0,4%-%ldt:~4,2%-%ldt:~6,2% %ldt:~8,2%:%ldt:~10,2%:%ldt:~12,6%
set ldt=%ldt:~0,4%%ldt:~4,2%%ldt:~6,2%
@echo Local date is [%ldt%]
set filedate=%ldt%
set filename=monkey.%filedate%
if exist %filename%.txt del %filename%.txt
if exist %filename%.dmp.txt del %filename%.dmp.txt
@echo "[È®ÀÎ]filename%.txt"
@echo on
REM ex)adb shell monkey -v --kill-process-after-error --monitor-native-crashes --pct-touch 0 --pct-motion 0 --pct-trackball 0 --pct-nav 0 --pct-majornav 0 --pct-syskeys 0 --pct-appswitch 0 -p com.google.android.youtube 10000
adb shell monkey --throttle 100 -v --ignore-timeouts --kill-process-after-error --monitor-native-crashes --pct-trackball 0 --pct-nav 0 --pct-majornav 0 --pct-syskeys 0 -p kr.kymedia.karaoke.play4 -c android.intent.category.APP_MUSIC 100000
REM adb shell dumpsys meminfo kr.kymedia.karaoke.play4 >> %filename%.dmp.txt
REM if exist %filename%.txt start %~dp0%filename%.txt
REM if exist %filename%.dmp.txt start %~dp0%filename%.dmp.txt
REM pause
