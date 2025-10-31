@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "ENV_SCRIPT=%SCRIPT_DIR%check_env.bat"

call "%SCRIPT_DIR%check_env.bat"

if errorlevel 1 (
    echo Environment check failed. Exiting...
    exit /b 1
)

if not defined AGENT_APPLICATION (
    echo ERROR: AGENT_APPLICATION environment variable is NOT set
    echo Please set it like: set AGENT_APPLICATION=path\to\your\project
    exit /b 1
)

if not defined MAVEN_PROFILE (
    echo ERROR: MAVEN_PROFILE environment variable is NOT set
    echo Please set it in the calling script
    exit /b 1
)

set "POM_FILE=%AGENT_APPLICATION%\pom.xml"

REM Display what we're running
echo Starting application with profile: %MAVEN_PROFILE%
echo Application path: %AGENT_APPLICATION%

if defined SPRING_PROFILES_ACTIVE (
    echo Spring profiles: %SPRING_PROFILES_ACTIVE%
)

REM Run Maven Spring Boot application
call ..\..\mvnw -U -P %MAVEN_PROFILE% -f "%POM_FILE%" -Dmaven.test.skip=true -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE% clean spring-boot:run

endlocal
