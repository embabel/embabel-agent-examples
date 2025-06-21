@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-kotlin
set MAVEN_PROFILE=enable-mcp

call ..\support\agent.bat

endlocal