

## 实体字段校验@NotNull、@NotEmpty、@NotBlank

1.@NotNull：
不能为null，但可以为empty(""," “,” ") ，一般用在基本数据类型的非空校验上，而且被其标注的字段可以使用 @size/@Max/@Min对字段数值进行大小的控制

2.@NotEmpty：
不能为null，而且长度必须大于0(" “,” ")，一般用在集合类上面

3.@NotBlank：
这玩意只能作用在接收的String类型上，注意是只能，不能为null，而且调用trim()后，长度必须大于0

```java
@Null  被注释的元素必须为null
@NotNull  被注释的元素不能为null
@AssertTrue  被注释的元素必须为true
@AssertFalse  被注释的元素必须为false
@Min(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@Max(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@Size(max,min)  被注释的元素的大小必须在指定的范围内。
@Digits(integer,fraction)  被注释的元素必须是一个数字，其值必须在可接受的范围内
@Past  被注释的元素必须是一个过去的日期
@Future  被注释的元素必须是一个将来的日期
@Pattern(value) 被注释的元素必须符合指定的正则表达式。
@Email 被注释的元素必须是电子邮件地址
@Length 被注释的字符串的大小必须在指定的范围内
@NotEmpty  被注释的字符串必须非空
@Range  被注释的元素必须在合适的范围内
```

