#!/bin/bash

if test -f "errors.sh"; then
    source errors.sh
else
    echo "Fatal error: missing script errors.sh" > /dev/stderr && exit -9
fi

[[ $# -lt 2 ]] && not_enough_args "app"

case $1 in
    calc )
	echo "calc not implemented yet" ;;
    search )
	echo "search not implemented yet" ;;
    reverse )
	echo "reverse not implemented" ;;
    strlen )
	echo "strlen not implemented" ;;
