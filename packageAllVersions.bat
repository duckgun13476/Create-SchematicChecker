@echo off
call "%~dp0gradlew.bat" allworkPackageAllVersions --console=plain %*
if not errorlevel 1 exit /b 0

echo Online package build failed. Retrying with the local Gradle cache...
call "%~dp0gradlew.bat" allworkPackageAllVersions --console=plain --offline %*
exit /b %ERRORLEVEL%
