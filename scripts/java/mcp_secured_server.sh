#!/usr/bin/env bash

# Resolve the directory this script is in
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Check for help flag
if [[ "$1" == "--help" || "$1" == "-h" ]]; then
    echo
    echo "Java Agent Secured MCP Server - Available Options:"
    echo
    echo "  --help, -h    Show this help message"
    echo "  --lazy-tools  Defer MCP client initialization to first use (JIT)."
    echo "                Required when downstream MCP servers authenticate via"
    echo "                user OAuth tokens forwarded in the Authorization header."
    echo
    echo "Examples:"
    echo "  ./mcp_secured_server.sh"
    echo "  ./mcp_secured_server.sh --lazy-tools"
    echo
    exit 0
fi

export AGENT_APPLICATION="$SCRIPT_DIR/../../examples-java"
export MAVEN_PROFILE=enable-secured-agent-mcp-server
export SPRING_PROFILES_ACTIVE=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --lazy-tools)
            export SPRING_PROFILES_ACTIVE=lazy-tools
            shift
            ;;
        *)
            shift
            ;;
    esac
done

if [[ "$SPRING_PROFILES_ACTIVE" == *"lazy-tools"* ]]; then
    echo -e "\033[33mINFO: Lazy tools profile is enabled\033[0m"
    echo -e "\033[36mMCP clients will initialize on first use (JIT), not at startup\033[0m"
    echo
fi

"$SCRIPT_DIR/../support/agent.sh"
