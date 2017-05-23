/* 
 * File:   httpclient.h
 * Author: igor
 *
 * Created on October 6, 2009, 3:23 PM
 */

#ifndef _HTTPCLIENT_H
#define	_HTTPCLIENT_H

#include <string>
#include <iostream>

#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <time.h>

typedef
struct WORK_THREADS_STATS_ {
    unsigned long totalRequestCounter_;
    unsigned long succeededRequestsCounter_;
    clock_t totalRequestsTime_;
    clock_t succeededRequestsTime_;
    clock_t minSucceededRequestTime_;
    clock_t maxSucceededRequestTime_;
    clock_t avgSucceededRequestTime_;
    clock_t minRequestTime_;
    clock_t maxRequestTime_;
    clock_t avgRequestTime_;
} WorkStat;

class HTTPClient {
public:
    HTTPClient();
    HTTPClient(const HTTPClient& orig);
    virtual ~HTTPClient();

    int InitHTTPParams(const char * hostName, unsigned short port, size_t pathMaxSize, bool repeatedHostnameResolv);
    int ResolveHostName(const char * hostName);
		void ResetStat();
    int SendHTTPRequest(const char * path, const char * userAgent);

    int SendHTTPRequest(const char * hostName, unsigned short port, const char * path, const char * userAgent);

    const char* getCookie();

private:

    clock_t TsDiff(struct timespec *tsStart, struct timespec *tsEnd);

    char * headerOtherParams_;
    Cookie headerCookies_;
    char * headerUserAgent_;
    char headerHostName_[500];
    char hostName_[500];
    bool repeatedHostNameResolve_;
    char * headerPath_;

    struct in_addr addr_[100];
    int addrNumber_;

    unsigned short port_;

    char * outBuffer_;
    char * inBuffer_;

    int socketId_;
    struct sockaddr_in hostAddr_;

    clock_t clocksReqStart_;
    clock_t clocksReqEnd_;
    clock_t clocksReqLen_;
    struct timespec tsStart_;
    struct timespec tsEnd_;
    clockid_t clockId_;

public:
    struct in_addr addrHistory_[100];
    int addrHistoryNumber_;
    struct in_addr localAddr_;
    WorkStat stat_;
};

#endif	/* _HTTPCLIENT_H */

