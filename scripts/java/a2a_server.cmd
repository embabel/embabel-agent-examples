@echo off
setlocal

set AGENT_APPLICATION=..\..\examples-java
set MAVEN_PROFILE=enable-agent-a2a-server

echo You can attach A2A Inspector and run tests!!

call ..\support\agent.bat

endlocal