#!/bin/bash

[[ -z "$IMPORT_REVERSE" ]] && IMPORT_REVERSE=true || return 0

require core.sh
require errors.sh

# reverse <input file> <output file>
# Reverse input file text and write to output file 
reverse() {
    ! file_exists "$1" && illegal_operation "file $1 does not exist"
    if file_exists && ! file_writable "$2" ; then
	illegal_operation "file $2 is not writable"
    fi
    
    tac "$1" | rev > "$2"
}
