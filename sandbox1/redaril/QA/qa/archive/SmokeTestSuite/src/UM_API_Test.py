import unittest, time, re
from meta_test import *


#############################################
LOGGING_FILE='um_test.log'
#prepare logging
try:
    LOGING_FILE = os.remove(LOGGING_FILE)
except IOError, err:
#    print err.strerror
    print "Create Empty LOGGING_FILE (i.e um_test.log)" 

logging.basicConfig(level = logging.INFO,format = "[%(asctime)s] %(levelname)s: %(message)s",
                    datefmt = "%d %b %Y %H:%M:%S",
                    filename = LOGGING_FILE,
                    filemode = "a")   

logging.info("============================================================")
logging.info("Starting UM Testing Procedure")    
    
    

class TestCase(meta_test):
    '''Initial Parametrs for Testing. Change their if you want to simulate another user'''    
    ASE_host="qaas1.nebuad.com"
    UM_host="10.50.150.111"
    cookie_d="0000007"
    cookie_c="QAUTA500"
    cookie_u="30680772461571528"
    UID="11546792417133555980"
    HIP="115467924171335559804fa881f4dcf6e99769c91d42f9967eb3"

    def setUp(self):
        """setUp"""
        logging.info("============================================================")
        print "cookie d: "+self.cookie_d
        print "cookie c: "+self.cookie_c
        print "cookie u: "+self.cookie_u
        print "UID: "+self.UID
        print "HIP: "+self.HIP

    def tearDown(self):
        """tearDown"""
        logging.info("============================================================")
          
        
    def test_coarseprofile(self):
        print "Start Coarse profile Testing"
        logging.info('Start Coarse profile Testing')
        '''Return the interest of current coarse profile user '''
        self.UM_API("check","coarse")
        '''Reset curent coarse profile interest '''
        self.UM_API("r","coarse")
        '''Hit the 1002 interest for current user '''
        self.UM_API("i","coarse")
        '''Reset curent coarse profile interest '''
        self.UM_API("r","coarse")
        '''Current coarse profile user has no any interest'''
        logging.info('Coarse profile Test PASSED')
        
    def test_fineprofile(self):
        
        print "Start Fine profile Testing"
        logging.info('Start Fine profile Testing')
        '''Return the interest of current coarse profile user '''
        self.UM_API("check","fine")
        '''Reset curent coarse profile interest '''
        self.UM_API("r","fine")
        '''Hit the 1002 interest for current user '''
        self.UM_API("i","fine")
        '''Reset curent coarse profile interest '''
        self.UM_API("r","fine")
        logging.info('Fine profile Test PASSED')        
        
if __name__ == "__main__":
    unittest.main()               
        
        
       
           
        
            