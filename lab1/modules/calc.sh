#!/bin/bash

[[ -z "$IMPORT_CALC" ]] && IMPORT_CALC=true || return 0

require core.sh
require errors.sh

sum() {
    let "result = $1 + $2"
    echo $result
}

sub() {
    let "result = $1 - $2"
    echo $result
}

mul() {
    let "result = $1 * $2"
    echo $result
}

div() {
    let "result = $1 / $2"
    echo $result
}

# is_int <param>
# Returns 0 if param is int, else -1
is_int() {
    [[ "$1" =~ ^([1-9][0-9]*|0)$ ]] && return 0 || return -1
}

# calc <action> <int1> <int2>
# action: sum/sub/mul/div
# If action is unknown, then invalid_args throws
calc() {
    local action="$1"
    local param1="$2"
    local param2="$3"
    
    ! is_int $param1 && invalid_args "calc" "$param1 is not an int"
    ! is_int $param2 && invalid_args "calc" "$param2 is not an int"

    case $action in
	sum )
	    echo $(sum $param1 $param2) ;;
	sub )
	    echo $(sub $param1 $param2) ;;
	mul )
	    echo $(mul $param1 $param2) ;;
	div )
	    echo $(div $param1 $param2) ;;
	* )
	    invalid_args "calc" "cannot calculate action $ACTION" ;;
    esac
}
