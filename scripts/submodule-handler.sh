#!/bin/bash

# Keruta Submodule Handler Script
# Handles git operations for submodules when tasks are completed

set -euo pipefail

# Configuration
PROJECT_ROOT="${KERUTA_PROJECT_ROOT:-/home/coder/keruta/keruta}"
LOG_FILE="${PROJECT_ROOT}/logs/submodule-operations.log"
DEBUG_MODE="${DEBUG:-false}"

# Ensure log directory exists
mkdir -p "$(dirname "$LOG_FILE")"

# Logging functions
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

debug() {
    if [[ "$DEBUG_MODE" == "true" ]]; then
        echo "[DEBUG $(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
    fi
}

error() {
    echo "[ERROR $(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE" >&2
}

# Function to check if directory is a git repository
is_git_repo() {
    local dir="$1"
    [[ -d "$dir/.git" ]] || git -C "$dir" rev-parse --git-dir >/dev/null 2>&1
}

# Function to check if there are uncommitted changes
has_uncommitted_changes() {
    local dir="$1"
    [[ -n "$(git -C "$dir" status --porcelain)" ]]
}

# Function to get current branch name
get_current_branch() {
    local dir="$1"
    git -C "$dir" rev-parse --abbrev-ref HEAD
}

# Function to commit and push changes in a submodule
process_submodule() {
    local submodule_path="$1"
    local commit_message="$2"
    local full_path="$PROJECT_ROOT/$submodule_path"
    
    log "Processing submodule: $submodule_path"
    
    # Check if submodule directory exists
    if [[ ! -d "$full_path" ]]; then
        error "Submodule directory not found: $full_path"
        return 1
    fi
    
    # Check if it's a git repository
    if ! is_git_repo "$full_path"; then
        error "Directory is not a git repository: $full_path"
        return 1
    fi
    
    # Change to submodule directory
    cd "$full_path"
    
    # Check if there are changes to commit
    if ! has_uncommitted_changes "$full_path"; then
        debug "No changes to commit in $submodule_path"
        return 0
    fi
    
    # Get current branch
    local current_branch
    current_branch=$(get_current_branch "$full_path")
    debug "Current branch in $submodule_path: $current_branch"
    
    # Add all changes
    debug "Adding changes in $submodule_path"
    if ! git add .; then
        error "Failed to add changes in $submodule_path"
        return 1
    fi
    
    # Commit changes
    debug "Committing changes in $submodule_path"
    if ! git commit -m "$commit_message"; then
        error "Failed to commit changes in $submodule_path"
        return 1
    fi
    
    log "Successfully committed changes in $submodule_path"
    
    # Push changes if auto-push is enabled
    if [[ "${KERUTA_SUBMODULE_AUTO_PUSH:-true}" == "true" ]]; then
        debug "Pushing changes in $submodule_path"
        if git push origin "$current_branch"; then
            log "Successfully pushed changes in $submodule_path"
        else
            error "Failed to push changes in $submodule_path"
            return 1
        fi
    else
        log "Auto-push disabled, skipping push for $submodule_path"
    fi
    
    return 0
}

# Function to update main repository submodule references
update_main_repository() {
    local updated_submodules=("$@")
    
    log "Updating main repository submodule references"
    cd "$PROJECT_ROOT"
    
    # Add updated submodule references
    local has_changes=false
    for submodule in "${updated_submodules[@]}"; do
        if git add "$submodule"; then
            debug "Added submodule reference: $submodule"
            has_changes=true
        fi
    done
    
    # Check if there are changes to commit
    if [[ "$has_changes" == "true" ]] && has_uncommitted_changes "$PROJECT_ROOT"; then
        local commit_message="Update submodule references: ${updated_submodules[*]}"
        
        if git commit -m "$commit_message"; then
            log "Committed main repository submodule reference updates"
            
            # Push main repository changes if auto-push is enabled
            if [[ "${KERUTA_SUBMODULE_AUTO_PUSH:-true}" == "true" ]]; then
                local current_branch
                current_branch=$(get_current_branch "$PROJECT_ROOT")
                
                if git push origin "$current_branch"; then
                    log "Pushed main repository changes"
                else
                    error "Failed to push main repository changes"
                    return 1
                fi
            else
                log "Auto-push disabled, skipping main repository push"
            fi
        else
            error "Failed to commit main repository submodule reference updates"
            return 1
        fi
    else
        debug "No submodule reference changes to commit in main repository"
    fi
    
    return 0
}

# Main function
main() {
    if [[ $# -lt 2 ]]; then
        echo "Usage: $0 <commit_message> <submodule1> [submodule2] ..."
        echo "Example: $0 'Task completed: API updates' keruta-api keruta-doc"
        exit 1
    fi
    
    local commit_message="$1"
    shift
    local submodules=("$@")
    local updated_submodules=()
    local success_count=0
    
    log "Starting submodule processing"
    log "Commit message: $commit_message"
    log "Submodules to process: ${submodules[*]}"
    
    # Process each submodule
    for submodule in "${submodules[@]}"; do
        if process_submodule "$submodule" "$commit_message"; then
            updated_submodules+=("$submodule")
            ((success_count++))
        else
            error "Failed to process submodule: $submodule"
        fi
    done
    
    log "Successfully processed $success_count out of ${#submodules[@]} submodules"
    
    # Update main repository if any submodules were successfully processed
    if [[ ${#updated_submodules[@]} -gt 0 ]]; then
        if update_main_repository "${updated_submodules[@]}"; then
            log "Main repository updated successfully"
        else
            error "Failed to update main repository"
            exit 1
        fi
    fi
    
    log "Submodule processing completed"
    
    # Exit with error if not all submodules were processed successfully
    if [[ $success_count -ne ${#submodules[@]} ]]; then
        exit 1
    fi
}

# Run main function with all arguments
main "$@"