#!/bin/bash

[[ -z "$IMPORT_CALC" ]] && IMPORT_CALC=true || return 0

require core.sh

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

calc() {
    local action="$1"
    local param1="$2"
    local param2="$3"

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
