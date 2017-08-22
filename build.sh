#!/bin/bash

set -ex

if [ -z "$1" ]; then
	mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno/pom.xml
	mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno-plugin-exmaralda/pom.xml
	mvn clean package -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno-webapp-exm/pom.xml
else
	if [ "$1" = "docker" ]; then
		mvn -f webanno-webapp-exm/pom.xml -Pdocker docker:build docker:push
	fi
fi


