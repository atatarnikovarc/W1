#!/usr/bin/env bash

if [[ `hostname` = 'vps2.fastsqa.com' ]];
then
 p_path=/var/www/fastsqa.com/public_html/dj1/
else
 p_path=/home/atatarnikov/work/git/github/W1/website/fastsqa/dj1/
fi

cd $p_path

python manage.py makemigrations
python manage.py migrate