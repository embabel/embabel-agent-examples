@echo off

REM Simple script to make Embabel Agent shell scripts executable
REM No complex variables - just straightforward commands

echo 🔧 Making Embabel Agent shell scripts executable...

cd ..

REM Check if we're in the right directory
if not exist "scripts" (
    echo ❌ ERROR: scripts directory not found
    echo Please run this from the project root directory
    exit /b 1
)

REM Check if in git repository
git status >nul 2>&1
if errorlevel 1 (
    echo ❌ ERROR: Not in a git repository
    exit /b 1
)

echo.
echo 📂 Processing scripts...

REM Process each known script individually
if exist "scripts\kotlin\shell.sh" (
    echo Making executable: scripts/kotlin/shell.sh
    git update-index --chmod=+x "scripts/kotlin/shell.sh"
) else (
    echo ⚠️  Not found: scripts/kotlin/shell.sh
)

if exist "scripts\kotlin\mcp_server.sh" (
    echo Making executable: scripts/kotlin/mcp_server.sh
    git update-index --chmod=+x "scripts/kotlin/mcp_server.sh"
) else (
    echo ⚠️  Not found: scripts/kotlin/mcp_server.sh
)

if exist "scripts\java\shell.sh" (
    echo Making executable: scripts/java/shell.sh
    git update-index --chmod=+x "scripts/java/shell.sh"
) else (
    echo ⚠️  Not found: scripts/java/shell.sh
)

if exist "scripts\java\mcp_server.sh" (
    echo Making executable: scripts/java/mcp_server.sh
    git update-index --chmod=+x "scripts/java/mcp_server.sh"
) else (
    echo ⚠️  Not found: scripts/java/mcp_server.sh
)

if exist "scripts\support\agent.sh" (
    echo Making executable: scripts/support/agent.sh
    git update-index --chmod=+x "scripts/support/agent.sh"
) else (
    echo ⚠️  Not found: scripts/support/agent.sh
)

if exist "scripts\support\check_env.sh" (
    echo Making executable: scripts/support/check_env.sh
    git update-index --chmod=+x "scripts/support/check_env.sh"
) else (
    echo ⚠️  Not found: scripts/support/check_env.sh
)

if exist "scripts\support\shell_template.sh" (
    echo Making executable: scripts/support/shell_template.sh
    git update-index --chmod=+x "scripts/support/shell_template.sh"
) else (
    echo ⚠️  Not found: scripts/support/shell_template.sh
)

echo.
echo ✅ Processing complete!

REM Check if there are changes to commit
git diff --cached --quiet
if errorlevel 1 (
    echo.
    echo 📋 Scripts now marked as executable:
    git diff --cached --name-only
    
    echo.
    echo 💡 Next steps:
    echo    git commit -m "Make shell scripts executable"
    echo    git push
    
    echo.
    echo 🎉 Users will get executable scripts when they clone the repository!
) else (
    echo.
    echo ℹ️  All scripts were already executable - no changes needed
)

echo.
echo 🧪 Users can test with:
echo    cd scripts\kotlin ^&^& bash shell.sh --docker-tools
echo    cd scripts\java ^&^& bash shell.sh --docker-tools