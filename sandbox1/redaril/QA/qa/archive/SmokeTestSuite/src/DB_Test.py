import unittest, time, re
from meta_test import *
from datetime import  datetime
from selenium import *

#############################################
LOGGING_FILE='db_test.log'
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
logging.info("Starting DB Logging Testing Procedure")    
 
class TestCase(meta_test):
    db_host='certer_qauta500'
    db_name='10.50.150.105'
    db_port=3306
    db_user='nebuad'
    db_password='n3buad'
    log_url="nebuad.com/certertest.html"
    delay=180
    time_lead=180
    
    def setUp(self):
        """setUp"""
        self.sel=selenium("localhost", 4444, "*chrome", "http://"+self.log_url)
        self.sel.start()    
        
    def tearDown(self):
        """tearDown"""
      
        self.sel.stop()
        logging.info("============================================================")
        
    def test_db_logging(self):  
        print "Open "+self.log_url+" in browserbot"
        logging.info("Open "+self.log_url+" in browserbot")
        self.sel.open("http://"+self.log_url)
        self.current_time=time.time()
        self.current_time=self.current_time-self.time_lead
        self.current_time=datetime.fromtimestamp(self.current_time)
        print "Time of page visit "+ str(self.current_time)
        logging.info("Time of page visit "+ str(self.current_time))
        print "Wait for "+str(self.delay)+" seconds until record is added to DB."
        logging.info("Wait for "+str(self.delay)+" until record is added to DB.")
        time.sleep(self.delay)
        self.db_last_log_check()
        self.db_visit_check()


if __name__ == "__main__":
    unittest.main()             
    
        