> 参考: [Spring 中@NotNull, @NotEmpty和@NotBlank之间的区别是什么？ - HappyDeveloper - 博客园 (cnblogs.com)](https://www.cnblogs.com/Terry-Wu/p/8134732.html)
>
> [Spring 提供的参数效验注解_dora_310的博客-CSDN博客](https://blog.csdn.net/dora_310/article/details/115128090)



## @RequestParam接收application/json

@RequestParam --->  application/x-www-form-urlencoded
@RequestBody --->  application/json

> https://blog.csdn.net/qq_40470612/article/details/104225419





## 遍历json的所有键值

```java
//fastjson解析方法
for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
  System.out.println("key值="+entry.getKey());
  System.out.println("对应key值的value="+entry.getValue());
}
```

> 参考: [java中遍历json的key和value_MTone1的博客-CSDN博客_java遍历json的key和value](https://blog.csdn.net/MTone1/article/details/87862485)





## swagger的json路径

`localhost:8086/v2/api-docs?group=1.x`







## Java使用新线程执行命令，waitFor()一直等待，线程阻塞

在执行命令的过程中可能又使用了新线程，可能导致第二个线程报错后停止，而第一个线程一直处于等待状态而阻塞。所以应该使用流去获取线程的内容，知道没有内容了就关闭流然后再结束，不应该仅仅使用waitFor()去判断。

```java
String s;  
Process process = Runtime.getRuntime().exec("cmd /c dir \\windows");  
BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream());  
while((s=bufferedReader.readLine()) != null)  
System.out.println(s);  
process.waitfor();  
```

> 参考:[waitFor（）一直等待，线程阻塞问题_灰色年华-CSDN博客_waitfor](https://blog.csdn.net/barry_hui/article/details/69261616)



## 先执行try，再执行finally，再执行try的return

> [JAVA中 try - finally 与return的爱恨情仇_nO0b-CSDN博客](https://blog.csdn.net/q5706503/article/details/84543406)





## 用 response 做文件下载, 图片预览

> [springboot| 使用 response 做文件下载, 图片预览 - douniwan 的回帖 - 链滴 (ld246.com)](https://ld246.com/article/1573812977277/comment/1573818095346)





## lombok注解

>[lombok注解介绍_sunsfan的博客-CSDN博客_lombok注解](https://blog.csdn.net/sunsfan/article/details/53542374)
>
>[Lombok常用注解 - 草木物语 - 博客园 (cnblogs.com)](https://www.cnblogs.com/ooo0/p/12448096.html)





## @Resource和@Autowired

@Resource是JAVA自己的注解，只可以根据名称自动注入

@Autowired是Spring的注解，可更具类型和名称自动注入





## java.lang.IllegalStateException: Cannot call sendError() after the response has been committed

在文件下载时流未正确关闭

> [轻松解决java.lang.IllegalStateException: Cannot call sendError() after the response has been committed！_Melo_FengZhi的博客-CSDN博客](https://blog.csdn.net/Melo_FengZhi/article/details/111408177)



## List对象中的属性以逗号分隔转字符串

1. 我们使用String.join()函数，给函数传递一个分隔符合一个迭代器，一个StringJoiner对象会帮助我们完成所有的事情

   ```java
   List<String> list= Arrays.asList("aaa",  "bbb", "ccc", "ddd");
   String str= String.join(",", list);
   System.out.println(str);
   // str = "aaa,bbb,ccc,ddd";
   ```

2. 我们使用String.join()函数，给函数传递一个分隔符合一个迭代器，一个StringJoiner对象会帮助我们完成所有的事情

   ```java
   List<String> list= Arrays.asList("aaa",  "bbb", "ccc", "ddd");
   String str= list.stream().collect(Collectors.joining(","));
   System.out.println(str);
   // str = "aaa,bbb,ccc,ddd";
   ```

3. 但是如果list集合中是一个对象，可以用下面的方式来处理

   ```java
   //将集合中TestDemo 对象name值以逗号方式隔开转为字符串
   String names = testDemos.stream().map(TestDemo::getName).collect(Collectors.joining(","));
   System.out.println(names);
   // names = "aaa,bbb,ccc,ddd";
   ```

> 参考:[Java8 List对象中的属性以逗号分隔转字符串__binlong的博客-CSDN博客_java list转string 逗号隔开](https://blog.csdn.net/zhangbinlong/article/details/86218758)

## EeasyExcel使用

> 参考:[真香！Java 导出 Excel 表格竟变得如此简单优雅_vincent-CSDN博客_java生成excel报表](https://blog.csdn.net/wuzhiwei549/article/details/105874226)



## 解决跨域

### 解决

1. 重写使用SpringBoot的配置

   ```java
   @Configuration
   public class CorsConfig implements WebMvcConfigurer {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/**")//项目中的所有接口都支持跨域
                   .allowedOrigins("*")//所有地址都可以访问，也可以配置具体地址(SpringBoot2.4需要替换为)
                   .allowCredentials(true)
                   .allowedMethods("*")//"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"
                   .maxAge(3600);// 跨域允许时间
       }
   }
   ```

   注意：SpringBoot 2.4以后启动会报错，将`.allowedOrigins`替换成`.allowedOriginPatterns`即可

   

2. 使用过滤器

   ```java
   @Slf4j
   @Component
   public class CorsFilter implements Filter {
       @Override
       public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
           HttpServletResponse response = (HttpServletResponse)servletResponse;
           response.setHeader("Access-Control-Allow-Origin", "*");
           response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
           response.setHeader("Access-Control-Max-Age", "3600");
           response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
           response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
           response.setHeader("Pragma", "no-cache");
           filterChain.doFilter(servletRequest,response);
       }
   }
   ```



### 测试页面

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>天地图</title>
    <script type="text/javascript" src="http://api.tianditu.gov.cn/api?v=4.0&tk=b0a4ad38470e7982bb91404fa6c976a2"></script>
    <script>
        var map;
        var zoom = 12;
        function onLoad() {
            map = new T.Map('mapDiv');
            map.centerAndZoom(new T.LngLat(116.40769, 39.89945), zoom);
        }
    </script>
</head>
<body onLoad="onLoad()">
<div id="mapDiv" style="position:absolute;width:100%; height:100%"></div>
</body>
</html>
```



>参考:
>
>[九、Spring Boot 优雅的实现CORS跨域 - SegmentFault 思否](https://segmentfault.com/a/1190000021189212)
>
>https://blog.csdn.net/jxysgzs/article/details/110818712





## Hutool

*此模块记录`Hutool`中一些遇到的工具类

### 压缩

压缩可以使用`ZipUtil.zip`方法

- 打包到当前目录（可以打包文件，也可以打包文件夹，根据路径自动判断）

```java
//将aaa目录下的所有文件目录打包到d:/aaa.zip
ZipUtil.zip("d:/aaa");Copy to clipboardErrorCopied
```

- 指定打包后保存的目的地，自动判断目标是文件还是文件夹

```java
//将aaa目录下的所有文件目录打包到d:/bbb/目录下的aaa.zip文件中
// 此处第二个参数必须为文件，不能为目录
ZipUtil.zip("d:/aaa", "d:/bbb/aaa.zip");

//将aaa目录下的所有文件目录打包到d:/bbb/目录下的ccc.zip文件中
ZipUtil.zip("d:/aaa", "d:/bbb/ccc.zip");Copy to clipboardErrorCopied
```

- 可选是否包含被打包的目录。比如我们打包一个照片的目录，打开这个压缩包有可能是带目录的，也有可能是打开压缩包直接看到的是文件。zip方法增加一个boolean参数可选这两种模式，以应对众多需求。

```java
//将aaa目录以及其目录下的所有文件目录打包到d:/bbb/目录下的ccc.zip文件中
ZipUtil.zip("d:/aaa", "d:/bbb/ccc.zip", true);Copy to clipboardErrorCopied
```

- 多文件或目录压缩。可以选择多个文件或目录一起打成zip包。

```java
ZipUtil.zip(FileUtil.file("d:/bbb/ccc.zip"), false, 
    FileUtil.file("d:/test1/file1.txt"),
    FileUtil.file("d:/test1/file2.txt"),
    FileUtil.file("d:/test2/file1.txt"),
    FileUtil.file("d:/test2/file2.txt")
);Copy to clipboardErrorCopied
```

1. 解压

`ZipUtil.unzip` 解压。同样提供几个重载，满足不同需求。

```java
//将test.zip解压到e:\\aaa目录下，返回解压到的目录
File unzip = ZipUtil.unzip("E:\\aaa\\test.zip", "e:\\aaa");
```

**压缩并添加密码**

Hutool或JDK的Zip工具并不支持添加密码，可以考虑使用[Zip4j](https://github.com/srikanth-lingala/zip4j)完成，以下代码来自Zip4j官网。

```java
ZipParameters zipParameters = new ZipParameters();
zipParameters.setEncryptFiles(true);
zipParameters.setEncryptionMethod(EncryptionMethod.AES);
// Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256); 

List<File> filesToAdd = Arrays.asList(
    new File("somefile"), 
    new File("someotherfile")
);

ZipFile zipFile = new ZipFile("filename.zip", "password".toCharArray());
zipFile.addFiles(filesToAdd, zipParameters);
```

> [压缩工具-ZipUtil (hutool.cn)](https://www.hutool.cn/docs/#/core/工具类/压缩工具-ZipUtil?id=压缩工具-ziputil)

