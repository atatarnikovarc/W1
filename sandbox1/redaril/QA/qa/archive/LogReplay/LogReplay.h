/* 
 * File:   LogReplay.h
 * Author: igor
 *
 * Created on October 14, 2009, 5:44 PM
 */

#ifndef _LOGREPLAY_H
#define	_LOGREPLAY_H

class LogReplay {
public:
    LogReplay();
    LogReplay(const LogReplay& orig);
    virtual ~LogReplay();

    int InitializeTest(int argc, char** arg);
    int StartTest();
    int PrintResults();

    LogReader logReader_;
    Conf conf_;
    WorkThread * workThreads_[1000];
    int startedThreadsNum_;
private:
    time_t testStartTime_;
    time_t testEndTime_;
    time_t totalTestTime_;

    clock_t clocksInSecond_;
};

#endif	/* _LOGREPLAY_H */

