#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=
AS=

# Macros
CND_PLATFORM=GNU-Linux-x86
CND_CONF=Debug
CND_DISTDIR=dist

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/logreader.o \
	${OBJECTDIR}/WorkThread.o \
	${OBJECTDIR}/Cookie.o \
	${OBJECTDIR}/conf.o \
	${OBJECTDIR}/httpclient.o \
	${OBJECTDIR}/main.o \
	${OBJECTDIR}/LogReplay.o

# C Compiler Flags
CFLAGS=-m64

# CC Compiler Flags
CCFLAGS=-m64
CXXFLAGS=-m64

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-Wl,-rpath /usr/local/lib -lrt -lpthread -lconfig++

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Debug.mk dist/Debug/GNU-Linux-x86/LogReplay

dist/Debug/GNU-Linux-x86/LogReplay: ${OBJECTFILES}
	${MKDIR} -p dist/Debug/GNU-Linux-x86
	g++ -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/LogReplay ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/logreader.o: nbproject/Makefile-${CND_CONF}.mk logreader.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/logreader.o logreader.cpp

${OBJECTDIR}/WorkThread.o: nbproject/Makefile-${CND_CONF}.mk WorkThread.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/WorkThread.o WorkThread.cpp

${OBJECTDIR}/Cookie.o: nbproject/Makefile-${CND_CONF}.mk Cookie.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/Cookie.o Cookie.cpp

${OBJECTDIR}/conf.o: nbproject/Makefile-${CND_CONF}.mk conf.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/conf.o conf.cpp

${OBJECTDIR}/httpclient.o: nbproject/Makefile-${CND_CONF}.mk httpclient.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/httpclient.o httpclient.cpp

${OBJECTDIR}/main.o: nbproject/Makefile-${CND_CONF}.mk main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/main.o main.cpp

${OBJECTDIR}/LogReplay.o: nbproject/Makefile-${CND_CONF}.mk LogReplay.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/LogReplay.o LogReplay.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r build/Debug
	${RM} dist/Debug/GNU-Linux-x86/LogReplay

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
