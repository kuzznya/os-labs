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
    [[ $2 -eq 0 ]] && illegal_operation "division by zero"
    let "result = $1 / $2"
    echo $result
}

# calc <action> <int1> <int2>
# action: sum/sub/mul/div
# If action is unknown, then invalid_args throws
calc() {    
    local action="$1"
    local param1="$2"
    local param2="$3"
    
    ! is_int $param1 && invalid_args calc "$param1 is not an int"
    ! is_int $param2 && invalid_args calc "$param2 is not an int"

    case $action in
	sum )
	    sum $param1 $param2 ;;
	sub )
	    sub $param1 $param2 ;;
	mul )
	    mul $param1 $param2 ;;
	div )
	    div $param1 $param2 ;;
	* )
	    invalid_args calc "cannot calculate action $action" ;;
    esac
}
