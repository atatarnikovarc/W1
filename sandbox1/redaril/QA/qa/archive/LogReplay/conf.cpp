/* 
 * File:   conf.cpp
 * Author: igor
 * 
 * Created on October 6, 2009, 2:31 PM
 */

#include "conf.h"
#include <stdlib.h>
#include <stdio.h>
#include <libio.h>
#include <sys/socket.h>
#include <netdb.h>

#define OPTIONS_STRING "d:h:p:f:t:n:b:c:vr"

Conf::Conf() {
	AssignDefaultValues();
}

Conf::Conf(int argc, char** argv) {    
    ParseArguments(argc, argv);
}

Conf::Conf(const Conf& orig) {
}

Conf::~Conf() {
}

void Conf::AssignDefaultValues(){
    hostName_.clear();
    port_ = 0;
    addrNumber_ = 0;
    logFileName_.clear();
    threadsNumber_ = 0;
    runTime_ = 0;
    delayTime_ = -1;
    maxLoadedLogRecordsCount_ = 0;
    verboseOutput_ = -1;
    repeatedHostNameResolve_ = -1;
    partnersLog_ = -1;
}

bool Conf::ValidateConf()
{
    if (hostName_.empty()) {
        if (!cfg.lookupValue("hostName", hostName_)) {
            cout << "No target host specified!\r\n";
            return (false);
        }
    }
    if (!port_) {
        if (!cfg.lookupValue("port", port_)) {
            cout << "No target port specified!\r\n";
            return (false);
        }
    }
    if (logFileName_.empty()) {
        if (!cfg.lookupValue("logFileName", logFileName_)) {
            cout << "No log filename specified!\r\n";
            return (false);
        }
    }
    if (!threadsNumber_) {
        if (!cfg.lookupValue("threadsNumber", threadsNumber_)) {
            threadsNumber_ = 1;
        }
    }
    if (!runTime_) {
        if (!cfg.lookupValue("runTime", runTime_)) {
            runTime_ = 10;
        }
    }
    if (delayTime_ == -1) {
        if (!cfg.lookupValue("delayTime", delayTime_)) {
            delayTime_ = 0;
        }
    }
    if (!maxLoadedLogRecordsCount_) {
        if (!cfg.lookupValue("maxLoadedLogRecordsCount", maxLoadedLogRecordsCount_)) {
            maxLoadedLogRecordsCount_ = 100;
        }
    }
    if (verboseOutput_ == -1) {
        if (!cfg.lookupValue("verboseOutput", verboseOutput_)) {
            verboseOutput_ = 0;
        }
    }
    if (repeatedHostNameResolve_ == -1) {
        if (!cfg.lookupValue("repeatedHostNameResolve", repeatedHostNameResolve_)) {
            repeatedHostNameResolve_ = 0;
        }
    }
    if (partnersLog_ == -1) {
        if (!cfg.lookupValue("partnersLog", partnersLog_)) {
            partnersLog_ = 0;
        }
    }
    return (true);
}

