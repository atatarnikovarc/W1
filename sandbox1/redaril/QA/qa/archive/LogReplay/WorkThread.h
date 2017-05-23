/* 
 * File:   WorkThread.h
 * Author: igor
 *
 * Created on October 17, 2009, 12:47 AM
 */

#ifndef _WORKTHREAD_H
#define	_WORKTHREAD_H

class LogReplay;

class WorkThread {
public:
    WorkThread(LogReplay * logReplay, int threadId);

    WorkThread(const WorkThread& orig);
    virtual ~WorkThread();
    static void InitPThreadsLib();
    static void FreePThreadsLib();
    int Start();
    int Join();

    int threadNum_;
    HTTPClient client_;
private:
    static void * workThread(void *arg);
    LogReplay * logReplay_;
    bool isRunning_;
    pthread_t threadId_;
};

#endif	/* _WORKTHREAD_H */


