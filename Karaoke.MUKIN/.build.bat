cd /d "%~dp0"
@echo OFF
rd build /s /q
REM [Å¬¸®´×ºôµå]
REM cmd /c .build.clean.bat
REM [¸±¸®½ººôµå]
REM start /d ".\" .build.release.bat
cmd /c .build.release.bat
REM [Å¬¸®´×ºôµå]
REM cmd /c .build.clean.bat
REM [µð¹ö±×ºôµå]
REM start /d ".\" .build.debug.bat
cmd /c .build.debug.bat
REM [Å¬¸®´×ºôµå]
REM cmd /c .build.clean.bat
REM pause
