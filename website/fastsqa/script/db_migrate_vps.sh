#!/usr/bin/env bash

srv=151.248.126.57
port=24244
user=atatarnikov
script_path=/home/atatarnikov/work/git/github/W1/website/fastsqa/script/db_migrate.sh

ssh -p $port $user@$srv 'bash -s' < $script_path