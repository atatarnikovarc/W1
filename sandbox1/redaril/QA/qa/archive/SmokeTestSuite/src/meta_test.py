import unittest, time, re,urllib
import logging
from logger.logger import *
import MySQLdb
import datetime

#===============================================================================
# You need to instal DateTimemx 
# Source - http://www.egenix.com/products/python/mxBase/
#===============================================================================
class meta_test(unittest.TestCase):
    
    
    def setUp(self):
        """SetUp method"""

    
    def tearDown(self):
        """tearDown method"""
   
    def ad_check(self,ad_type,sel):
        """Check that define type Ad is rendered"""
    
        self.ad_type=ad_type
        sel.window_maximize()
        sel.open("http://ezanga.com")       
        self.injection_check()
        time.sleep(20)
        
        if self.ad_type=="whitespacead":
            self.result=sel.get_eval("if (this.browserbot.findElement('id=faireagle_5_22_169_645Header')){'1'}else{'0';}")
            self.result=self.result+sel.get_eval("if (this.browserbot.findElement('id=faireagle_5_22_169_645Header')){'1'}else{'0';}")
            if self.result>=1:
                logging.info("White-Space Ad is rendered on the screen")
                print "White-Space Ad is rendered on the screen"
                return 1
            else:
                #self.fail("White-Space Ad is NOT rendered on the screen")   
                return 0 
            
        elif self.ad_type=="splashad":
            self.result=sel.get_eval("if (this.browserbot.findElement('id=faireagle_centerHeader')){'1'}else{'0';}")
            if self.result==1:
                print "splash is rendered"
                return 1
            else:
                print "splash ads is not rendered"
                return 0     
            
        else:
            print "Enter correct property"

      
    def injection_check(self):
        """Check Script injecting on the page before functional testing itself""" 
        print "Start injection check"
        connect = httplib.HTTPConnection("ezanga.com")
        connect.request("GET", "")
        resp=connect.getresponse()
        inject=resp.read()
        
         
        if self.ASE_host in inject:
            print "Script is injected"
            logging.info("Script is injected into page, starting Functional Testing")
            
        else:
            print "Script isn't injected. No ADs are displayed"
            logging.error("Script isn't injected. No ADs are displayed")
            raise Exception("Script isn't injected")
     
    def deleting_ASE_cookies(self,sel):
        """Delete ASE cookies"""
        print "Deleting ASE cookies"
        sel.open("http://"+self.ASE_host)
        
        sel.delete_cookie("u","/")
        sel.delete_cookie("c","/")
        sel.delete_cookie("d","/")
        sel.delete_cookie("i","/")
        sel.delete_cookie("b","/")
        sel.delete_cookie("t","/")
        sel.delete_cookie("w","/")
        sel.delete_cookie("h","/")
        
        
    def creating_ASE_cookies(self,sel): 
        sel.open("http://"+self.ASE_host)
        sel.create_cookie("u=92886742314626514", "path=/, max_age=60")
        sel.create_cookie("c=SI1", "path=/path/, max_age=60")
        sel.create_cookie("i=64", "path=/, max_age=60")
        sel.create_cookie("b=1191916004242", "path=/, max_age=60")
        sel.create_cookie("t=1191846723726", "path=/, max_age=60")
        sel.create_cookie("w=1191916488360", "path=/, max_age=60")
        sel.create_cookie("h=17195763388764860860", "path=/, max_age=60")
        print "ASE cookies is created -"+ sel.get_cookie()
           
    def UM_API(self,type,profile):
        """Interact with HTTP UM interface"""
        
        if type=="check" and profile=="fine":
            try:
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&cu="+self.cookie_u)
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")     
            self.UM_resp=f.read()
            if self.UM_resp=='':
                print "!Current user is not qualified"
                logging.info('!Fine profile of current user is not qualified')
            else:
                print "Interests for Fine profile of current user is  " +self.UM_resp  
                logging.info("Interests for Fine profile of current user is  " +self.UM_resp)
                
        if type=="check" and profile=="coarse":
            try:
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP)
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")     
            self.UM_resp=f.read()
            if self.UM_resp=='':
                print "!Coarse profile of current user is not qualified"
                logging.info("!Coarse profile of current user is not qualified")
            else:
                print "Coarse profile interests for current user is  " +self.UM_resp  
                logging.info("Coarse profile interests for current user is  " +self.UM_resp)
            
            
        
        if type=="r" and profile=="coarse":
                        
            try:
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&r=yes")
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")     
            self.UM_resp=f.read()
            if self.UM_resp=="":
                print "Interests for Coarse Profile are reset"
                logging.info("Interests for Coarse Profile are reset")
            else:
                self.fail("Interests for Coarse profile are NOT reset. Reseting interest failed")    
                logging.error("Interests for Coarse profile are NOT reset. Reseting interest failed")
           
        if type=="r" and profile=="fine":
            try:
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&cu="+self.cookie_u+"r=yes")    
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")     
            self.UM_resp=f.read()
            if self.UM_resp=="":
                print "Interests for Fine Profile are reset"
                logging.info("Interests for Fine Profile are reset")
            else:
                self.fail("Interests for Fine Profile are reset. Reseting interest failed")    
                logging.error("Interests for Fine Profile are reset. Reseting interest failed")


        if type=="i" and profile=="fine":
            print "Hit fine interest"
            logging.info("Set fine interest for current user: 1002")
            try:
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&cu="+self.cookie_u+"&i=1002")
                time.sleep(10)
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&cu="+self.cookie_u+"&i=1002")
                time.sleep(10)
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&cu="+self.cookie_u+"&i=1002")
                time.sleep(10)
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&cu="+self.cookie_u)
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")     
            self.UM_resp=f.read()
            if self.UM_resp!="":
                print "User is qualified by fine profile:  "+self.UM_resp
                logging.info("User is qualified by fine profile:  "+self.UM_resp)
            elif  self.UM_resp=="":
                print self.UM_resp
                self.fail("Hitting Fine profile interest through HTTP interface failed")    
                logging.error("Hitting Fine profile interest through HTTP interface failed")

                
         
        if type=="i" and profile=="coarse":
            print "Hit coarse interest"
            logging.info("Set coarse interest for current user: 1002")
            try:
                
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&i=1002")
                time.sleep(10)
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&i=1002")
                time.sleep(10)
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&i=1002")
                time.sleep(10)
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&i=1002")
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&i=1002")
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP)
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")       
            
            self.UM_resp=f.read()
            if self.UM_resp!="":
                print "User is qualified by coarse profile:  "+self.UM_resp
                logging.info("User is qualified by coarse profile:  "+self.UM_resp)
            else:
                self.fail("Hitting coarse profile interest through HTTP interface  failed")    
                logging.error("Hitting coarse profile interest through HTTP interface failed")


        if type=="i" and profile=="mix":

            print "Hit mix interest"
            logging.info("Set interest to mix profile: 1002")
            try:
                
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&cu="+self.cookie_u+"&i=1002")
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&cu="+self.cookie_u+"&i=1002")
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&cu="+self.cookie_u+"&i=1002")
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&r=yes")
                f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?c="+self.cookie_c+"&d="+self.cookie_d+"&u="+self.HIP+"&cu="+self.cookie_u)
            except Exception,e:
                print "Can't established connection"
                logging.exception("Exception appears during testing!")       
            self.UM_resp=f.read()
            print "Interests for Mix profile of current user is  "+self.UM_resp
            logging.info("Interests for Mix profile of current user is  "+self.UM_resp)
            if self.UM_resp!="":
                print "User is qualified by fine profile in case of mix request"
                logging.info("User is qualified by fine profile in case of mix request")
            else:
                self.fail("User is NOT qualified by fine profile")    
                logging.error("User is NOT qualified by fine profile in case of mix request")

    def hit_optout_api(self,sel,type):
        """"Interact with opt-out api"""
