#!/bin/bash

set -ex

if [ -z "$1" ]; then
  mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f pom.xml
elif [ "$1" = "webanno" ]; then
  mvn clean install -U -DskipTests -Dcheckstyle.skip=true -Drat.skip=true -f webanno/pom.xml
elif [ "$1" = "docker" ]; then
  set +x
  echo -n "Password: "
  read -s p
  mvn -f pom.xml -Pdocker docker:build docker:push -Ddocker.image.name=webanno/webanno-mm -Ddocker.username=remstef -Ddocker.password=$p
fi
