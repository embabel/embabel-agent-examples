@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-java
set MAVEN_PROFILE=enable-mcp

call ..\support\agent.bat

endlocal