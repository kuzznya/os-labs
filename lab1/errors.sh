#!/bin/bash

# print_err <message>
# Print message to stderr
print_err() {
    echo "$*" >> /dev/stderr
}

# error_exit <message> [<code>]
# Print message to srderr & then exit with given code
# If no code present, then -1 is returned
error_exit() {
    local message="$1"
    local code="${2:-1}"

    [[ -n "$message" ]] && print_err "Error: $message" || \
	    print_err "Fatal error"

    exit "${code}"
}

# missing_script <missing script name>
# Reports that script is missing
# Exit code -10
missing_script() {
    error_exit "Missing script $1" -10
}

# not_enough_args <command>
# Prints manual & reports that command is invalid
# Exit code -9
not_enough_args() {
    [[ -f "utils.sh" ]] && source utils.sh && print_man || missing_script "errors.sh" "utils.sh"
    error_exit "Not enough args for command $1" -9
}