int Conf::ParseArguments(int argc, char** argv) {
    AssignDefaultValues();
    int c_opt = 0;
    while ((c_opt = getopt(argc, argv, OPTIONS_STRING)) != EOF) {
        switch (c_opt) {
            case 'h':
                hostName_ = optarg;
                break;
            case 'p':
                port_ = (unsigned short) (atoi(optarg));
                break;
            case 'f':
                logFileName_ = optarg;
                break;
            case 'n':
                threadsNumber_ = atoi(optarg);
                break;
            case 't':
                runTime_ = atoi(optarg);
                break;
            case 'd':
                delayTime_ = atoi(optarg);
                break;
            case 'b':
                maxLoadedLogRecordsCount_ = atol(optarg);
                break;
            case 'c':
                try {
                    cfg.readFile(optarg);
                } catch (FileIOException) {
                    cout << "Invalid config file name specified\r\n";
                } catch (ParseException) {
                    cout << "Couldn't parse the specidied config file\r\n";
                }
                break;
            case 'v':
                verboseOutput_ = 1;
                break;
            case 'r':
                repeatedHostNameResolve_ = 1;
                break;
            case 'a':
                partnersLog_ = 1;
                break;
            default:
                cout << "Usage: LogReplay <param>=<value> ...\n";
                cout<<"Params:\n";
                cout<<"\t-h <host name or ip>\n";
                cout<<"\t-p <port number>\n";
                cout<<"\t-f <log file name>\n";
                cout<<"\t-n <threads number>\n";
                cout<<"\t-b <max number of log lines loaded>\n";
                cout<<"\t-t <time to run test in seconds>\n";
                cout<<"\t-d <time to run test before staring performance measurment>\n";
                cout<<"\t-v do verbose putput\n";
                cout<<"\t-r do repeated host name to ip resolution\n";
                cout<<"\t-c <config file name>\n";
                cout<<"\t-a play partners log\n";
                exit(1);
        }
    }

    if (!ValidateConf())
        return -1;

    if (ResolveHostName(hostName_.c_str()) < 0)
        return -1;

    cout << "Host:\t" << hostName_ << ":" << port_ << "\r\n";
    cout << "Log file:\t" << logFileName_ << "\r\n";
    cout << "Log file type: \t";
    if (partnersLog_) {
        cout << "partners log\r\n";
    }
    else {
        cout << "other log\r\n";
    }
    cout << "Thread number:\t" << threadsNumber_ << "\r\n";
    cout << "Time to run:\t" << runTime_ << " seconds\r\n";
    cout << "Delay time:\t" << delayTime_ << " seconds\r\n";
    cout << "Maximum log records to load:\t" << maxLoadedLogRecordsCount_ << "\r\n";
    if (!verboseOutput_) {
        cout << "Compact output\r\n";
    } else {
        cout << "Verbose output\r\n";
    }

    if (repeatedHostNameResolve_) {
        cout << "Repeated hostname resolve\r\n";
    }
    cout << "\r\n";
}

extern int h_errno;

// returns 0 if success, -1 otherwise
int Conf::ResolveHostName(const char * hostName) {
    struct hostent *remoteHost = NULL;
    struct in_addr addr;

    char **pAlias;
    int i = 0;

    // If the user input is an alpha name for the host, use gethostbyname()
    // If not, get host by addr (assume IPv4)
    if (isalpha(hostName[0])) { /* host address is a name */
        printf("Calling gethostbyname with %s\n", hostName);
        remoteHost = gethostbyname(hostName);
    } else {
        printf("Calling gethostbyaddr with %s\n", hostName);
        addr.s_addr = inet_addr(hostName);
        if (addr.s_addr == INADDR_NONE) {
            printf("The IPv4 address entered must be a legal address\n");
            return -1;
        } else {
            remoteHost = gethostbyaddr((char *)&addr, 4, AF_INET);
        }
    }

    if (remoteHost == NULL) {
        switch (h_errno) {
            case HOST_NOT_FOUND:
                cout << "The specified host is unknown.\r\n";
                break;
            case NO_DATA:
                cout << "The requested name is valid but does not have an IP address.\r\n";
                break;
            case NO_RECOVERY:
                cout << "A non-recoverable name server error occurred.\r\n";
                break;
            case TRY_AGAIN:
                cout << "A temporary error occurred on an authoritative name server. Try again later.\r\n";
                break;
            default:
                break;
        }
        return -1;
    }

    printf("Function returned:\r\n\tOfficial name: %s\r\n", remoteHost->h_name);
    for (pAlias = remoteHost->h_aliases; *pAlias != 0; pAlias++) {
        printf("\tAlternate name #%d: %s\n", ++i, *pAlias);
    }
    printf("\tAddress type: ");
    switch (remoteHost->h_addrtype) {
        case AF_INET:
            printf("AF_INET\n");
            break;
        case AF_INET6:
            printf("AF_INET6\n");
            break;
        case AF_NETBEUI:
            printf("AF_NETBEUI\n");
            break;
        default:
            printf(" %d\n", remoteHost->h_addrtype);
            break;
    }
    printf("\tAddress length: %d\n", remoteHost->h_length);

    i = 0;
    while (remoteHost->h_addr_list[i] != 0) {
        addr.s_addr = *(u_long *) remoteHost->h_addr_list[i++];
        printf("\tIP Address #%d: %s\n", i, inet_ntoa(addr));
        addr_[i].s_addr = addr.s_addr;
        addrNumber_++;
    }
    
    return 0;
}