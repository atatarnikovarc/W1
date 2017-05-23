#!/bin/bash
cat $2 > wrap.sql
echo -e "\n" >> wrap.sql
echo "commit;" >> wrap.sql
echo "quit;" >> wrap.sql

sqlplus $1/$1@qadb.qa.phsg.coreaudience.com:1521/qacluster @wrap.sql > dbRunScript.log
less dbRunScript.log
