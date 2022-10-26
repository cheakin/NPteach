#!/bin/bash

echo "start install jdk"

jdk_url="https://cdn.azul.com/zulu/bin/zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz"
package_name="zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz"
java_home="/java_home"

mkdir $java_home
cd $java_home

wget $jdk_url
echo "wget success"

tar -zxvf $java_home/$package_name
echo "tar success"

cat >> /etc/profile << EOF
JAVA_HOME=$java_home/jdk
JRE_HOME=$java_home/jre
PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
export JAVA_HOME JRE_HOME PATH CLASSPATH
EOF
echo "cat success"
source /etc/profile
echo "JDK is installed"