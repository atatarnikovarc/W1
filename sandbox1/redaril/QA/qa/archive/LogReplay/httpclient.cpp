/* 
 * File:   httpclient.cpp
 * Author: igor
 * 
 * Created on October 6, 2009, 3:23 PM
 */

#include "Cookie.h"
#include "httpclient.h"
#include "stdlib.h"
#include <unistd.h>

#include <pthread.h>
#include <time.h>
#include <fcntl.h>
#include <netdb.h>
#define IN_BUFFER_SIZE 1024*10
#define OUT_BUFFER_SIZE 1024*10

HTTPClient::HTTPClient() {
    headerOtherParams_ = NULL;
    headerUserAgent_ = NULL;
    headerPath_ = NULL;
    outBuffer_ = NULL;
    inBuffer_ = NULL;
    addrHistoryNumber_ = 0;
    localAddr_.s_addr = inet_addr("127.0.0.1");
}

HTTPClient::HTTPClient(const HTTPClient& orig) {
}

HTTPClient::~HTTPClient() {
    free(headerPath_);
    free(inBuffer_);
    free(outBuffer_);
}

int HTTPClient::InitHTTPParams(const char * hostName, unsigned short port, size_t pathMaxSize,
        bool repeatedHostnameResolve) {
    headerPath_ = (char *) malloc(strlen("/partners/universal/in?pid=139") + pathMaxSize + 1);

    if (headerPath_ == NULL) {
        return -1;
    }

    headerOtherParams_ = "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: en-us,en;q=0.5\r\nAccept-Encoding: gzip,deflate\r\nAccept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\nKeep-Alive: 300\r\nConnection: keep-alive";
    //headerCookies_ = "Cookie: o=0\r\n";// "Cookie: u=131327215009793; o=0\r\n";
    headerUserAgent_ = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.14) Gecko/2009090216 Ubuntu/9.04 (jaunty) Firefox/3.0.14";
    port_ = port;
    strcpy(hostName_, hostName);
    sprintf(headerHostName_, "Host: %s:%d", hostName, port);
    repeatedHostNameResolve_ = repeatedHostnameResolve;
    //socketId_ = socket(AF_INET, SOCK_STREAM, 0);
    hostAddr_.sin_family = AF_INET;
    hostAddr_.sin_port = htons(port);
    if (ResolveHostName(hostName) < 0) {
        return -1;
    }
    hostAddr_.sin_addr.s_addr = addr_[0].s_addr;

    outBuffer_ = (char *) malloc(strlen("/partners/universal/in?pid=139")
    //outBuffer_ = (char *) malloc(strlen("/a?")
            + strlen(headerOtherParams_)
            + strlen(headerUserAgent_)
            + 4000
            + strlen(headerHostName_)
            + pathMaxSize
            + 900);

    if (outBuffer_ == NULL) {
        free(headerPath_);
        return -1;
    }
    
    inBuffer_ = (char *) malloc(1024);
    if (inBuffer_ == NULL) {
        free(headerPath_);
        free(outBuffer_);
        return -1;
    }

		ResetStat();
    return 0;
}

