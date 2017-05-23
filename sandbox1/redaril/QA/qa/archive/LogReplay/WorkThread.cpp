/* 
 * File:   WorkThread.cpp
 * Author: igor
 * 
 * Created on October 17, 2009, 12:47 AM
 */

#include <string>
#include <iostream>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#include "conf.h"
#include "Cookie.h"
#include "httpclient.h"
#include "logreader.h"
#include "WorkThread.h"
#include "LogReplay.h"

pthread_mutex_t g_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_attr_t g_pthread_attr_;

WorkThread::WorkThread(LogReplay * logReplay, int threadId) {
    threadId_ = threadId;
    logReplay_ = logReplay;
    isRunning_ = false;
}

WorkThread::WorkThread(const WorkThread& orig) {
}

WorkThread::~WorkThread() {
}

int WorkThread::Start() {
    int ret = pthread_create(&threadId_, &g_pthread_attr_, WorkThread::workThread, (void *) this);
    if (ret == 0) {
        isRunning_ = true;
    }
    return ret;
}

int WorkThread::Join() {
    int ret, status;
    ret = pthread_join(threadId_, (void **) & status);
    return ret;
}

void * WorkThread::workThread(void *arg)
{
    WorkThread* obj = (WorkThread*)arg;
    HTTPClient* client = &obj->client_;
    LogReplay* logReplay = obj->logReplay_;

    time_t startTime, currTime;
    startTime = time(NULL);
    currTime = time(NULL);
    int runTime;
    int delayTime;
    int ret;
    int initHTTPResult = 0;
    int reqResult = 0;

    /////////////////////////////////////////////
    ret = pthread_mutex_lock(&g_mutex);
    char* logRecordBuffer = (char*)malloc(logReplay->logReader_.GetMaxLogRecordSize() + strlen("/partners/universal/in?") + 1);
    if (logReplay->conf_.partnersLog_) {
        sprintf(logRecordBuffer, "/partners/universal/in?");
    }
    else {
        sprintf(logRecordBuffer, "/a?");
    }
    char* pathRecordAppend = logRecordBuffer + strlen(logRecordBuffer);
    runTime = logReplay->conf_.runTime_;
    delayTime = logReplay->conf_.delayTime_;
    initHTTPResult = client->InitHTTPParams(logReplay->conf_.hostName_.c_str(),
            logReplay->conf_.port_, logReplay->logReader_.GetMaxLogRecordSize(),
            logReplay->conf_.repeatedHostNameResolve_);
    ret = pthread_mutex_unlock(&g_mutex);
    /////////////////////////////////////////////
    if (initHTTPResult < 0) {
        cout << "Coud not initialize HTTP level, check host name\n";
        pthread_exit(NULL);
    }
    if (delayTime > 0) {
        while (difftime(currTime, startTime) < delayTime) {
            /////////////////////////////////////////////
            ret = pthread_mutex_lock(&g_mutex);
            logReplay->logReader_.GetNextRecord(pathRecordAppend);
            ret = pthread_mutex_unlock(&g_mutex);
            /////////////////////////////////////////////
            reqResult = client->SendHTTPRequest(logRecordBuffer, "User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.14) Gecko/2009090216 Ubuntu/9.04 (jaunty) Firefox/3.0.14");
            currTime = time(NULL);
        }
        startTime = time(NULL);
        client->ResetStat();
    }
    while (difftime(currTime, startTime) < runTime) {
        /////////////////////////////////////////////
        ret = pthread_mutex_lock(&g_mutex);
        logReplay->logReader_.GetNextRecord(pathRecordAppend);
        ret = pthread_mutex_unlock(&g_mutex);
        /////////////////////////////////////////////
        reqResult = client->SendHTTPRequest(logRecordBuffer, "User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.14) Gecko/2009090216 Ubuntu/9.04 (jaunty) Firefox/3.0.14");
        currTime = time(NULL);
    }

    pthread_exit(NULL);
}

void WorkThread::InitPThreadsLib() {
    pthread_attr_init(&g_pthread_attr_);
    pthread_attr_setdetachstate(&g_pthread_attr_, PTHREAD_CREATE_JOINABLE);

    size_t stacksize;

    printf("Default stack size = %li\n", stacksize);
    //stacksize = sizeof (double) * 1000 * 1000 + 1000000 ;
    //printf("Amount of stack needed per thread = %li\n", stacksize);
    //pthread_attr_setstacksize(&g_pthread_attr_, stacksize);
}

void WorkThread::FreePThreadsLib() {
    pthread_attr_destroy(&g_pthread_attr_);
}