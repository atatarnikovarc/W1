#!/bin/bash
../../bin/remoteDeployer -c qacluster4 kill
watch ../../bin/remoteDeployer -c qacluster4 ps
