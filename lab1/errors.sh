#!/bin/bash

print_err() {
    echo "$*" >> /dev/stderr
}

error_exit() {
    local script_name="$1"
    local message="$2"
    local code="${3:-1}"

    [[ -n "$message" ]] && print_err "Error in script $script_name: $message" || \
	    print_err "Error in script $script_name"

    exit "${code}"
}

missing_script() {
    error_exit "$1" "Missing script $2" -9
}

not_enough_args() {
    print_man
    error_exit "$1" "Not enough args" -10
}
