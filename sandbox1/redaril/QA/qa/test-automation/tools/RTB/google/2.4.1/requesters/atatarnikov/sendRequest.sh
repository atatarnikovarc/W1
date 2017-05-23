#!/bin/bash
rm -rf *.log *.html
python requester.py --url=http://qa-03.qa.coreaudience.com:8080/api/google --max_qps=1 --requests=1
#perl requester.py --url=http://10.118.56.22:9400/api/google --max_qps=1 --requests=1
#perl requester.py --url=http://192.168.0.32:9400/api/google --max_qps=1 --requests=1
./sendSnippetToWS.sh

