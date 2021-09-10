## IDEA中使用远程docker发布

1. IDEA安装docker，连接

   * 下载docker插件

     ![image-20210811131745546](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210811131745546.png)

   * 连接远程docker

     ![image-20210811132100880](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210811132100880.png)

2. 编写`Dockerfile`文件，例如

   ```shell
   # 基础镜像，使用alpine操作系统，使用openjkd11
   FROM openjdk:11
   
   #拷贝jar包
   COPY target/*.jar app.jar
   
   # 声明挂载点，容器内此路径会对应宿主机的某个文件夹
   #VOLUME ["/point", "/tempfile", "/logs"]
   
   # 暴露的端口
   EXPOSE 8000
   
   # 启动容器时的进程
   ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=test"]
   ```

3. 设置启动参数

   * 安装docker插件后dockerfile文件会有启动按钮

     ![image-20210811132314028](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210811132314028.png)

   * 设置启动参数

     ![image-20210811132811335](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210811132811335.png)

   * 还可以设置启动前的操作，如maven打包(`clean package -Dstaging=true`)

     ![image-20210811132955436](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210811132955436.png)

     ![image-20210811133139817](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210811133139817.png)

4. 启动

   略
