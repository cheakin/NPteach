#!/bin/bash
#日志路径 /var/bin/mysqld.log

#安装wget
#yum install wget -y

#下载mysql rpm
wget http://repo.mysql.com/mysql57-community-release-el7.rpm

#安装mysql rpm
rpm -ivh mysql57-community-release-el7.rpm

#安装服务
yum localinstall -y mysql-server 
 
#设置权限
chown mysql:mysql -R /var/lib/mysql

#初始化mysql(生成随机的初始密码),也可以使用 mysqld --initialize-insecure 将初始密码设置为空
#再次初始化前需要删除/var/lib/mysql文件夹下的内容rm -rf /var/lib/mysql
mysqld --initialize

#启动 MySQL
systemctl start mysqld

#完成mysql 安装 查看 MySQL 运行状态
systemctl status mysqld

#查看安装的mysql密码
grep 'temporary password' /var/log/mysqld.log

#添加mysql端口3306和Tomcat端口8080
firewall-cmd --zone=public --add-port=3306/tcp --permanent
#firewall-cmd --zone=public --add-port=8080/tcp --permanent
firewall-cmd --reload


#登录mysql
mysql 

#修改密码策略
set global validate_password_policy=0;
set global validate_password_length=4;
#修改密码 为123456
SET PASSWORD = PASSWORD('123456');

#设置远程登录 登录密码123456
grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;
flush privileges; 

#退出mysql登录
exit;