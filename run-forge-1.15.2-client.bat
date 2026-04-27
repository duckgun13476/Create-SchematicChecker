@echo off
setlocal
pushd "%~dp0gradle-runners\fg5-csc-1.16.5"
call gradlew.bat :forge-1.15.2-0.3.1:runClient %*
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
