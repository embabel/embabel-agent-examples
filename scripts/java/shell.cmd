@echo off
setlocal

REM Check for help flag
if "%~1"=="--help" goto show_help
if "%~1"=="-h" goto show_help

set AGENT_APPLICATION=..\..\examples-java

call ..\support\shell_template.bat %*

endlocal
exit /b 0

:show_help
echo.
echo Java Agent Shell - Available Options:
echo.
echo   --help, -h              Show this help message
echo   --observability         Enable observability features (Zipkin tracing)
echo   --no-docker-tools       Disable Docker tool integration (basic features only)
echo.
echo Examples:
echo   shell.cmd
echo   shell.cmd --observability
echo   shell.cmd --observability --no-docker-tools
echo.
exit /b 0
endlocal
