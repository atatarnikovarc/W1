#! /usr/bin/env python
#Description: that module sends N bid requests to google/api and print summary on sending results
#Could be used to check, how google/api redistribute the probability between Ads of the same size.
#A campaign has a couple of Ads with the same size (campaign_id is set into generator.py,ad list 
#is set up into Ad Central - write these Ads ids into ADS list below)

import subprocess

ADS = [ 
 [101019, 10, 0], #ID, probability, numberOfResponses
 [101601, 20, 0],
 [101602, 30, 0],
 [101603, 20, 0],
 [101604, 5,  0],
 [101605, 15, 0]
]

#total requests to send
N = 20
numberOfSuccessfulResponses  = 0

#**********start of functions defining

def getAdID(filename):
 good = open(filename, "r")
 all = good.read()
 adIdAmpIndex = all.find("&a=")
 nextAmpIndex = all.find("&", adIdAmpIndex + 3)
 good.close()
 return all[adIdAmpIndex+3:nextAmpIndex]

def getFileName():
 ls = subprocess.Popen(['ls'], stdout=subprocess.PIPE)
 grep = subprocess.Popen(['grep', 'good'], stdin=ls.stdout, stdout=subprocess.PIPE)
 tmp = grep.communicate()[0]
 return tmp[0:len(tmp)-1] #remove /n symbol 

def processResponse():
 global numberOfSuccessfulResponses
 filename = getFileName()
 if filename.startswith('good'):
  adId = getAdID(filename)
  if adId.isdigit():
   adIdIndex = getAdIdIndexInList(adId)
   if adIdIndex != -1:
     ADS[adIdIndex][2] = ADS[adIdIndex][2] + 1
     numberOfSuccessfulResponses = numberOfSuccessfulResponses + 1
  
def printSummary():
 print "Successful reponses: ", numberOfSuccessfulResponses 
 print "Error responses: ", N - numberOfSuccessfulResponses
 print " "
 print "ADS percentage for responses: "

 for i in range(0, len(ADS)):
  if numberOfSuccessfulResponses != 0:
   percentage = (float(ADS[i][2]) / float(numberOfSuccessfulResponses)) * 100
  else:
   percentage = 0
  print "ID: ", ADS[i][0], " Weight: ", ADS[i][1], " Number of Responses: ", ADS[i][2], " Percentage: ", percentage

def getAdIdIndexInList(id):
 index = -1
 for i in range(0, len(ADS)):
  if ADS[i][0] == int(id):
   index = i
   break
 return index


def Main():
 for i in range(0, N):
  print i
  subprocess.call(["./sendRequest.sh"],shell=True)
  processResponse()

 print 
 printSummary()

Main()
