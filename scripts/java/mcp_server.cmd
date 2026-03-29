@echo off
setlocal

if "%~1"=="--help" goto show_help
if "%~1"=="-h" goto show_help

set AGENT_APPLICATION=..\..\examples-java
set MAVEN_PROFILE=enable-agent-mcp-server
set SPRING_PROFILES_ACTIVE=

:parse_args
if "%~1"=="" goto end_parse
if "%~1"=="--lazy-tools" (
    set SPRING_PROFILES_ACTIVE=lazy-tools
    shift
    goto parse_args
)
shift
goto parse_args

:end_parse

echo %SPRING_PROFILES_ACTIVE% | findstr /i "lazy-tools" >nul 2>&1
if not errorlevel 1 (
    powershell -Command "Write-Host 'INFO: Lazy tools profile is enabled' -ForegroundColor Yellow"
    powershell -Command "Write-Host 'MCP clients will initialize on first use (JIT), not at startup' -ForegroundColor Cyan"
    echo.
)

call ..\support\agent.bat
endlocal
exit /b 0

:show_help
echo.
echo Java Agent MCP Server - Available Options:
echo.
echo   --help, -h    Show this help message
echo   --lazy-tools  Defer MCP client initialization to first use (JIT).
echo                 Required when downstream MCP servers authenticate via
echo                 user OAuth tokens forwarded in the Authorization header.
echo.
echo Examples:
echo   mcp_server.cmd
echo   mcp_server.cmd --lazy-tools
echo.
exit /b 0
endlocal
