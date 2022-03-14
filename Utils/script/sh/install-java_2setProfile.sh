echo "start install jdk2"

cat >> /etc/profile << EOF
JAVA_HOME=/usr/local/java/jdk1.8.0_202
JRE_HOME=/usr/local/java/jdk1.8.0_202/jre
PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
export JAVA_HOME JRE_HOME PATH CLASSPATH
EOF
echo "cat success"
source /etc/profile
echo "JDK is installed"