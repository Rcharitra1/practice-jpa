#!/bin/sh
if [ $(docker ps -a -f name=dmit2015-practice2-git | grep -w dmit2015-practice2-git | wc -l) -eq 1 ]; then
  docker rm -f dmit2015-practice2-git
fi
mvn clean package && docker build -t org.example/dmit2015-practice2-git .
docker run -d -p 9080:9080 -p 9443:9443 --name dmit2015-practice2-git org.example/dmit2015-practice2-git
