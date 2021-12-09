#!/bin/shell

# 快速安装MySQL
# CentOS7 中已成功验证
# 使用yum+rpm方式安装
# quick-install of mysql5.7

# 配置阿里云yum镜像源
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo

yum clean all

yum makecache

# 下载mysql rpm
wget http://repo.mysql.com/mysql57-community-release-el7.rpm

# 安装rpm
rpm -ivh http://repo.mysql.com/mysql57-community-release-el7.rpm
# yum 安装mysql服务
yum install -y mysql-community-server

# 启动mysql服务
systemctl start mysqld.service

# 查看mysql服务状态
systemctl status mysqld.service

# 查看安装的mysql密码
grep 'temporary password' /var/log/mysqld.log

TEMP_PWD=$(grep 'temporary password' /var/log/mysqld.log)
PWD=${TEMP_PWD##* }
echo "${PWD}"

# 登录
mysql -uroot -p${PWD}

# 更改密码策略为LOW
# set global validate_password_policy=0;
# 更改密码长度
# set global validate_password_length=0;
# 如此即可随意设置密码：
# set password for 'root'@'localhost'=password('123456');

# 使用新密码登陆
# exit
# mysql -uroot -pbpgbpg++


# 导入你自己的数据库脚本
# use mysql;
# source /your_own_path/you_own_sql.sql