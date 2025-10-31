#!/usr/bin/env bash

# Resolve the directory this script is in
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Set the path relative to this script's directory
export AGENT_APPLICATION="$SCRIPT_DIR/../../examples-kotlin"

# Check for help flag
if [[ "$1" == "--help" || "$1" == "-h" ]]; then
    echo
    echo "Kotlin Agent Shell - Available Options:"
    echo
    echo "  --help, -h              Show this help message"
    echo "  --observability         Enable observability features (Zipkin tracing)"
    echo "  --no-docker-tools       Disable Docker tool integration (basic features only)"
    echo
    echo "Examples:"
    echo "  ./shell.sh"
    echo "  ./shell.sh --observability"
    echo "  ./shell.sh --observability --no-docker-tools"
    echo
    exit 0
fi

# Call the shell_template.sh relative to this script's location
"$SCRIPT_DIR/../support/shell_template.sh" "$@"
