__author__ = 'atatarnikov'

import sys
import configparser

try:
    config_arg = str(sys.argv[1])
    sample_arg = str(sys.argv[2])
except IndexError:
    print 'There\' must be 2 input parameters - config(1-st) and sample(2-nd) file names, ' \
          'please, specify and run again. Exiting...'
    sys.exit(-1)

sample_f = open(sample_arg, 'r')
sample = sample_f.read()

# no errors processing for the momment
config = configparser.ConfigParser()
config.read(config_arg)

for e in config['main']:
    sample = sample.replace(e, config['main'][e])

for e in reversed(sample.split()):
    print e

sample_f.close()