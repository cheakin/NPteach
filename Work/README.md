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





## Spring中的切面

### 拦截器和过滤器执行顺序：

 1）.Filter.init();
 2）.Filter.doFilter(); before doFilter
 3）.HandlerInterceptor.preHandle();
 4）.Controller方法执行
 5）.HandlerInterceptor.postHandle();
 6）.DispatcherServlet视图渲染
 7）.HandlerInterceptor.afterCompletion();
 8）.Filter.doFilter(); after doFilter
 9）.Filter.destroy();

### 示意图

![image-20210916101902441](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210916101902441.png)

![image-20210916101851184](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210916101851184.png)

另外，Filter与Interceptor相比。Filter使用的是ServerletRequest，而Interceptor使用的是HttpServerletRequest。HttpServerletRequest继承于ServerletRequest，ServerletRequest比HttpServerletRequest少了请求头和session，在使用身份校验功能时需要留意

