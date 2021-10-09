# syntax=docker/dockerfile:1
# 基础镜像，使用alpine操作系统，使用openjkd8
FROM mdsol/java11-jre

# 拷贝jar包
COPY *.jar app.jar

# 设置时间及时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

# 声明挂载点，容器内此路径会对应宿主机的某个文件夹
#VOLUME ["/point", "/tempfile", "/logs"]

# 暴露的端口
EXPOSE 8000

# 启动容器时的进程
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=test"]





#1. docekr生成镜像(后面有个.),vv表示版本
#docker build -t wopihost:vv .
#如：docker build -t wopihost:1.0 .

#2. docker打包为.tar包,wopihost_test:vv 与上面保持一致
#docker save -o X:\Desktop\docekr\wopihost.tar wopihost:vv
#如：docker save -o D:\Desktop\docekr\wopihost_1.0.tar wopihost:1.0

#3. 在服务器中将.tar包加载为镜像,运行:(DIR是tar包路径)
#docker load -i DIR
#如：docker load -i C:\Users\Administrator\Desktop\docker\wopihost_1.0.tar

#4. 运行
#如：docker run -d -p 8086:8086 -v C:/point:/point -v C:/point/tempfile:/tempfile -e "SPRING_PROFILES_ACTIVE=test" --name wopi_test wopihost:1.0
#或如：docker run -d -p 8089:8086 -v C:/point:/point -v C:/point/tempfile:/tempfile --name wopi_dev2 wopihost:1.0 --spring.profiles.active=dev


