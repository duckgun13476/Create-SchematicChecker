@echo off
setlocal
pushd "%~dp0gradle-runners\fg5-csc-1.16.5"
call gradlew.bat :forge-1.16.5-0.3.2g:runClient %*
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
