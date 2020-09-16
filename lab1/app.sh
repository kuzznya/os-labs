#!/bin/bash

if [[ -f "core.sh" ]] ; then
    source core.sh
else
    echo "Fatal error: missing script core.sh" > /dev/stderr && exit -10
fi

require errors.sh

[[ $# -lt 1 ]] && invalid_args app "not enough args"

case "$1" in
    calc )
	[[ $# -ne 4 ]] && invalid_args calc "invalid args count"
	require modules/calc.sh
	calc "$2" "$3" "$4" ;;
    search )
	[[ $# -ne 3 ]] && invalid_args search "invalid args count"
	require modules/search.sh
	search "$2" "$3" ;;
    reverse )
	echo "reverse not implemented" ;;
    strlen )
	echo "strlen not implemented" ;;
    "help" )
	print_man ;;
    * )
	invalid_args app "unknown command $1" ;;
esac
