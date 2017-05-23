#!/bin/bash
#prepare filename
filename=mysql_backup$(date +-%d-%m-%y).bak
#run backup command
mysqldump -u root bugtracker > $filename
tar -czf /mnt/nfs/backup/mysql/$filename.tar.gz $filename
rm -f $filename
