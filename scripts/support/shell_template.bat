@echo off
setlocal

REM This script requires AGENT_APPLICATION to be set by the calling script
if not defined AGENT_APPLICATION (
    echo ERROR: AGENT_APPLICATION must be set by calling script
    exit /b 1
)

set MAVEN_PROFILE=enable-shell

REM Check for --docker-tools parameter
:parse_args
if "%~1"=="" goto end_parse
if "%~1"=="--docker-tools" (
    set MAVEN_PROFILE=enable-shell-mcp-client
    shift
    goto parse_args
)
REM Skip unknown parameters
shift
goto parse_args

:end_parse

REM Display feature availability warning
if "%MAVEN_PROFILE%"=="enable-shell" (
    powershell -Command "Write-Host 'WARNING: Only Basic Agent features will be available' -ForegroundColor Red"
    powershell -Command "Write-Host 'Use --docker-tools parameter to enable advanced Docker integration features' -ForegroundColor Yellow"
    echo.
)

call "%~dp0agent.bat"

endlocal