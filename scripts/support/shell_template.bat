@echo off
setlocal
REM This script requires AGENT_APPLICATION to be set by the calling script
if not defined AGENT_APPLICATION (
    echo ERROR: AGENT_APPLICATION must be set by calling script
    exit /b 1
)

REM Default: Docker tools enabled (reversed from original logic)
set MAVEN_PROFILE=enable-shell-mcp-client

REM Default: No additional Spring profiles
set SPRING_PROFILES_ACTIVE=

REM Check for optional parameters
:parse_args
if "%~1"=="" goto end_parse
if "%~1"=="--no-docker-tools" (
    set MAVEN_PROFILE=enable-shell
    shift
    goto parse_args
)
if "%~1"=="--observability" (
    call :append_profile observability
    shift
    goto parse_args
)
if "%~1"=="--lazy-tools" (
    call :append_profile lazy-tools
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

REM Display observability status
echo %SPRING_PROFILES_ACTIVE% | findstr /i "observability" >nul 2>&1
if not errorlevel 1 (
    powershell -Command "Write-Host 'INFO: Observability profile is enabled' -ForegroundColor Magenta"
    powershell -Command "Write-Host 'Make sure to run docker compose up to start Zipkin trace collector' -ForegroundColor Cyan"
    echo.
)

REM Display lazy-tools status
echo %SPRING_PROFILES_ACTIVE% | findstr /i "lazy-tools" >nul 2>&1
if not errorlevel 1 (
    powershell -Command "Write-Host 'INFO: Lazy tools profile is enabled' -ForegroundColor Yellow"
    powershell -Command "Write-Host 'MCP clients will initialize on first use (JIT), not at startup' -ForegroundColor Cyan"
    echo.
)

call %~dp0..\support\agent.bat
endlocal
exit /b 0

REM Helper: append a Spring profile to SPRING_PROFILES_ACTIVE (comma-separated)
:append_profile
if "%SPRING_PROFILES_ACTIVE%"=="" (
    set SPRING_PROFILES_ACTIVE=%~1
) else (
    set SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%,%~1
)
exit /b 0
