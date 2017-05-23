/* 
 * File:   LogReplay.cpp
 * Author: igor
 * 
 * Created on October 14, 2009, 5:44 PM
 */

#include <string>
#include <iostream>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

#include "conf.h"
#include "logreader.h"
#include "Cookie.h"
#include "httpclient.h"
#include "WorkThread.h"
#include "LogReplay.h"

LogReplay::LogReplay() {
    startedThreadsNum_ = 0;
    testStartTime_ = NULL;
    testEndTime_ = NULL;
    totalTestTime_ = NULL;
    clocksInSecond_ = CLOCKS_PER_SEC;
}

LogReplay::LogReplay(const LogReplay& orig) {
}

LogReplay::~LogReplay() {
}

int LogReplay::InitializeTest(int argc, char** arg) {
    if (conf_.ParseArguments(argc, arg) < 0) {
        return -1;
    }

    if (logReader_.OpenLogFile(conf_.logFileName_.c_str()) < 0) {
        cout << "\n Could not open log file:" << conf_.logFileName_.c_str() << "\r\n";
        return -1;
    }

    if (logReader_.LoadLogRecords(conf_.maxLoadedLogRecordsCount_) < 0) {
        cout << "\n Could not load log records" << "\r\n";
        return -1;
    }

    return 0;
}

int LogReplay::StartTest() {


    if (logReader_.GetLoadedRecordsCount() < 3) {
        cout << "Could not start the test \r\n";
        return -1;
    }


		WorkThread::InitPThreadsLib();

    int ret;

    for (int i = 0; i < conf_.threadsNumber_; i++) {
        workThreads_[i] = new WorkThread(this, i);
        ret = workThreads_[i]->Start();
        if (ret == 0) {
            startedThreadsNum_++;
        } else {
            printf("Error creating thread %d\n", i);
            break;
        }
    }

    struct tm *ptr;
    testStartTime_ = time(NULL);

    cout << "\r\n"; //Separate from test parameters
    if (startedThreadsNum_ == conf_.threadsNumber_) {
        cout << "All threads started at:\t";
    } else {
        cout << "Only " << startedThreadsNum_ << " of " << conf_.threadsNumber_ << " started at:\t";
    }

    cout << asctime(localtime(&testStartTime_)) << "\r\n";

    for (int i = 0; i < startedThreadsNum_; i++) {
        workThreads_[i]->Join();
    }

    testEndTime_ = time(NULL);
    totalTestTime_ = difftime(testEndTime_, testStartTime_) - conf_.delayTime_;

    cout << "Test completed at:\t" << asctime(localtime(&testEndTime_)) << "\r\n";

		WorkThread::FreePThreadsLib();

    return 0;
}

