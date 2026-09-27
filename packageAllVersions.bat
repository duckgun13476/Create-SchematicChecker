@echo off
setlocal
rem Loom merges Minecraft jars in place; the shared V: Gradle cache is not writable as ZIP FS.
rem Keep packaging on a local cache, with an explicit override for other machines.
if not defined CSC_PACKAGE_GRADLE_USER_HOME set "CSC_PACKAGE_GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "GRADLE_USER_HOME=%CSC_PACKAGE_GRADLE_USER_HOME%"
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
