@echo off
call mvn clean package
call docker build -t org.example/dmit2015-practice2-git .
call docker rm -f dmit2015-practice2-git
call docker run -d -p 9080:9080 -p 9443:9443 --name dmit2015-practice2-git org.example/dmit2015-practice2-git