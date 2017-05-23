#!/bin/sh

bin/remoteDeployer -c $1 stop
sleep 15
bin/remoteDeployer -c $1 kill
sleep 15
bin/remoteDeployer -c $1 nuke
bin/remoteDeployer -c $1 rmlog
bin/remoteDeployer -c $1 install
bin/remoteDeployer -c $1 start
