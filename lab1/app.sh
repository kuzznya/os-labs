#!/bin/bash

if [[ -f "utils.sh" ]] ; then
    source utils.sh
else
    echo "Fatal error: missing script utils.sh" > /dev/stderr && exit -10
fi

require errors.sh

[[ $# -lt 1 ]] && not_enough_args "app"

case $1 in
    calc )
	echo "calc not implemented yet" ;;
    search )
	echo "search not implemented yet" ;;
    reverse )
	echo "reverse not implemented" ;;
    strlen )
	echo "strlen not implemented" ;;
esac