int HTTPClient::SendHTTPRequest(const char* path, const char* userAgent) {
    int httpResult = -1;
    socketId_ = socket(AF_INET, SOCK_STREAM, 0);

    pthread_getcpuclockid(pthread_self(), &clockId_);
    clock_gettime(CLOCK_MONOTONIC, &tsStart_);

    if (repeatedHostNameResolve_ == true) {
        if (stat_.totalRequestCounter_ % 3000 == 0) {
            ResolveHostName(hostName_);
        }
        int addrIndex = stat_.totalRequestCounter_ % addrHistoryNumber_;
        hostAddr_.sin_addr.s_addr = addrHistory_[addrIndex].s_addr;
    }

    int res = connect(socketId_, (struct sockaddr *) & hostAddr_, sizeof (hostAddr_));
    if (res == 0) {
        sprintf(outBuffer_, "GET %s HTTP/1.1\r\n%s\r\nUser-Agent: %s\r\n%s\r\n%s\r\n",
                path, headerHostName_, headerUserAgent_, headerOtherParams_, headerCookies_.cookie.c_str());
        write(socketId_, outBuffer_, strlen(outBuffer_));
        int len = read(socketId_, inBuffer_, 1024);
        if (len > 0) {
            inBuffer_[len] = 0;
            headerCookies_.update(inBuffer_);
            if (inBuffer_[9] == '2' && inBuffer_[10] == '0' && inBuffer_[11] == '0')
                httpResult = 0;
        }
    }

    close(socketId_);

    clock_gettime(clockId_, &tsEnd_);

    clock_gettime(CLOCK_MONOTONIC, &tsEnd_);

    clocksReqLen_ = TsDiff(&tsStart_, &tsEnd_);

    if (clocksReqLen_ < stat_.minRequestTime_) {
        stat_.minRequestTime_ = clocksReqLen_;
    }

    if (clocksReqLen_ > stat_.maxRequestTime_) {
        stat_.maxRequestTime_ = clocksReqLen_;
    }
    stat_.totalRequestCounter_++;
    stat_.totalRequestsTime_ += clocksReqLen_;

    if (httpResult == 0) {
        stat_.succeededRequestsTime_ += clocksReqLen_;
        stat_.succeededRequestsCounter_++;

        if (clocksReqLen_ < stat_.minSucceededRequestTime_) {
            stat_.minSucceededRequestTime_ = clocksReqLen_;
        }

        if (clocksReqLen_ > stat_.maxSucceededRequestTime_) {
            stat_.maxSucceededRequestTime_ = clocksReqLen_;
        }
    }
    return httpResult;
}

// returns 0 if success -1 otherwise
int HTTPClient::ResolveHostName(const char * hostName) {
    struct hostent *remoteHost;
    struct in_addr addr;
    addrNumber_ = 0;
    // If the user input is an alpha name for the host, use gethostbyname()
    // If not, get host by addr (assume IPv4)
    if (isalpha(hostName[0])) { /* host address is a name */
        remoteHost = gethostbyname(hostName);
    } else {
        addr.s_addr = inet_addr(hostName);
        if (addr.s_addr == INADDR_NONE) {
            return -1;
        } else
            remoteHost = gethostbyaddr((char *) & addr, 4, AF_INET);
    }

    if (remoteHost == NULL) {
        return -1;
    } else {
        while (remoteHost->h_addr_list[addrNumber_] != 0) {
            addr.s_addr = *(u_long *) remoteHost->h_addr_list[addrNumber_];
            if (addr.s_addr != localAddr_.s_addr) {
                addr_[addrNumber_].s_addr = addr.s_addr;
                bool addrNotInHistory = true;
                for (int i = 0; i < addrHistoryNumber_; i++) {
                    if (addrHistory_[i].s_addr == addr_[addrNumber_].s_addr) {
                        addrNotInHistory = false;
                        break;
                    }
                }
                if (addrNotInHistory == true) {
                    addrHistory_[addrHistoryNumber_++].s_addr = addr_[addrNumber_].s_addr;
                }
            }
            addrNumber_++;
        }
    }
    return 0;
}

clock_t HTTPClient::TsDiff(struct timespec *tsStart, struct timespec *tsEnd) {
    clock_t res = (tsEnd->tv_sec - tsStart->tv_sec)* 1000000 + (tsEnd->tv_nsec - tsStart->tv_nsec) / 1000;
    return res;
};

void HTTPClient::ResetStat(){
	    memset(&stat_, 0, sizeof (stat_));
    stat_.minRequestTime_ = 999999999l;
    stat_.minSucceededRequestTime_ = 999999999l;
}

const char* HTTPClient::getCookie()
{
    return headerCookies_.cookie.c_str();
}