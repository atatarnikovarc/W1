#!/usr/bin/env bash

eta_dir="./data/etalon"
curr_dir="./data/curr"

# it is pity - during that task implementation - I deleted up all my user's files data
# - bless me for auto backup, but I have to move back for one week (((
# so - be aware of command like 'rm -rf' - always add to your '.bashrc' the
# following: "alias rm='rm -I'". Another one lesson - after 'windows' batch
# scripting - note - in bash - you just put 'varname=value' - no any 'set' instructions
# !!! otherwise you get rm -rf $curr_dir/* - ffffffff uuu hhh unity logout!

# refresh target dir
rm -r $curr_dir/
cp -rf $eta_dir/ $curr_dir/

do_1() {
  if grep -q @Author "$1"; then
    sed -i -e 's/@Author.*/@Author Candidate Surname/' $1;
  else
    echo "$1";
  fi
}

export -f do_1
find $curr_dir -type f \( -name "*.cpp" -o -name "*.java" \)\
    -exec bash -c 'do_1 '{} \;

printf "\n"
echo "I'm done -]"
