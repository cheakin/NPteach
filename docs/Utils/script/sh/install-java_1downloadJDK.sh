echo "start install jdk1"

mkdir /usr/local/java
cd /usr/local/java
echo "mkdir success"
wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "https://repo.huaweicloud.com/java/jdk/8u202-b08/jdk-8u202-linux-x64.tar.gz"
#wget http://192.168.12.47/upload/jdk-9.0.1_linux-x64_bin.tar.gz
echo "wget success"

#tar -zxvf /usr/local/java/jdk-9.0.1_linux-x64_bin.tar.gz
tar -zxvf /usr/local/java/jdk-8u202-linux-x64.tar.gz
echo "tar success"