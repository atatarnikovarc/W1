/* 
 * File:   logreader.h
 * Author: igor
 *
 * Created on October 8, 2009, 1:49 PM
 */

#ifndef _LOGREADER_H
#define	_LOGREADER_H

#include <fstream>
#include <iostream>

using namespace std;

#define MAX_LINE_SIZE 100*1024

class LogReader {
public:
	LogReader();
	LogReader(const LogReader& orig);
	virtual ~LogReader();

	int			OpenLogFile(const char * fileName_);
	size_t	LoadLogRecords(size_t maxRecordsCount);
	char *	GetLogRecordsBuffer();
	size_t GetMaxLogRecordSize();
	void GetNextRecord(char* buffer);
	unsigned long GetLoadedRecordsCount();

private:

	size_t	FindMaxPathSize();
	size_t	ExtractPathFromLine(const char * line, char * pathBuffer, int pathBufferSize);

	ifstream	logFile_;

	char *		logRecordsBuffer_;
	size_t		maxLogRecordSize_;
	unsigned long logRecordsCount_;
	unsigned long loadedLogRecordsCount_;
	size_t		currentPos_;
};

#endif	/* _LOGREADER_H */

