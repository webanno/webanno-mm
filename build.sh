#!/bin/bash

set -ex

mvn clean install -U -DskipTests -Drat.skip=true -f webanno/pom.xml
mvn clean install -U -DskipTests -Drat.skip=true -f webanno-plugin-exmaralda/pom.xml
mvn clean package -U -DskipTests -Drat.skip=true -f webanno-webapp-exm/pom.xml

# docker login --username=remstef
# docker build -t remstef/webanno-exmaralda .
# docker push remstef/webanno-exmaralda
