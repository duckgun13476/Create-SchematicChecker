@echo off
setlocal
rem The shared Gradle init script defaults to offline unless this switch is set.
set "MCSN_ALLOW_ONLINE_GRADLE=true"
call "%~dp0gradlew.bat" allworkPackageAllVersions --console=plain %*
if not errorlevel 1 exit /b 0

echo Direct online package build failed. Retrying through the Fabric Maven mirror...
call "%~dp0gradlew.bat" allworkPackageAllVersions --console=plain -Pcsc_fabric_maven_mirror_url=http://192.168.147.111:8081/repository/fabric/ %*
if not errorlevel 1 exit /b 0

echo Mirror package build failed. Retrying with the local Gradle cache...
set "MCSN_ALLOW_ONLINE_GRADLE="
call "%~dp0gradlew.bat" allworkPackageAllVersions --console=plain --offline %*
exit /b %ERRORLEVEL%
