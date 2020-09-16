#!/bin/bash

[[ -z "$IMPORT_LOG" ]] && IMPORT_LOG=true || return 0

require core.sh

# log_warns <log>
# Get all warning lines from log & replace (WW) with Warning:
log_warns() {
    grep "(WW)" "$1" | foreach_str replace_all '\(WW\)' 'Warning: '
}

# log_infos <log>
# Get all information lines & replace (II) with Information:
log_infos() {
    grep "(II)" "$1" | foreach_str replace_all '\(II\)' 'Information: '
}

# Add yellow color to warning line
print_warn() {
    echo -e $(replace_all 'Warning: ' '\e[1;33mWarning: \e[1;0m' "$@")
}

# Add cyan color to information line
print_info() {
    echo -e $(replace_all 'Information: ' '\e[1;36mInformation: \e[1;0m' "$@")
}

# log <log>
# Print all warning & info lines with colors
log() {
    log_warns "$1" | foreach_str print_warn
    log_infos "$1" | foreach_str print_info
}
