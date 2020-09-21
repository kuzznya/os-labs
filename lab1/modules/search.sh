#!/bin/bash

[[ -z "$IMPORT_SEARCH" ]] && IMPORT_SEARCH=true || return 0

require core.sh
require errors.sh

# search <dir> <regex>
# Search for strings matching regex in all files of directory recursively
search() {
    ! dir_exists "$1" && invalid_args search "$1 is not a directory"
    grep -r "$2" "$1" 2> /dev/null | cut -d: -f2 | uniq
}
