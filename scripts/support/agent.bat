@echo off
setlocal

set SCRIPT_DIR=%~dp0
set ENV_SCRIPT=%SCRIPT_DIR%check_env.bat

call %SCRIPT_DIR%\check_env.bat

if errorlevel 1 (
    echo Environment check failed. Exiting...
    exit /b 1
)

if not defined AGENT_APPLICATION (
    echo ERROR: AGENT_APPLICATION environment variable is NOT set
    echo Please set it like: set AGENT_APPLICATION=path\to\your\project
    exit /b 1
)

set POM_FILE=%AGENT_APPLICATION%\pom.xml

cmd /c mvn -U -P %MAVEN_PROFILE% -f %POM_FILE% -Dmaven.test.skip=true spring-boot:run

endlocal