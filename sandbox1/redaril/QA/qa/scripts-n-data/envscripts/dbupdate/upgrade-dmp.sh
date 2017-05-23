cd /root/QA/DbUpdate/dmp
$branch = snv info | grep URL | 
svn up
sqlplus $1"_meta"/$1"_meta"@10.50.150.90/qacluster "@SR_"$branch"_Upgrade.sql"