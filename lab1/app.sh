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
	[[ $# -ne 3 ]] && invalid_args reverse "invalid args count"
	require modules/reverse.sh
	reverse "$2" "$3" ;;
    strlen )
	[[ $# -ne 2 ]] && invalid_args strlen "invalid args count"
	require modules/strlen.sh
	strlen "$1" ;;
    log )
	[[ $# -ne 1 ]] && invalid_args log "no args expected"
	require modules/log.sh
	log /var/log/anaconda/X.log ;;
    help )
	print_man ;;
    * )
	invalid_args app "unknown command $1" ;;
esac