###Hit one particular category throug Google search
        if type=="no":
            sel.open("http://"+self.ASE_host+"/a?t=o&track=no")
            print "OptOutApi OFF"
        elif type=="yes":
            sel.open("http://"+self.ASE_host+"/a?t=o&track=yes")
            print "OptOutApi ON"
        elif type=="splash":
            sel.open("http://"+self.ASE_host+"/a?t=o&noads=splash")
            "Spash Ads are disabled"
            
                
    def cookie_o_parser(self,sel):
        """Get Vause Cookie O from recieved ASE cookies"""
        sel.open("http://"+self.ASE_host)
        cookies=sel.get_cookie()
        pattern=r'o=(.*?);'
        cpu_re = re.compile(pattern)
        match1 = cpu_re.search(cookies)
        self.cookie_o=cookies[match1.start()+2:match1.end()-1]
        print self.cookie_o
        
            
    def cookie_cu_parser(self,sel):  
        """Get Cookie ID from recieved ASE cookies"""
        sel.open("http://"+self.ASE_host)
        cookies=sel.get_cookie()
        pattern=r'u=(.*?);'
        cpu_re = re.compile(pattern)
        match1 = cpu_re.search(cookies)
        self.cookie_u=cookies[match1.start()+2:match1.end()-1]
        print "ASE Cookie is equal "+self.cookie_u
    
    def u_parser(self):
        f = urllib.urlopen("http://ezanga.com")
        inject=f.read()
        pattern=r'faireagle_u=(.*?);'
        
        try:
            cpu_re = re.compile(pattern)
            match1 = cpu_re.search(inject)
            self.UID=inject[match1.start()+13:match1.end()-2]
        except Exception,e:
            print "Can't get coarse uid from page source "
        print "UID is equal:"+self.UID
        
    def HIP_parser(self):
        f = urllib.urlopen("http://"+self.UM_host+":8080/usermodeler/getcategories?u="+self.UID+"&p")
        inject=f.read()
        print inject
        pattern=r'HIP\+:\s*([\d\w]*)'
        cpu_re = re.compile(pattern)
        match1 = cpu_re.search(inject)
        self.HIP=inject[match1.start()+6:match1.end()]
        print "HIP is equal"+self.HIP
            

    def db_parser(self,opt_out_api_value):
        """Get OPt-Out-Api value"""
        db = MySQLdb.connect(db='certer_si1', host='devdb1.nebuad.local',port=3306, user='root', passwd='901nebuad')
        cursor=db.cursor()
        cursor.execute("select * from nc_user where USER_IDENTIFIER="+self.cookie_u)                 
        result = cursor.fetchall() 
        
        try:
            if opt_out_api_value in result[0]:
                print opt_out_api_value
                self.opt_out_api=opt_out_api_value
                print "Test passed"
            else:
                print opt_out_api_value
                self.opt_out_api=opt_out_api_value
                print "Test failed" 
                self.fail("There is no such record in DB. Test failed.")
        except Exception,e:
            self.fail("There is no such record in DB. Test failed.")
            
    def db_visit_check(self):
        try:
            db = MySQLdb.connect(db=self.db_host, host=self.db_name,port=self.db_port, user=self.db_user, passwd=self.db_password)
            c=db.cursor()
            c.execute("select * from nc_user_page_visit where CREATED_DATE_TIME=(select Max(CREATED_DATE_TIME) from `nc_user_page_visit` where url like '%"+self.log_url+"%') and url like '%"+self.log_url+"%'")                 
            result = c.fetchone()
        except Exception,e:
            print e
            
        date=result[2]
        fail=0
        print "The latest record in DB with such url has such time: "+str(date)
        logging.info("The latest record in DB with such url has such time: "+str(date))

        if date<self.current_time:
            print "Visit is not logged in. Test Failed"
            self.fail("Visit is not logged in. Test Failed")
            logging.error("Visit is not logged in. Test Failed")
        else:
            print "Visit is logged. Test passed" 
            logging.info("Visit is logged.Test passed" )   

    
    def db_last_log_check(self):
        try:
            db = MySQLdb.connect(db=self.db_host, host=self.db_name,port=self.db_port, user=self.db_user, passwd=self.db_password)
            c=db.cursor()
            c.execute("select * from `nc_user_page_visit` where CREATED_DATE_TIME=(select Max(CREATED_DATE_TIME) from `nc_user_page_visit`)")
            result = c.fetchone()
        except Exception,e:
            print e
            
        date=result[2]
        fail=0
        logging.info("The latest record in DB has such time: "+str(date))
        print "The latest record in DB has such time: "+str(date)
     
    
    def qu_check(self,id):
        try:
            db = MySQLdb.connect(db=self.db_host, host=self.db_name,port=self.db_port, user=self.db_user, passwd=self.db_password)
            c=db.cursor()
            c.execute("select * from `nc_qualifier_user_visit` where CREATED_DATE_TIME=(select Max(CREATED_DATE_TIME) from `nc_qualifier_user_visit` where qualifier_id="+id+") and qualifier_id="+id)                 
            result = c.fetchone()
        except Exception,e:
            print e
            logging.exception(e)
            
        date=result[2]
        print "The Date/Time of the latest qualifier firing is "+ str(date)
        logging.info("The Date/Time of the latest qualifier firing is "+ str(date))
        if date<self.current_time:
            self.fail("Qualifier wasn't fired. Test Failed")
            logging.error("Qualifier wasn't fired. Test Failed")
        else:
            print "Test passed" 
            logging.info("Test passed" )   
            
                                
                        
                            
        
    
        
