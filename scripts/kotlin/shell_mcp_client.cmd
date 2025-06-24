@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-kotlin
set MAVEN_PROFILE=enable-shell-mcp-client

call ..\support\agent.bat

endlocal