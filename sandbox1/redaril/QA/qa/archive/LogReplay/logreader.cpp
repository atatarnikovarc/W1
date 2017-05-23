/* 
 * File:   logreader.cpp
 * Author: igor
 * 
 * Created on October 8, 2009, 1:49 PM
 */

#include "logreader.h"
#include <string.h>
#include <stdlib.h>

LogReader::LogReader() {
	logRecordsBuffer_ = NULL;
	maxLogRecordSize_ = 0;
	logRecordsCount_ = 0;
	loadedLogRecordsCount_ = 0;
	currentPos_ = 0;
}

LogReader::LogReader(const LogReader& orig) {
}

LogReader::~LogReader() {
	logFile_.close();
}

int LogReader::OpenLogFile(const char * fileName) {
    logFile_.open(fileName, ifstream::in);
    if (logFile_.is_open() == false)
        return -1;    
    return 0;
}

size_t LogReader::FindMaxPathSize() {
	char * lineBuffer = (char *) malloc(MAX_LINE_SIZE);
	if (lineBuffer == NULL) {
		cout << "Could not allocate enough memory for line buffer\r\n";
		return 0;
	}

	char * pathBuffer = (char *) malloc(MAX_LINE_SIZE);
	if (pathBuffer == NULL) {
		cout << "Could not allocate enough memory for path buffer\r\n";
		return 0;
	}

	size_t maxPathSize = 0;
	size_t pathSize = 0;
	unsigned long lineCount = 0;
	logFile_.seekg(ios_base::beg);

	while (!logFile_.eof()) {
		logFile_.getline(lineBuffer, MAX_LINE_SIZE);
		pathSize = ExtractPathFromLine(lineBuffer, pathBuffer, MAX_LINE_SIZE);
		if (maxPathSize < pathSize) {
			maxPathSize = pathSize;
		}
		lineCount++;
	}

	cout << "The log file contains:\t" << lineCount << " lines\r\n";
	cout << "Max path lenght:\t" << maxPathSize << "\r\n";

	free(lineBuffer);
	free(pathBuffer);

	maxLogRecordSize_ = maxPathSize + 1;
	logRecordsCount_ = lineCount;

	return maxPathSize;
}

size_t LogReader::ExtractPathFromLine(const char * line, char * pathBuffer, int pathBufferSize) {
	size_t pathFieldEndIndex = 0;
	size_t pathFieldBeginIndex = 0;
	int fieldCounter = 0;

	while (line[pathFieldBeginIndex] != 0) {
		if (line[pathFieldBeginIndex++] == '\t') {
			fieldCounter++;
			if (fieldCounter == 7) {
				break;
			}
		}
	}

	pathFieldEndIndex = pathFieldBeginIndex + 1;
	while (line[pathFieldEndIndex++] != 0) {
		if (line[pathFieldEndIndex] == '\t') {
			break;
		}
	}

	size_t fieldLen = pathFieldEndIndex - pathFieldBeginIndex;

	if (fieldLen > pathBufferSize) {
		fieldLen = pathBufferSize - 1;
	}

	memcpy(pathBuffer, line + pathFieldBeginIndex, fieldLen);
	pathBuffer[fieldLen] = 0;

	return fieldLen + 1;
}

size_t LogReader::LoadLogRecords(size_t maxRecordsCount) {

	FindMaxPathSize();

	if (maxRecordsCount > logRecordsCount_) {
		maxRecordsCount = logRecordsCount_;
	}

	//Allocate memory
	char * lineBuffer = (char *) malloc(MAX_LINE_SIZE);
	if (lineBuffer == NULL) {
		cout << "Could not allocate enough memory for line buffer\r\n";
		return 0;
	}

	char * pathBuffer = (char *) malloc(MAX_LINE_SIZE);
	if (pathBuffer == NULL) {
		cout << "Could not allocate enough memory for path buffer\r\n";
		return 0;
	}

	logRecordsBuffer_ = (char *) malloc(maxRecordsCount * (maxLogRecordSize_));
	if (logRecordsBuffer_ == NULL) {
		cout << "Could not allocate enough memory for log records buffer\r\n";
		return 0;
	}

	cout << "Loading records...";

	loadedLogRecordsCount_ = 0;
	logFile_.clear();
	logFile_.seekg(ifstream::beg);
	while (!logFile_.eof() && loadedLogRecordsCount_ < maxRecordsCount) {
		logFile_.getline(lineBuffer, MAX_LINE_SIZE);
		ExtractPathFromLine(lineBuffer, pathBuffer, MAX_LINE_SIZE);
		strcpy((logRecordsBuffer_ + maxLogRecordSize_ * loadedLogRecordsCount_), pathBuffer);
		//		cout << "\t" << (char*)(logRecordsBuffer_ + maxLogRecordSize_*loadedLogRecordsCount_) << "\r\n";
		loadedLogRecordsCount_++;
	}

	cout << "... " << loadedLogRecordsCount_ << " records loaded\r\n";

	free(lineBuffer);
	free(pathBuffer);

	return loadedLogRecordsCount_;
}

char * LogReader::GetLogRecordsBuffer() {
	return logRecordsBuffer_;
}

size_t LogReader::GetMaxLogRecordSize() {
	return maxLogRecordSize_;
}

void LogReader::GetNextRecord(char* buffer) {
	strcpy(buffer, (logRecordsBuffer_ + maxLogRecordSize_ * currentPos_));
	currentPos_++;
	if (currentPos_ == loadedLogRecordsCount_) {
		currentPos_ = 0;
	}
}

unsigned long LogReader::GetLoadedRecordsCount() {
	return loadedLogRecordsCount_;
}