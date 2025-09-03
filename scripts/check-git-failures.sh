#!/bin/bash

# Git Commit Failure Log Checker
# This script helps check for git commit failures from the command line

set -euo pipefail

PROJECT_ROOT="${KERUTA_PROJECT_ROOT:-/home/coder/keruta/keruta}"
FAILURE_LOG_FILE="${PROJECT_ROOT}/logs/git-commit-failures.json"
REGULAR_LOG_FILE="${PROJECT_ROOT}/logs/submodule-operations.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  list                    List all git commit failures"
    echo "  count                   Count total git commit failures"
    echo "  recent [N]              Show N most recent failures (default: 5)"
    echo "  task <task_id>          Show failures for a specific task"
    echo "  session <session_id>    Show failures for a specific session"
    echo "  clear                   Clear all git commit failure logs"
    echo "  tail                    Follow the regular log file in real-time"
    echo ""
    echo "Options:"
    echo "  --json                  Output in JSON format"
    echo "  --no-color             Disable colored output"
    echo ""
}

# Check if jq is available
has_jq() {
    command -v jq >/dev/null 2>&1
}

# Format timestamp for display
format_timestamp() {
    local timestamp="$1"
    if command -v date >/dev/null 2>&1; then
        date -d "$timestamp" '+%Y-%m-%d %H:%M:%S' 2>/dev/null || echo "$timestamp"
    else
        echo "$timestamp"
    fi
}

# Pretty print a failure entry
print_failure() {
    local failure="$1"
    local use_color="${2:-true}"
    
    if has_jq; then
        local timestamp=$(echo "$failure" | jq -r '.timestamp')
        local task_id=$(echo "$failure" | jq -r '.taskId')
        local session_id=$(echo "$failure" | jq -r '.sessionId')
        local submodule=$(echo "$failure" | jq -r '.submodulePath')
        local commit_msg=$(echo "$failure" | jq -r '.commitMessage')
        local error_output=$(echo "$failure" | jq -r '.errorOutput')
        local working_dir=$(echo "$failure" | jq -r '.workingDirectory // "unknown"')
        
        if [[ "$use_color" == "true" ]]; then
            echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
            echo -e "${RED}Git Commit Failure${NC}"
            echo -e "${BLUE}Time:${NC} $(format_timestamp "$timestamp")"
            echo -e "${BLUE}Task ID:${NC} $task_id"
            echo -e "${BLUE}Session ID:${NC} $session_id"
            echo -e "${BLUE}Submodule:${NC} $submodule"
            echo -e "${BLUE}Working Directory:${NC} $working_dir"
            echo -e "${BLUE}Commit Message:${NC} $commit_msg"
            echo -e "${YELLOW}Error Output:${NC}"
            echo "$error_output" | sed 's/^/  /'
        else
            echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            echo "Git Commit Failure"
            echo "Time: $(format_timestamp "$timestamp")"
            echo "Task ID: $task_id"
            echo "Session ID: $session_id"
            echo "Submodule: $submodule"
            echo "Working Directory: $working_dir"
            echo "Commit Message: $commit_msg"
            echo "Error Output:"
            echo "$error_output" | sed 's/^/  /'
        fi
        echo ""
    else
        echo "$failure"
        echo ""
    fi
}

# List all failures
list_failures() {
    local output_json="${1:-false}"
    local use_color="${2:-true}"
    
    if [[ ! -f "$FAILURE_LOG_FILE" ]]; then
        if [[ "$use_color" == "true" ]]; then
            echo -e "${GREEN}No git commit failures recorded.${NC}"
        else
            echo "No git commit failures recorded."
        fi
        return 0
    fi
    
    if [[ "$output_json" == "true" ]]; then
        cat "$FAILURE_LOG_FILE"
        return 0
    fi
    
    if has_jq; then
        local failures=$(jq -r '.[]' "$FAILURE_LOG_FILE" 2>/dev/null || echo "[]")
        if [[ "$failures" != "[]" ]]; then
            echo "$failures" | while IFS= read -r failure; do
                print_failure "$failure" "$use_color"
            done
        else
            if [[ "$use_color" == "true" ]]; then
                echo -e "${GREEN}No git commit failures recorded.${NC}"
            else
                echo "No git commit failures recorded."
            fi
        fi
    else
        cat "$FAILURE_LOG_FILE"
    fi
}

# Count failures
count_failures() {
    if [[ ! -f "$FAILURE_LOG_FILE" ]]; then
        echo "0"
        return 0
    fi
    
    if has_jq; then
        jq '. | length' "$FAILURE_LOG_FILE" 2>/dev/null || echo "0"
    else
        # Crude count by counting opening braces that start a failure entry
        grep -c '"timestamp":' "$FAILURE_LOG_FILE" 2>/dev/null || echo "0"
    fi
}

