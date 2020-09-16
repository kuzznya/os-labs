#!/bin/bash

[[ -z "$IMPORT_STRLEN" ]] && IMPORT_STRLEN=true || return 0

strlen() {
    local str="$@"
    echo "${#str}"
}
