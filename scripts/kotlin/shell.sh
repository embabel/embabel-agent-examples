#!/usr/bin/env bash

# Resolve the directory this script is in
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Set the path relative to this script's directory
export AGENT_APPLICATION="$SCRIPT_DIR/../../examples-kotlin"

# Call the shell_template.sh relative to this script's location
"$SCRIPT_DIR/../support/shell_template.sh" "$@"