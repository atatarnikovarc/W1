#!/bin/bash
../../bin/remoteDeployer -c qacluster4 rmlog
../../bin/remoteDeployer -c qacluster4 clean
../../bin/remoteDeployer -c qacluster4 install
../../bin/remoteDeployer -c qacluster4 start
watch ../../bin/remoteDeployer -c qacluster4 summary
