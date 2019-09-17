# BECut - BEC Unit Test
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](code-of-conduct.md)
## Description
This is a library for working with unit test in COBOL. It generates a debug
script to be used by IBM's Debug Tool. There are two main functions of the
library.

1. Generation of a test script
2. Generation of debug script

This library contains a simple gui interface for creating test cases from a
compile listing and creating debug scripts from test cases. The gui is intended for
demonstration purposes. We suggest integrating this into your development tools
so that getting the compile listing and submitting the debug job happen automatically.

## Context
When unit testing, if there is an error I want to know that the error is in my
code and not somewhere else - another program, data in a table, etc. Therefore,
this library assumes that a unit is a program. The technique used could also
stub paragraphs/sections but this avenue has not been explored.

Anything outside of that program is outside of the unit
* Calls to subprograms
* Calls to DB2
* Calls to CICS
* MQ
* ...

BECut does not call stuff outside the program but "tricks" the program into thinking the call was made.


## How to use

### GUI
The gui can be used for creating test scripts from compile listings, editing
existing test scripts, and creating debug scripts. The gui is menu driven. Start
by either creating a new test case or changing an existing one. You will then be
able to fill out what return values the program should have.

You can run the gui by executing dk.bec.unittest.becut.BECutLauncher in your
IDE. You can also build the project and run the executable jar in
target/becut-<version>-jar-with-dependencies.jar

#### Creating a test script
Select in the top menu:

BECut -> create unit test

In the following dialog, point to a compile listing in utf-8 and optionally fill out the test case name and id.

#### Changing a test script
Select in the top menu:

BECut -> Edit test case

In the following dialog, point to a valid test case, for example a test case saved after creating one as described above.

#### Filling out the return values
The main table should now contain the test case. Expanding the test case will
show all of the programs external calls. Expanding each call will show all of
the parameters of the call and expanding the parameters will show the
substructure of the record, until there are no substructures left.

You only need to fill out the parts of the parameters that are returned from the external call.

#### Creating the debug script
Once you have a valid test case you can create the debug script. The debug
script needs two things: the test case and the latest compile listing. If you
just created a new test case, then we have all of the information needed. If you
loaded a test case, then you will be prompted for a valid compile listing first.

In the following dialog, input the file name of where to save the debug script.
The script generated is a set of commands for the debugger.

### Generation of test script
See dk.bec.unittest.becut.integrationtests.GenerateTestScriptIT for an example.

Input: Compile listing and user input

The compiling listing can be parsed using dk.bec.unittest.becut.Parse.parse(File file)

The resulting dk.bec.unittest.becut.compilelist.model.CompileListing can be used to decide what to stub and what values should be stubbed. This information is used to create the dk.bec.unittest.becut.testcase.model.BecutTestCase

Output: dk.bec.unittest.becut.testcase.model.BecutTestCase

### Generation of debug script
See dk.bec.unittest.becut.integrationtests.GenerateDebugScriptIT for an example.

Input: Compile listing and test case

The compiling listing can be parsed using dk.bec.unittest.becut.Parse.parse(File file)

The test case can be read from a serialized (json) BecutTestCase object using jackson.

Output: dk.bec.unittest.becut.debugscript.model.DebugScript

### Executing the debug script
Sample JCL for executing the unit test

```
//JOBNAME0  JOB ,'<USER>',
//          SCHENV=<SCHENV>,
//          MSGCLASS=<MSGCLASS>,
//          NOTIFY=<USER>,
//STEP1     EXEC PGM=<PGMNAME>
//STEPLIB   DD DSN=<RUNTIME.LIB.SCEERUN>,DISP=SHR
//          DD DSN=<DEBUG.LIB.SEQAMOD>,DISP=SHR
//          DD DSN=<LOAD.LIB.LOCATION>,DISP=SHR
//INSPIN    DD DSN=<DEBUG.SCRIPT.LOCATION>
//INSPLOG   DD SYSOUT=*
//INSPCMD   DD DSN=<DEBUG.STARTUP.COMMANDS.LOCATION>,DISP=SHR
//CEEOPTS   DD *,DLM='/*'
TEST(,INSPIN,,)
/*
```

## Build

mvn validate

mvn verify

Note: The validate step will install the koopa dependency to your local maven repo and needs to be run first. This is needed until we can get koopa into maven central. 
## TODO
This is a work in progress and there is unimplemented functionality. Here is a short list of what is on our roadmap.

* Stability
* Handling occurs
* Parameter recording
* CICS call integration like SQL
* Pre/Post conditions
* Test case repository – reuse parts of test cases
* Test Suite, part of build, reporting
* Improve algorithm for finding the next statement
* Handle stubbing multiple calls to the same module in a useful way (e.g. use these return values on invocation #5)
* Look at stubbing paragraph/section
