#!/bin/bash

jarname='platform-boot-dev-x.x.x'
pid=`ps aux | grep $jarname | grep -v grep | awk '{print $2}'`
echo $pid
kill -9 $pid
nohup java -jar $jarname.jar --spring.profiles.active=prod > error-$jarname.log &