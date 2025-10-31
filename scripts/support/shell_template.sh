#!/usr/bin/env bash
# This script requires AGENT_APPLICATION to be set by the calling script
if [ -z "$AGENT_APPLICATION" ]; then
    echo "ERROR: AGENT_APPLICATION must be set by calling script"
    exit 1
fi

# Default: Docker tools enabled (reversed from original logic)
export MAVEN_PROFILE=enable-shell-mcp-client

# Default: No observability profile
export SPRING_PROFILES_ACTIVE=""

# Check for optional parameters
while [[ $# -gt 0 ]]; do
    case $1 in
        --no-docker-tools)
            export MAVEN_PROFILE=enable-shell
            shift
            ;;
        --observability)
            export SPRING_PROFILES_ACTIVE=observability
            shift
            ;;
        *)
            # Skip unknown parameters
            shift
            ;;
    esac
done

# Display startup message about Docker tools status
if [ "$MAVEN_PROFILE" = "enable-shell-mcp-client" ]; then
    echo -e "\033[32mINFO: Docker tools are enabled (default behavior)\033[0m"
    echo -e "\033[36mUse --no-docker-tools to disable Docker integration if needed\033[0m"
    echo
fi

# Display feature availability warning when tools are disabled
if [ "$MAVEN_PROFILE" = "enable-shell" ]; then
    echo -e "\033[31mWARNING: Only Basic Agent features will be available\033[0m"
    echo -e "\033[33mDocker tools have been disabled. Remove --no-docker-tools to enable advanced features\033[0m"
    echo
fi

# Display observability status
if [ -n "$SPRING_PROFILES_ACTIVE" ]; then
    echo -e "\033[35mINFO: Observability profile is enabled\033[0m"
    echo -e "\033[36mMake sure to run 'docker compose up' to start Zipkin trace collector\033[0m"
    echo
fi

# Get the directory where this script is located and call agent.sh
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
"$SCRIPT_DIR/../support/agent.sh"
