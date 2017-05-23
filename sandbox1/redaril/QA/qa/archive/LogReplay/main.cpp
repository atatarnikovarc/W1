/* 
 * File:   main.cpp
 * Author: igor
 *
 * Created on October 6, 2009, 12:50 PM
 */

#include <stdlib.h>
#include "conf.h"

#include "Cookie.h"
#include "httpclient.h"
#include "WorkThread.h"
#include "logreader.h"
#include "LogReplay.h"

#include <string>
#include <iostream>
#include <pthread.h>
#include <stdio.h>

using namespace std;

/*
 * 
 */

int main(int argc, char** argv) {
	LogReplay logReplay;

	if (logReplay.InitializeTest(argc, argv) < 0)
            return (EXIT_FAILURE);
	logReplay.StartTest();
	logReplay.PrintResults();

	return (EXIT_SUCCESS);
}