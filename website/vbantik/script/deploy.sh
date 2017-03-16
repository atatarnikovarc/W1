#!/usr/bin/env bash
home=/home/atatarnikov/work/git/github/W1/website/vbantik
srv=134.0.113.42
port=24244
srv_path=/var/www/vbantik.ru/public_html
user=atatarnikov

cd $home/dj1 && python manage.py collectstatic --noinput
scp -P $port -r $home/dj1 $user@$srv:$srv_path
scp -P $port $home/dj1/dj1/prod_settings.py $user@$srv:$srv_path/dj1/dj1/settings.py
ssh -p $port $user@$srv 'sudo apachectl restart'