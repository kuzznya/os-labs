#!/bin/bash

require core.sh
require errors.sh

import modules/calc.sh && calc_imported=true
import modules/search.sh && search_imported=true
import modules/reverse.sh && reverse_imported=true
import modules/strlen.sh && strlen_imported=true
import modules/log.sh && log_imported=true

print_menu() {
    [[ -n $calc_imported ]] && echo "c - calc"
    [[ -n $search_imported ]] && echo "s - search"
    [[ -n $reverse_imported ]] && echo "r - reverse"
    [[ -n $strlen_imported ]] && echo "n - strlen"
    [[ -n $log_imported ]] && echo "l - log"
    echo "e - exit"
    echo "m - menu"
}

input_valid() {
    validate="$2"
    [[ -z "$3" ]] && error_msg="Invalid input" || error_msg="$3"
    
    while true ; do
	printf "> "
	read line
	if $validate $line ; then
	    eval "$1=$line"
	    break
	fi
	echo "$error_msg"
    done
}

input_int() {
    input_valid "$1" is_int "Input is not an int"
}

input_file() {
    input_valid "$1" file_exists "Input is not a valid file"
}

input_dir() {
    input_valid "$1" dir_exists "Input is not a valid directory"
}

interactive_calc() {
    echo "Actions:"
    echo "+ sum"
    echo "- sub"
    echo "* mul"
    echo "/ div"

    while true
    do
	printf "> "
	read action
	case $action in
	    +|sum )
		action=sum
		break ;;
	    -|sub )
		action=sub
		break ;;
	    \*|mul )
		action=mul
		break ;;
	    /|div )
		action=div
		break ;;
	    * )
		echo "Invalid action, please try again" ;;
	esac
    done

    input_int int1
    input_int int2

    eval "calc $action $int1 $int2"
}

interactive_search() {
    echo "Directory to search into:"
    input_dir dir
    echo "Regex:"
    read regex
    search "$dir" "$regex"
}

interactive_reverse() {
    echo "Input file:"
    input_file input
    echo "Output file:"
    printf "> "
    read output
    touch output && reverse "$input" "$output" || illegal_operation "$output is not writable"
}

interactive_strlen() {
    printf "> "
    read str
    strlen $str
}

handle_input() {
    while true
    do
	printf "> "
	read line
	case $line in
	    c|calc )
		[[ -z $calc_imported ]] && echo "Command unavailable" && continue
		interactive_calc
		break ;;
	    s|search )
		[[ -z $search_imported ]] && echo "Command unavailable" && continue
		interactive_search
		break ;;
	    r|reverse )
		[[ -z $reverse_imported ]] && echo "Command unavailable" && continue
		interactive_reverse
		break ;;
	    n|strlen )
		[[ -z $strlen_imported ]] && echo "Command unavailable" && continue
		interactive_strlen
		break ;;
	    l|log )
		[[ -z $log_imported ]] && echo "Command unavailable" && continue
		log /var/log/anaconda/X.log
		break ;;
	    m|menu )
		print_menu ;;
	    e|exit )
		echo "Return code (default 0):"
		read code
		is_int $code && exit $code || exit 0 ;;
	    * )
		echo "Invalid input, enter 'menu' to see menu again"
	esac
    done
}

start() {
    print_menu
    while true ; do
	handle_input
    done
}
