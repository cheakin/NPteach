

## 实体字段校验@NotNull、@NotEmpty、@NotBlank

1.@NotNull：
不能为null，但可以为empty(""," “,” ") ，一般用在基本数据类型的非空校验上，而且被其标注的字段可以使用 @size/@Max/@Min对字段数值进行大小的控制

2.@NotEmpty：
不能为null，而且长度必须大于0(" “,” ")，一般用在集合类上面

3.@NotBlank：
这玩意只能作用在接收的String类型上，注意是只能，不能为null，而且调用trim()后，长度必须大于0



## @RequestParam接收application/json

@RequestParam --->  application/x-www-form-urlencoded
@RequestBody --->  application/json

> https://blog.csdn.net/qq_40470612/article/details/104225419



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

