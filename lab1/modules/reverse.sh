#!/bin/bash

[[ -z "$IMPORT_REVERSE" ]] && IMPORT_REVERSE=true || return 0

require core.sh
require errors.sh

# reverse <input file> <output file>
# Reverse input file text and write to output file 
reverse() {
    ! file_exists "$1" && illegal_operation "file $1 does not exist"
    ! file_readable "$1" && illegal_operation "file $1 is not readable"
    if file_exists "$2" && ! file_writable "$2" || dir_exists "$2" || ! touch "$2" 2> /dev/null ; then
	illegal_operation "file $2 is not writable"
    fi
    
    [[ "$1" = "$2" ]] && tac "$1" | rev > TMP && mv TMP "$2" || tac "$1" | rev > "$2"
}
