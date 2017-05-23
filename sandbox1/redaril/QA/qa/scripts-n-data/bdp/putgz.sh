#!/bin/bash
gzip ip.txt -c > NEW_IPs_20110101.gz
cp NEW_IPs_20110101.gz ../../dmp_data/ip_base
