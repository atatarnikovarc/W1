#!/bin/sh

BEAT[0]=http://$1.west.p.raasnet.com/partners/hb
BEAT[1]=http://$1.east.p.raasnet.com/partners/hb
BEAT[2]=http://$1.west.dts.raasnet.com/dts/hb
BEAT[3]=http://$1.east.dts.raasnet.com/dts/hb
BEAT[4]=http://$1.west.a.raasnet.com/hb
BEAT[5]=http://$1.west.rtb.raasnet.com/api/hb

env3_beat[0]=http://$1.west.p.raasnet.com/partners/hb
env3_beat[1]=http://$1.west.p.raasnet.com/partners/hb
env3_beat[2]=http://$1.west.p.raasnet.com/partners/hb
env3_beat[3]=http://$1.west.p.raasnet.com/partners/hb
env3_beat[4]=http://$1.west.p.raasnet.com/partners/hb

while :
do
 for i in {0..4}
 do
  if [ "$1" = env3 ]
  then
   result=$(curl -s ${env3_beat[i]})
  else
   result=$(curl -s ${BEAT[i]})
  fi
  sleep 15
#  echo $result
  if [ "$result" != OK ];
  then
#  echo $result
   break
  fi
 done
 if [ "$result" = OK ];
 then
  break
 fi
done

