@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-java
set MAVEN_PROFILE=enable-secured-agent-mcp-server

call ..\support\agent.bat

endlocal