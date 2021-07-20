## 安装

1. 在[官网](http://www.sonatype.org/nexus/archived/)下载压缩包

2. 解压后进入到bin目录下(如：.../nexus-3.14.0-04-win64/nexus-3.14.0-04/bin)

3. 【略】可以在 ../nexus-3.14.0-04-win64/nexus-3.14.0-04/bin/nexus.vmoptions修改配置，如监听端口、上下文路径、内存等

4. 安装

   * `nexus.exe /run` 命令可以启动nexus服务（参考[官方文档](https://help.sonatype.com/repomanager3/installation/installation-methods))

   * 安装nexus本地服务来启动(推荐使用这种方式，参考[官方文档](https://help.sonatype.com/repomanager3/installation/run-as-a-service)),命令如下所示。

     `nexus.exe /install <optional-service-name>　　//安装nexus服务`

5. 启动/关闭/卸载nexus服务

   `nexus.exe /start <optional-service-name>   //启动nexus服务 `

   `nexus.exe /stop <optional-service-name>    //停止nexus服务`

   `sc delete <optional-service-name>    //卸载nexus服务`

6. 登录

   > 如没有修改配置，则直接访问 http://localhost:8081即可

   * **Nexus2**：时默认账号是`admin`，默认密码是`admin123`
   * Nexus3之后默认账号是`admin`，默认密码会在弹窗中提示保存的位置，注意查看。

7. 仓库类型

8. 默认安装有以下这几个仓库，在控制台也可以修改远程仓库的地址，第三方仓库等。

   | 仓库名               | 作用                                                        |
   | -------------------- | ----------------------------------------------------------- |
   | hosted（宿主仓库库） | 存放本公司开发的jar包（正式版本、测试版本）                 |
   | proxy（代理仓库）    | 代理中央仓库、Apache下测试版本的jar包                       |
   | group（组仓库）      | 使用时连接组仓库，包含Hosted（宿主仓库）和Proxy（代理仓库） |
   | virtual (虚拟仓库)   | 基本用不到，重点关注上面三个仓库的使用                      |

9. 删包

   没有 unpublish，现在推荐使用 deprecate 命令，deprecate 命令 只是标记一下此包废弃，在安装的时候会在命令行warn一下，但还是可以装`npm deprecate my-package@1.0.0`

   * 非要删除，那只能去 Nexus 的后台删除了，点击左侧菜单 browse，选择对应的 包



## 切换仓库源

` npm config set registry http://localhost:8081/repository/miitech/` 

## 推送到私有库

> 推送到host库，而不是项目库

*需要新建npm项目`npm init`*

* 添加用户

  `nmp adduser -registry=http://localhost:8081/repository/hosted/`

* 推送项目

  ` npm publish --registry=http://localhost:8081/repository/hosted/`





## NPM仓库

> 设置npm仓库是可能用到以下知识点

###  Maximum component age

这个的含义是获取到 remote 包后，都长时间开始缓存，改成 -1， 意思是获取到就缓存

### Maximum metadata age

多长时间去 remote 仓库获取下源信息， 默认是 30天（可以改短一点）

### Negative Cache

都去掉，意思是不缓存没有获取到更新的信息

### **Cleanup**

这个是设置清除缓存的选项，因为一直缓存，磁盘存储是越来越多，总有一天会爆满，需要定时清除 缓存的依赖，比如 30 天都没有人用的依赖就应该清除掉，到时候再有人用的时候就自动去 remote 拉取





### 这个的含义是获取到 remote 包后，都长时间开始缓存，改成 -1， 意思是获取到就缓存





> 参考： 
>
> 1. [windows系统nexus3安装和配置 - 胡峻峥 - 博客园 (cnblogs.com)](https://www.cnblogs.com/hujunzheng/p/9807646.html)
> 2. [前端工程化之内部 npm 组件管理（1）- 私服搭建 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/146396429)
>
> 





