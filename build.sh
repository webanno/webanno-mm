#!/bin/bash

set -ex

#mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno/pom.xml
mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno-plugin-exmaralda/pom.xml
mvn clean package -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno-webapp-exm/pom.xml

docker build -t remstef/webanno-exmaralda .
docker login --username=remstef
docker push remstef/webanno-exmaralda
