#!/usr/bin/env bash
export project_home=/home/atatarnikov/IdeaProjects/HP-NG/jbehave/

#java -cp ${project_home}/config:${project_home}/target/*.jar jbehave.MainRun
java -cp ./target/jbehave-1.0-SNAPSHOT-jar-with-dependencies.jar:./target/jbehave-1.0-SNAPSHOT-tests.jar org.junit.runner.JUnitCore selenium.EveloTask
