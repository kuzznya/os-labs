#!/bin/bash

[[ -z "$IMPORT_SEARCH" ]] && IMPORT_SEARCH=true || return 0

require errors.sh

# search_in_file <file> <regex>
search_in_file() {
    file="$1"
    regex="$2"

    grep "$regex" "$file"
}

# search <dir> <regex>
# Search for strings matching regex in all files of directory recursively
search() {
    ! [[ -d "$1" ]] && invalid_args search "$1 is not a directory"
    
    for file in $(find "$1")
    do
	! [[ -d $file ]] && search_in_file "$file" "$2"
    done
}