# Show recent failures
show_recent() {
    local count="${1:-5}"
    local output_json="${2:-false}"
    local use_color="${3:-true}"
    
    if [[ ! -f "$FAILURE_LOG_FILE" ]]; then
        if [[ "$use_color" == "true" ]]; then
            echo -e "${GREEN}No git commit failures recorded.${NC}"
        else
            echo "No git commit failures recorded."
        fi
        return 0
    fi
    
    if [[ "$output_json" == "true" ]]; then
        if has_jq; then
            jq ".[-$count:]" "$FAILURE_LOG_FILE" 2>/dev/null || echo "[]"
        else
            tail -n "$count" "$FAILURE_LOG_FILE"
        fi
        return 0
    fi
    
    if has_jq; then
        local failures=$(jq -r ".[-$count:] | .[]" "$FAILURE_LOG_FILE" 2>/dev/null || echo "")
        if [[ -n "$failures" ]]; then
            echo "$failures" | while IFS= read -r failure; do
                print_failure "$failure" "$use_color"
            done
        else
            if [[ "$use_color" == "true" ]]; then
                echo -e "${GREEN}No git commit failures recorded.${NC}"
            else
                echo "No git commit failures recorded."
            fi
        fi
    else
        tail -n "$count" "$FAILURE_LOG_FILE"
    fi
}

# Filter by task ID
filter_by_task() {
    local task_id="$1"
    local output_json="${2:-false}"
    local use_color="${3:-true}"
    
    if [[ ! -f "$FAILURE_LOG_FILE" ]]; then
        if [[ "$use_color" == "true" ]]; then
            echo -e "${GREEN}No git commit failures recorded.${NC}"
        else
            echo "No git commit failures recorded."
        fi
        return 0
    fi
    
    if has_jq; then
        local failures=$(jq -r ".[] | select(.taskId == \"$task_id\")" "$FAILURE_LOG_FILE" 2>/dev/null || echo "")
        if [[ -n "$failures" ]]; then
            if [[ "$output_json" == "true" ]]; then
                echo "$failures"
            else
                echo "$failures" | while IFS= read -r failure; do
                    print_failure "$failure" "$use_color"
                done
            fi
        else
            if [[ "$use_color" == "true" ]]; then
                echo -e "${GREEN}No git commit failures found for task: $task_id${NC}"
            else
                echo "No git commit failures found for task: $task_id"
            fi
        fi
    else
        grep "\"$task_id\"" "$FAILURE_LOG_FILE" 2>/dev/null || {
            if [[ "$use_color" == "true" ]]; then
                echo -e "${GREEN}No git commit failures found for task: $task_id${NC}"
            else
                echo "No git commit failures found for task: $task_id"
            fi
        }
    fi
}

# Filter by session ID
filter_by_session() {
    local session_id="$1"
    local output_json="${2:-false}"
    local use_color="${3:-true}"
    
    if [[ ! -f "$FAILURE_LOG_FILE" ]]; then
        if [[ "$use_color" == "true" ]]; then
            echo -e "${GREEN}No git commit failures recorded.${NC}"
        else
            echo "No git commit failures recorded."
        fi
        return 0
    fi
    
    if has_jq; then
        local failures=$(jq -r ".[] | select(.sessionId == \"$session_id\")" "$FAILURE_LOG_FILE" 2>/dev/null || echo "")
        if [[ -n "$failures" ]]; then
            if [[ "$output_json" == "true" ]]; then
                echo "$failures"
            else
                echo "$failures" | while IFS= read -r failure; do
                    print_failure "$failure" "$use_color"
                done
            fi
        else
            if [[ "$use_color" == "true" ]]; then
                echo -e "${GREEN}No git commit failures found for session: $session_id${NC}"
            else
                echo "No git commit failures found for session: $session_id"
            fi
        fi
    else
        grep "\"$session_id\"" "$FAILURE_LOG_FILE" 2>/dev/null || {
            if [[ "$use_color" == "true" ]]; then
                echo -e "${GREEN}No git commit failures found for session: $session_id${NC}"
            else
                echo "No git commit failures found for session: $session_id"
            fi
        }
    fi
}

# Clear all failure logs
clear_failures() {
    if [[ -f "$FAILURE_LOG_FILE" ]]; then
        echo "[]" > "$FAILURE_LOG_FILE"
        echo "Git commit failure logs cleared."
    else
        echo "No failure logs to clear."
    fi
}

# Tail the regular log file
tail_logs() {
    if [[ -f "$REGULAR_LOG_FILE" ]]; then
        tail -f "$REGULAR_LOG_FILE"
    else
        echo "Log file not found: $REGULAR_LOG_FILE"
        exit 1
    fi
}

# Main command processing
main() {
    local command="${1:-list}"
    local output_json="false"
    local use_color="true"
    
    # Process global options
    while [[ $# -gt 0 ]]; do
        case $1 in
            --json)
                output_json="true"
                shift
                ;;
            --no-color)
                use_color="false"
                shift
                ;;
            --help|-h)
                usage
                exit 0
                ;;
            -*)
                echo "Unknown option: $1" >&2
                usage >&2
                exit 1
                ;;
            *)
                break
                ;;
        esac
    done
    
    case "$command" in
        list)
            list_failures "$output_json" "$use_color"
            ;;
        count)
            count_failures
            ;;
        recent)
            local count="${2:-5}"
            show_recent "$count" "$output_json" "$use_color"
            ;;
        task)
            if [[ $# -lt 2 ]]; then
                echo "Error: Task ID is required" >&2
                usage >&2
                exit 1
            fi
            filter_by_task "$2" "$output_json" "$use_color"
            ;;
        session)
            if [[ $# -lt 2 ]]; then
                echo "Error: Session ID is required" >&2
                usage >&2
                exit 1
            fi
            filter_by_session "$2" "$output_json" "$use_color"
            ;;
        clear)
            clear_failures
            ;;
        tail)
            tail_logs
            ;;
        *)
            echo "Unknown command: $command" >&2
            usage >&2
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"