## Repository进行版本管理
> 参考:
> 1. [PowerDesigner Repository进行版本管理 - 掘金 (juejin.cn)](https://juejin.cn/post/6844904146865225736)
> 2. [powerdesigner 团队合作&版本管理设置_zlsdmx的博客-CSDN博客_powerdesigner16.5 协作](https://blog.csdn.net/zlsdmx/article/details/94624524)
> 3. [PowerDesigner版本控制_ITPUB博客](http://blog.itpub.net/28275505/viewspace-2148725/)
> 4. [PowerDesigner实现版本控制，多人协作_伴我久的博客-CSDN博客_powerdesigner多人协作](https://blog.csdn.net/u011914929/article/details/51719214)

### 说明
利用PowerDesigner（下文简称PD）进行数据库设计时，需要对输出的数据模型文件进行版本控制。常用的办法包括：
-   将pdm文件上传到svn中进行版本控制。这种方式简单粗暴，但是对二进制的pdm文件仅能起到集中备份的作用，无法基于文件内容进行更精细的版本管理。
-   使用PD导出建库SQL脚本，然后上传到svn中进行版本控制。这种方式可以对文本内容进行版本控制，但是因为PD设置不同导出的脚本格式在细节上有很大差异，严重影响对比的效率。
-   使用PD自带的版本控制功能，官方名称叫做资料库（Repository） 。

本文就介绍如何使用PD资料库来规范化数据库模型的版本管理流程。

使用资料库的优点是:
1.  采用和SVN类似的操作，通过可视化方式进行版本对比、迁入迁出。
2.  使用数据库直接保存建模文档的内容，并且在PD中直接查看和编辑，避免线下传递pdm文件造成版本混乱。
3.  集成了灵活的用户和权限机制，可以在大型团队内部对不同用户分别设置只读、读写等权限。权限可以在全局设置，也可以对独立的建模文件设置。

### 初始化仓库（由管理员操作）
点击 Repository -> Repository Definitions 菜单
![[Pasted image 20230104112531.png]]

在弹出的菜单里新建一个Repository，我们取名为`PD_Repo`(仓库名)，然后点击Data Source Name栏
![[Pasted image 20230104112605.png]]

在弹出的Select a Data Source(选择数据源)中点击Connection profile按钮(意为使用配置连接), 没有就新建连接
![[Pasted image 20230104113046.png]]
如果测试连接时一直失败可能的原因是, 环境变量：**PowerDesigner仅支持32位的jdk, 配置完jdk需要重启**, 配置参考[PowerDesigner配置Jdk](#point1)


第一次的时候会提示安装它的数据库, 无脑下一步, 直到完成, 然后就开始设置连接密码(不是数据库密码)
![[Pasted image 20230104113400.png]]
完成后可以在导航栏中看到仓库了
![[Pasted image 20230104113710.png]]

### 连接仓库(成员连接)
点击 Repository -> Connect 菜单
第一次会提示连接数据库，如上创建数据库连接
![[635792d034316f4123bbc20341acc04.png]]
仓库仓库名一定要保持一致, 开始是没有的, 点击新建，创建过程如上
![[5d375104ca065c370d4aac885123e57.png]]

## <a id="point1">附：PowerDesigner配置Jdk</a>
**PowerDesigner仅支持32位的jdk**, 所以需要先下载32的jdk. 32位和64位是独立的, 只要安装的时候不冲突, 且不配置系统环境变量就行.

安装完以后在 Tools -> General Options ->Variables 中配置PowerDesigner的环境变量
| Name     | Value                                                    |
| -------- | -------------------------------------------------------- |
| JAR      | C:\Program Files (x86)\Java\jdk1.8.0_351\bin\jar.exe     |
| JAVA     | C:\Program Files (x86)\Java\jdk1.8.0_351\bin\java.exe    |
| JAVAC    | C:\Program Files (x86)\Java\jdk1.8.0_351\bin\javac.exe   |
| JAVADOOC | C:\Program Files (x86)\Java\jdk1.8.0_351\bin\javadoc.exe |

**配置完jdk需要重启!!!**