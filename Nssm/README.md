# nssm如何把jar包注册为服务
## 

## 1下载nssm
去[官网 ](https://nssm.cc/download)下载，目前最新版本是nssm 2.24 下载完解压后有win32和win64的nssm.exe

## 2创建一个bat安装脚本
安装脚本命名为install.bat，然后复制下面代码到文件里面
```shell
nssm install aHchgClient %cd%\jre\bin\java.exe 
nssm set aHchgClient AppParameters -jar %cd%\aHchgClient.jar 
nssm set aHchgClient AppStdout log.log 
nssm set aHchgClient AppStderr err.log 
nssm set aHchgClient AppStopMethodSkip 6 
nssm set aHchgClient AppStopMethodConsole 1000 
nssm set aHchgClient AppThrottle 5000 
nssm set aHchgClient DisplayName aHchgClient-test
nssm set aHchgClient start SERVICE_AUTO_START
```

> PS:  
aHchgClient是服务的名字，aHchgClient-test是服务显示的名字，两个名字可以一样，我用的aHchgClient
我的电脑没有安装jdk，用的是一个免安装的jre，所以%cd%\jre\bin\java.exe 就指定了java.exe路径，如果 安装了jdk并且有JAVA_HOME变量，可以写成JAVA_HOME\bin\java.exe
AppParameters这个要指定jar包的路径，和其它参数（没有其它参数可以不写），我把aHchgClient.jar 放到 jre\bin的目录下了。
SERVICE_AUTO_START是设置服务为自动启动， 如果要配置成延迟启动，可以用SERVICE_DELAYED_AUTO_START

## 3启动服务
把nssm.exe和install放到同一个文件夹下面，双击install就能安装服务了，安装成功后就能在服务列表找到aHchgClient 

> WARNING:  
因为我用的是免安装的jre，所以我把jre也和这两个文件放到一起了，方便管理。我的应用jar包（aHchgClient.jar）在 jre的bin目录下和java.exe同级。如果大家注册服务失败，或者服务注册成功，启动服务失败的时候，看下路径是否正确

#  4如何删除服务
`nssm remove <servicename> `就可以了

## nssm常见命令
### Service installation
```shell
nssm install <servicename>
nssm install <servicename> <program>
nssm install <servicename> <program> [<arguments>]
```

### Service removal
nssm remove
nssm remove <servicename>
nssm remove <servicename> confirm

### Starting and stopping a service
nssm start <servicename>
nssm stop <servicename>
nssm restart <servicename>

### Querying a service's status
nssm status <servicename>
#Sending controls to services
nssm pause <servicename>
nssm continue <servicename>
nssm rotate <servicename>

> tips: 
参考：https://www.moyundong.com/commontools/windows-tools/2jar%E5%8C%85%E5%A6%82%E4%BD%95%E6%B3%A8%E5%86%8C%E4%B8%BA%E6%9C%8D%E5%8A%A1.html#%E5%89%8D%E8%A8%80