int LogReplay::PrintResults() {
    unsigned long threadsTotalRequestsCount = 0l;
    clock_t threadsTotalRequestsTime = 0l;
    clock_t threadsMinRequestsTime = 999999l;
    clock_t threadsMaxRequestsTime = 0l;

    unsigned long threadsSucessRequestsCount = 0l;
    clock_t threadsSucessRequestsTime = 0l;
    clock_t threadsSucessMinRequestsTime = 9999999l;
    clock_t threadsSucessMaxRequestsTime = 0l;

    for (int i = 0; i < startedThreadsNum_; i++) {
        threadsTotalRequestsCount += workThreads_[i]->client_.stat_.totalRequestCounter_;
        threadsTotalRequestsTime += workThreads_[i]->client_.stat_.totalRequestsTime_;

        if (threadsMinRequestsTime > workThreads_[i]->client_.stat_.minRequestTime_) {
            threadsMinRequestsTime = workThreads_[i]->client_.stat_.minRequestTime_;
        }

        if (threadsMaxRequestsTime < workThreads_[i]->client_.stat_.maxRequestTime_) {
            threadsMaxRequestsTime = workThreads_[i]->client_.stat_.maxRequestTime_;
        }
        threadsSucessRequestsCount += workThreads_[i]->client_.stat_.succeededRequestsCounter_;
        threadsSucessRequestsTime += workThreads_[i]->client_.stat_.succeededRequestsTime_;

        if (threadsSucessMinRequestsTime > workThreads_[i]->client_.stat_.minSucceededRequestTime_) {
            threadsSucessMinRequestsTime = workThreads_[i]->client_.stat_.minSucceededRequestTime_;
        }

        if (threadsSucessMaxRequestsTime < workThreads_[i]->client_.stat_.maxSucceededRequestTime_) {
            threadsSucessMaxRequestsTime = workThreads_[i]->client_.stat_.maxSucceededRequestTime_;
        }
    }

    cout << "Test time:\t" << totalTestTime_ << "\r\n";
    cout << "Test requests:\ttotal: " << threadsTotalRequestsCount << "\tsuccess: " << threadsSucessRequestsCount << "\r\n";

    cout << "Test avg time:";
    if (threadsTotalRequestsCount > 0) {
        cout << "\ttotal: " << threadsTotalRequestsTime / threadsTotalRequestsCount / 1000;
    } else {
        cout << " - ";
    }


    if (threadsSucessRequestsCount > 0) {
        cout << "\tsuccess: " << threadsSucessRequestsTime / threadsSucessRequestsCount / 1000 << "\r\n";
    } else {
        cout << "\t - \r\n";
    }

    cout << "Test min time:\ttotal: " << (threadsMinRequestsTime / 1000) << "\tsuccess: " << (threadsSucessMinRequestsTime / 1000) << "\r\n";

    cout << "Test max time:\ttotal: " << (threadsMaxRequestsTime / 1000)
            << "\tsuccess: " << (threadsSucessMaxRequestsTime / 1000) << "\r\n";

    if (totalTestTime_ > 0) {
        cout << "Test reqs/s:\ttotal: " << (threadsTotalRequestsCount / totalTestTime_) << "\tsuc in sec: " << (threadsSucessRequestsCount / totalTestTime_) << "\r\n\r\n";
    }
    cout << "Final test cookie for thread 0:\r\n" << workThreads_[0]->client_.getCookie() << "\r\n";

    if (conf_.verboseOutput_ == true) {
        cout << "Thread id\ttotal\tper s\tmin\tavg\tmax\tstotal\tsper s\tsmin\tsavg\tsmax\r\n";
        for (int i = 0; i < startedThreadsNum_; i++) {
            cout << "Thread[" << i << "]: \t"
                    << workThreads_[i]->client_.stat_.totalRequestCounter_ << "\t";


            if (workThreads_[i]->client_.stat_.totalRequestCounter_ > 0) {
                cout << workThreads_[i]->client_.stat_.totalRequestCounter_ * 1000000 / workThreads_[i]->client_.stat_.totalRequestsTime_ << "\t";
                cout << workThreads_[i]->client_.stat_.minRequestTime_ / 1000 << "\t"
                        << workThreads_[i]->client_.stat_.totalRequestsTime_ / workThreads_[i]->client_.stat_.totalRequestCounter_ / 1000 << "\t"
                        << workThreads_[i]->client_.stat_.maxRequestTime_ / 1000 << "\t";

            } else {
                cout << "-\t-\t"
                        << "-\t"
                        << "-\t";
            }
            cout << workThreads_[i]->client_.stat_.succeededRequestsCounter_ << "\t";
            if (workThreads_[i]->client_.stat_.succeededRequestsCounter_ > 0) {
                cout << workThreads_[i]->client_.stat_.succeededRequestsCounter_ * 1000000 / workThreads_[i]->client_.stat_.succeededRequestsTime_ << "\t";
                cout << workThreads_[i]->client_.stat_.minSucceededRequestTime_ / 1000 << "\t"
                        << workThreads_[i]->client_.stat_.succeededRequestsTime_ / workThreads_[i]->client_.stat_.succeededRequestsCounter_ / 1000 << "\t"
                        << workThreads_[i]->client_.stat_.maxSucceededRequestTime_ / 1000 << "\r\n";
            } else {
                cout << "-\t-\t"
                        << "-\t"
                        << "-\t\r\n";
            }
        }
    }
    cout << "\r\n";

    struct in_addr addrHistory[100];
    int addrHistoryNumber = 0;
    for (int i = 0; i < startedThreadsNum_; i++) {
        for (int j = 0; j < workThreads_[i]->client_.addrHistoryNumber_; j++) {
            bool addrNotInHistory = true;
            for (int k = 0; k < addrHistoryNumber; k++) {
                if (addrHistory[k].s_addr == workThreads_[i]->client_.addrHistory_[j].s_addr) {
                    addrNotInHistory = false;
                    break;
                }
            }
            if (addrNotInHistory == true) {
                addrHistory[addrHistoryNumber++].s_addr = workThreads_[i]->client_.addrHistory_[j].s_addr;
            }
        }
    }

    cout << "IP list:\r\n";
    for (int i = 0; i < addrHistoryNumber; i++) {
        cout << "IP #" << i << ":\t" << inet_ntoa(addrHistory[i]) << "\r\n";
    }

    return 0;
}


