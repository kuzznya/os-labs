#!/bin/bash

[[ -z "$IMPORT_REVERSE" ]] && IMPORT_REVERSE=true || return 0

# reverse <input file> <output file>
# Reverse input file text and write to output file 
reverse() {
    tac "$1" | rev > "$2"
}
