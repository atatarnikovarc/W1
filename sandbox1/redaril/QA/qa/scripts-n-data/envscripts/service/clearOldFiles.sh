#!/bin/bash
find /mnt/nfs/backup/mysql/ -mtime +0 | xargs rm -rf {}\;
