/* 
 * File:   conf.h
 * Author: igor
 *
 * Created on October 6, 2009, 2:31 PM
 */

#ifndef _CONF_H
#define	_CONF_H

#include <string>
#include <iostream>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <libconfig.h++>

using namespace std;
using namespace libconfig;

class Conf {
public:
    Conf();
    Conf(int argc, char** argv);
    Conf(const Conf& orig);
    virtual ~Conf();

    void    AssignDefaultValues();
    int     ResolveHostName(const char * hostName);
    int     ParseArguments(int argc, char** arg);
    void    PrintTheAppManual();
    
    Config cfg;
    string          hostName_;                  //name of target host
    unsigned int    port_;                      //port on target host
    struct in_addr  addr_[50];                  //list of resoved ip addresses
    int             addrNumber_;                //number of resoved ip addresses
    string          logFileName_;               //name of log file to replay
    int             threadsNumber_;             //number of working threads
    int             runTime_;                   //time to run the test
    int             delayTime_;                 //time of preliminary traffic generation without performance measurment
    unsigned int    maxLoadedLogRecordsCount_;  //max line count loaded into the buffer
    int             verboseOutput_;             //make verbose output of replay results
    int             repeatedHostNameResolve_;   //resolve host name before each 1000 call
    int             partnersLog_;               //determines the type of log file to replay

private:
    bool ValidateConf();
};

#endif	/* _CONF_H */