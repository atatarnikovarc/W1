import unittest, time, re
from meta_test import *
from datetime import datetime
from selenium import *

#############################################
LOGGING_FILE='qu_test.log'
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
    delay=300
    time_lead=180
    gs_id="1038238"
    ss_id="1038239"
    dw_id="1038240"
    eu_id="1038241"
    uc_id="1038242"
    
    def setUp(self):
        """setUp"""
        self.sel=selenium("localhost", 4444, "*chrome", "http://google.com")
        self.sel.start()    
        
    def tearDown(self):
        """tearDown"""
      
        self.sel.stop()

        logging.info("============================================================")
        

    
    def test_google_gs(self):
       print "Start Google General Search Test"
       logging.info("Start Google General Search Test")
       self.sel.open("http://google.com")
       self.sel.type("//input[@name='q']","Smoke_Suite")
       self.sel.click("//input[@name='btnG']")
       self.sel.type("//input[@name='q']","Smoke_Suite")
       self.sel.click("//input[@name='btnG']")
       self.sel.type("//input[@name='q']","Smoke_Suite")
       self.sel.click("//input[@name='btnG']")
       self.current_time=time.time()
       self.current_time=self.current_time-self.time_lead
       self.current_time=datetime.fromtimestamp(self.current_time)
       print "Time of qualifier firing "+ str(self.current_time)
       logging.info("Time of qualifier firing "+ str(self.current_time))
       print "Qualifier Id="+self.gs_id
       logging.info("Qualifier Id="+self.gs_id)
       time.sleep(self.delay)
       self.qu_check(self.gs_id)

    def test_msn_gs(self):
       print "Start MSN General Search Test"
       logging.info("Start MSN General Search Test")
       self.sel.open("http://msn.com")
       self.sel.type("//input[@id='f1']","Smoke_Suite")
       self.sel.click("//input[@value='Search Web']")
       self.sel.type("//input[@id='f1']","Smoke_Suite")
       self.sel.click("//input[@value='Search Web']")
       self.sel.type("//input[@id='f1']","Smoke_Suite")
       self.sel.click("//input[@value='Search Web']")
       self.current_time=time.time()
       self.current_time=self.current_time-self.time_lead
       self.current_time=datetime.fromtimestamp(self.current_time)
       print "Time of qualifier firing "+ str(self.current_time)  
       logging.info("Time of qualifier firing "+ str(self.current_time))
       print "Qualifier Id="+self.gs_id
       logging.info("Qualifier Id="+self.gs_id)
       time.sleep(self.delay)
       self.qu_check(self.gs_id)
        
    def test_tm_ss(self):
       print "Start TicketMaster Specific Search Test"
       logging.info("Start TicketMaster Specific Search Test")
       self.sel.open("http://www.ticketmaster.com/search")
       self.sel.type("//input[@id='keyword']","Smoke_Suite")
       self.sel.click("//input[@value='Search']")
       self.sel.type("//input[@id='keyword']","Smoke_Suite")
       self.sel.click("//input[@value='Search']")
       self.sel.type("//input[@id='keyword']","Smoke_Suite")
       self.sel.click("//input[@value='Search']")
       self.current_time=time.time()
       self.current_time=self.current_time-self.time_lead
       self.current_time=datetime.fromtimestamp(self.current_time)
       print "Time of qualifier firing "+ str(self.current_time)
       logging.info("Time of qualifier firing "+ str(self.current_time))
       print "Qualifier Id="+self.ss_id
       logging.info("Qualifier Id="+self.ss_id)
       time.sleep(self.delay)
       self.qu_check(self.ss_id)
    
    def test_dw(self):
       print "Start Domain Wide Testing"
       logging.info("Start Domain Wide Testing")
       self.sel.open("http://www.nebuad.com/certertest.html")
       self.sel.open("http://www.nebuad.com/certertest.html")
       self.sel.open("http://www.nebuad.com/certertest.html")
       self.current_time=time.time()
       self.current_time=self.current_time-self.time_lead
       self.current_time=datetime.fromtimestamp(self.current_time)
       print "Time of qualifier firing "+ str(self.current_time)
       logging.info("Time of qualifier firing "+ str(self.current_time))
       print "Qualifier Id="+self.dw_id
       logging.info("Qualifier Id="+self.dw_id)
       logging.info("Qualifier Id="+self.dw_id)
       time.sleep(self.delay)
       self.qu_check(self.dw_id)
       
    def test_eu(self):
        print "Start Exact Url Test"    
        logging.info("Start Exact Url Test")
        self.sel.open("http://nebuad.com/certertest.html")
        self.sel.open("http://nebuad.com/certertest.html")
        self.sel.open("http://nebuad.com/certertest.html")
        self.sel.refresh()
        self.sel.refresh()
        self.current_time=time.time()
        self.current_time=self.current_time-self.time_lead
        self.current_time=datetime.fromtimestamp(self.current_time)
        print "Time of qualifier firing "+ str(self.current_time)    
        logging.info("Time of qualifier firing "+ str(self.current_time))
        print "Qualifier Id="+self.eu_id
        logging.info("Qualifier Id="+self.eu_id)
        self.qu_check(self.eu_id)
        
    def test_uc(self):
        print "Start Url Contain Test"  
        logging.info("Start Url Contain Test"  )  
        self.sel.open("http://www.sixapart.com/typepad/whyblog")
        self.sel.refresh()
        self.sel.refresh()
        self.current_time=time.time()
        self.current_time=self.current_time-self.time_lead
        self.current_time=datetime.fromtimestamp(self.current_time)
        print "Time of qualifier firing "+ str(self.current_time)    
        logging.info("Time of qualifier firing "+ str(self.current_time))
        print "Qualifier Id="+self.uc_id
        logging.info("Qualifier Id="+self.uc_id)
        self.qu_check(self.eu_id)
            
        
       
        
        
       
       
       
        
         
 
        
        
        
          

if __name__ == "__main__":
    unittest.main()             
    
        