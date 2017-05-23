#!/bin/bash

pushd /usr/local/bin

[ $# == 0 ] && { echo "Usage: $(basename $0) <SCHEMA_NAME>"; exit 1; }

schema=$1
sqlplus $schema/$schema@10.50.150.90/qacluster @bunupdater.sql

popd