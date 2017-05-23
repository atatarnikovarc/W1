how to use:
(it is assumed, that schema name and password are equal)
-go to 'execScript' folder
-to execute particular script for some db schema use ./runDbScript.sh schema_name path_to_script
-to execute particular script for dmp X schema use ./envX_dmb_db.sh path_to_script
-to execute particular script for meta X schema use ./envX_meta_db.sh path_to_script
-to execute particular script for both 1,2 dmp schema use ./env12_dmp_db.sh path_to_script
-to execute particular script for both 1,2 meta schema use ./env12_meta_db.sh path_to_script

each execution leads to command 'less executionlog.sql' i.e. one need to press 'q' to continue
