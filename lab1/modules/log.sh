#!/bin/bash

[[ -z "$IMPORT_LOG" ]] && IMPORT_LOG=true || return 0

require core.sh

log_warns() {
    grep "(WW)" "$1" | foreach_str replace_all '\(WW\)' 'Warning: '
}

log_infos() {
    grep "(II)" "$1" | foreach_str replace_all '\(II\)' 'Information: '
}

log() {
    log_warns "$1" && log_infos "$1"
}
