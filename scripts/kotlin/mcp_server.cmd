@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-kotlin
set MAVEN_PROFILE=enable-agent-mcp-server

call ..\support\agent.bat

endlocal