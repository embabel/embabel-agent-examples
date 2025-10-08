@echo off
setlocal
REM This script requires AGENT_APPLICATION to be set by the calling script
if not defined AGENT_APPLICATION (
    echo ERROR: AGENT_APPLICATION must be set by calling script
    exit /b 1
)

REM Default: Docker tools enabled (reversed from original logic)
set MAVEN_PROFILE=enable-shell-mcp-client

REM Check for --no-docker-tools parameter to disable tools
:parse_args
if "%~1"=="" goto end_parse
if "%~1"=="--no-docker-tools" (
    set MAVEN_PROFILE=enable-shell
    shift
    goto parse_args
)
REM Skip unknown parameters
shift
goto parse_args

:end_parse

REM Display startup message about Docker tools status
if "%MAVEN_PROFILE%"=="enable-shell-mcp-client" (
    powershell -Command "Write-Host 'INFO: Docker tools are enabled (default behavior)' -ForegroundColor Green"
    powershell -Command "Write-Host 'Use --no-docker-tools to disable Docker integration if needed' -ForegroundColor Cyan"
    echo.
)

REM Display feature availability warning when tools are disabled
if "%MAVEN_PROFILE%"=="enable-shell" (
    powershell -Command "Write-Host 'WARNING: Only Basic Agent features will be available' -ForegroundColor Red"
    powershell -Command "Write-Host 'Docker tools have been disabled. Remove --no-docker-tools to enable advanced features' -ForegroundColor Yellow"
    echo.
)

call %~dp0..\support\agent.bat
endlocal