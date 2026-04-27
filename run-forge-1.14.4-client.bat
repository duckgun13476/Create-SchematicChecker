@echo off
setlocal
pushd "%~dp0gradle-runners\fg3-csc-1.14.4"
call gradlew.bat :forge-1.14.4-0.2.3:runClient %*
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
