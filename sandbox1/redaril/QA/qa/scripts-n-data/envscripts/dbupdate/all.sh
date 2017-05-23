copy.sh > update.log
purge.sh $1 >> update.log 
import.sh $1 >> update.log
upgrade-csc.sh $1 >> update.log
upgrade-dmp.sh $1 >> update.log