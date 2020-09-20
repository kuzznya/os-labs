#!/bin/bash

if [[ -f "core.sh" ]] ; then
    source core.sh
else
    echo "Fatal error: missing script core.sh" > /dev/stderr && exit -10
fi

require errors.sh

module_missing() {
    echo "Warning: $@ not found; some actions are unavailable"
}

! file_exists modules/calc.sh &&  module_missing modules/calc.sh
! file_exists modules/search.sh && module_missing modules/search.sh
! file_exists modules/reverse.sh && module_missing modules/reverse.sh
! file_exists modules/strlen.sh && module_missing modules/strlen.sh
! file_exists modules/log.sh && module_missing modules/log.sh
! file_exists interactive.sh && module_missing interactive.sh
! file_exists man.txt && module_missing man.txt

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
	strlen "$2" ;;
    log )
	[[ $# -ne 1 ]] && invalid_args log "no args expected"
	require modules/log.sh
	log /var/log/anaconda/X.log ;;
    interactive )
	[[ $# -ne 1 ]] && invalid_args interactive "no args expected"
	call interactive.sh start ;;
    help )
	print_man ;;
    exit )
	[[ -n $2 ]] && ! is_int "$2" && invalid_args exit "param should be int"
	exit ${2:0} ;;
    * )
	invalid_args app "unknown command $1" ;;
esac
