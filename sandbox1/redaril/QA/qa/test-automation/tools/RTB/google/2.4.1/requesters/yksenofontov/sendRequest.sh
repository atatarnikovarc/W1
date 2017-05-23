#!/bin/bash
rm -rf *.log *.html
perl requester.py --url=http://10.50.150.152:8080/api/google --max_qps=1 --requests=1
#perl requester.py --url=http://192.168.0.32:9400/api/google --max_qps=1 --requests=1
./sendSnippetToMe.sh

