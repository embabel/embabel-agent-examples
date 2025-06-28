#!/usr/bin/env bash

# This script requires AGENT_APPLICATION to be set by the calling script
if [ -z "$AGENT_APPLICATION" ]; then
    echo "ERROR: AGENT_APPLICATION must be set by calling script"
    exit 1
fi

export MAVEN_PROFILE=enable-shell

# Check for --docker-tools parameter
while [[ $# -gt 0 ]]; do
    case $1 in
        --docker-tools)
            export MAVEN_PROFILE=enable-shell-mcp-client
            shift
            ;;
        *)
            # Skip unknown parameters
            shift
            ;;
    esac
done

# Display feature availability warning
if [ "$MAVEN_PROFILE" = "enable-shell" ]; then
    echo -e "\033[31mWARNING: Only Basic Agent features will be available\033[0m"
    echo -e "\033[33mUse --docker-tools parameter to enable advanced Docker integration features\033[0m"
    echo
fi

# Get the directory where this script is located and call agent.sh
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
"$SCRIPT_DIR/../support/agent.sh"