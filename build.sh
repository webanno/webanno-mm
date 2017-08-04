#!/bin/bash

set -ex

if [ $1 = "docker" ]; then
	docker login --username=remstef
	docker build -t remstef/webanno-exmaralda .
	docker push remstef/webanno-exmaralda
else  
	mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno/pom.xml
	mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno-plugin-exmaralda/pom.xml
	mvn clean package -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno-webapp-exm/pom.xml
fi


