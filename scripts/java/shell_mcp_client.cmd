@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-java
set MAVEN_PROFILE=enable-shell-mcp-client

call ..\support\agent.bat

endlocal