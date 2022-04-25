# MySQL

## 备份与恢复

### 备份

`mysqldump --opt -h [ip地址] -P [端口] --u [用户名] -p [密码] --lock-all-tables=true --result-file=[文件位置] --default-character-set=utf8 [数据库名]`



### 恢复

* #### 恢复全量备份：

  `mysql --usroot -p 密码 数据库名 < 全量备份.sql`
  mysql --user=root --password=sybase db_ky_msgcenter < D:\Desktop\SQL\test\1626769938844.sql

* #### 恢复增量备份:

  *前提是需要数据已开启了日志功能*

  1. 恢复全部增量
     `msyqlbinlog --no-defaults /usr/local/mysql/data/msyql-bin.0000002 | msyql -u root -p`
     mysqlbinlog --no-defaults "D:\Program Files\phpstudy_pro\Extensions\MySQL5.7.26\data\binlog.000002" | mysql --user=root --password=sybase
  2. 基于位置恢复增量(binlog)
     `mysqlbinlog --start-position=337 --database=数据库名 binlog的文件位置 > 增量备份.sql`
     mysqlbinlog --start-position=337 --stop-position=527 --database=数据库名 文件位置 > 增量备份.sql
  3. 基于时间点恢复(binlog)
     `mysqlbinlog --no-defaults --start-datetime=‘2021-07-03 19:06:03’ 文件位置 | mysql -u root -p //注意修改日期格式xxx-xx-xx`



### 工具类代码

```java
@Slf4j
public class DatabaseUtils {

public static void main(String[] args) throws ParseException {
   boolean flag = false;

   // 备份     mysqldump --opt -h [ip地址] -P [端口] --u [用户名] -p [密码] --lock-all-tables=true --result-file=[文件位置] --default-character-set=utf8 [数据库名]
   flag = backup("localhost", "3306", "root", "sybase", "D:\\Desktop\\SQL\\test\\" + System.currentTimeMillis() + ".sql", "db_ky_msgcenter");

   // 全量恢复    mysql --u[root] -p [密码] [数据库名] < 全量备份.sql
//        flag = recoverTotal("localhost", "3306", "root", "sybase", "D:\\Desktop\\SQL\\test\\1626833052932.sql", "db_ky_msgcenter");

   // 增量全恢复    msyqlbinlog --no-defaults [binlog的文件位置] | msyql -u [root] -p []
//        flag = recoverIncrement("localhost", "3306", "root", "sybase", "D:\\Program Files\\phpstudy_pro\\Extensions\\MySQL5.7.26\\data\\binlog.000006");

   // 基于位置的增量恢复     mysqlbinlog --start-position=[position] --database=[数据库名] [binlog的文件位置] > [增量备份.sql]
   flag = recoverIncrementByPosition("localhost", "3306", "root", "sybase", "D:\\Program Files\\phpstudy_pro\\Extensions\\MySQL5.7.26\\data\\binlog.000006", 123, 456);

   // 基于时间的增量恢复    mysqlbinlog --no-defaults --start-datetime=[‘2021-07-03 19:06:03’] [文件位置] | mysql -u [root] -p [] //注意修改日期格式xxx-xx-xx
//        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-09-02");
//        flag = recoverIncrementByDate("localhost", "3306", "root", "sybase", "D:\\Program Files\\phpstudy_pro\\Extensions\\MySQL5.7.26\\data\\binlog.000006", date);

   String result = flag ? "finished" : "unfinish!!!";
   System.out.println(result);
}

/**
    * 全量备份
    * mysqldump --opt -h [ip地址] -P [端口] --u [用户名] -p [密码] --lock-all-tables=true --result-file=[文件位置] --default-character-set=utf8 [数据库名]
    * @param ip           ip地址，可以是本机也可以是远程
    * @param port         端口
    * @param userName     数据库的用户名
    * @param password     数据库的密码
    * @param fileFullPath 保存备份文件的路径(全路径)
    * @param database     数据库名
    * @param tables       表名(可以是多个)
    * @return
    */
   public static boolean backup(String ip, String port, String userName, String password, String fileFullPath,
                                String database, String... tables) {

       /*File saveFile = new File(fileFullPath);
       if (!saveFile.exists()) {// 如果目录不存在
           saveFile.mkdirs();// 创建文件夹
       }
       if (!savePath.endsWith(File.separator)) {
           savePath = savePath + File.separator;
       }*/

       //拼接命令行的命令
       //mysqldump -h [ip地址] -P [端口号] -u [用户名] -p [数据库] [表名] --where="[条件]" > [导出sql文件位置]
       // mysqldump -uroot -p123456 --quick --events --all-databases --flush-logs --delete-master-logs --single-transaction > data.sql
       // mysqldump --opt -h hostIP --user=userName --password=password --lock-all-tables=true --result-file=savePath+fileName --default-character-set=utf8 databaseName
       StringBuilder command = new StringBuilder();
       command.append("mysqldump")
               .append(" --opt")
               .append(" --host=").append(ip)
               .append(" --port=").append(port);
       command.
               append(" --user=").append(userName)
               .append(" --password=").append(password)
               .append(" --lock-all-tables=true")
               .append(" --result-file=").append(fileFullPath)
               .append(" --default-character-set=utf8 ");
       if (tables.length > 0) {
           command.append(" --databases ").append(database);
           command.append(" --tables");
           for (String table : tables) {
               command.append(" " + table + " ");
           }
       } else {
           command.append(" ").append(database);
       }
       System.out.println(command.toString());

       if (commandRun(command.toString())) {
           return true;
       }
       /*try {
           //调用外部执行exe文件的javaAPI
           Process process = Runtime.getRuntime().exec(command.toString());
           if (process.waitFor() == 0) {   // 0 表示线程正常终止。
               return true;
           }

       } catch (IOException e) {
           e.printStackTrace();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }*/
       return false;
   }

   /**
    * 全量恢复, 需要添加环境变量
    * mysql --user [用户名] -password [密码] --database [数据库名1] [数据库名2] < 全量备份.sql
    * @param ip            ip地址，可以是本机也可以是远程
    * @param port          端口
    * @param userName      数据库的用户名
    * @param password      数据库的密码
    * @param fileFullPath  binlog文件路径(全路径)
    * @param databases     数据库名
    * @return
    */
   public static boolean recoverTotal(String ip, String port, String userName, String password, String fileFullPath, String... databases) {
       StringBuilder command = new StringBuilder();
       command.append("mysql")
               .append(" --host=").append(ip)
               .append(" --port=").append(port)
               .append(" --user=").append(userName)
               .append(" --password=").append(password);
       if (databases.length > 0) {
           command.append(" --database");
           for (String database : databases) {
               command.append(" " + database + " ");
           }
       }
       command.append(" < " + fileFullPath);
       System.out.println("command = " + command);
       if (commandRun(command.toString())) {
           return true;
       }
       return false;
   }

   /**
    * 增量全恢复
    * msyqlbinlog --no-defaults [binlog的文件位置] | msyql -u [root] -p []
    * @param ip            ip地址，可以是本机也可以是远程
    * @param port          端口
    * @param userName      数据库的用户名
    * @param password      数据库的密码
    * @param fileFullPath  binlog文件路径(全路径)
    * @return
    */
   public static boolean recoverIncrement(String ip, String port, String userName, String password, String fileFullPath) {
       StringBuilder command = new StringBuilder();
       command.append("mysqlbinlog --no-defaults")
               .append(" \"" + fileFullPath + "\"");
       command.append(" | ");
       command.append("mysql")
               .append(" --host=").append(ip)
               .append(" --port=").append(port)
               .append(" --user=").append(userName)
               .append(" --password=").append(password);
       System.out.println("command = " + command);
       if (commandRun(command.toString())) {
           return true;
       }
       return false;
   }

   /**
    * 基于位置的增量恢复
    * mysqlbinlog --start-position=[position] --database=[数据库名] [binlog的文件位置] > [增量备份.sql]
    * @param ip            ip地址，可以是本机也可以是远程
    * @param port          端口
    * @param userName      数据库的用户名
    * @param password      数据库的密码
    * @param fileFullPath  binlog文件路径(全路径)
    * @param startPosition 执行binlog文件时的起始位置
    * @param stopPosition  执行binlog文件时的结束位置,不写则表示从起始位置全执行
    * @return
    */
   public static boolean recoverIncrementByPosition(String ip, String port, String userName, String password, String fileFullPath, int startPosition, int... stopPosition) {
       StringBuilder command = new StringBuilder();

       command.append("mysqlbinlog --no-defaults")
               .append(" --start-position=").append(startPosition);
       if (stopPosition.length > 0) {
           command.append(" --stop-position=").append(stopPosition[0]);
       }
       command.append(" \"" + fileFullPath + "\"");
       command.append(" | mysql")
               .append(" --host=").append(ip)
               .append(" --port=").append(port)
               .append(" --user=").append(userName)
               .append(" --password=").append(password);
       System.out.println("command = " + command);
       if (commandRun(command.toString())) {
           return true;
       }
       return false;
   }

   /**
    * 基于时间的增量恢复
    * mysqlbinlog --no-defaults --start-datetime=[‘2021-07-03 19:06:03’] [文件位置] | mysql -u [root] -p [] //注意修改日期格式xxx-xx-xx
    * @param ip            ip地址，可以是本机也可以是远程
    * @param port          端口
    * @param userName      数据库的用户名
    * @param password      数据库的密码
    * @param fileFullPath  binlog文件路径(全路径)
    * @param startDate     执行binlog文件时的起始位置
    * @return
    */
   public static boolean recoverIncrementByDate(String ip, String port, String userName, String password, String fileFullPath, Date startDate) {
       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String date = format.format(startDate);
       StringBuilder command = new StringBuilder();
       command.append("mysqlbinlog --no-defaults")
               .append(" --start-datetime=‘").append(date).append("’")
               .append(" \"" + fileFullPath + "\"");
       command.append(" | ");
       command.append("mysql")
               .append(" --host=").append(ip)
               .append(" --port=").append(port)
               .append(" --user=").append(userName)
               .append(" --password=").append(password);
       System.out.println("command = " + command);
       if (commandRun(command.toString())) {
           return true;
       }
       return false;
   }

   /**
    * 使用CMD窗口运行命令
    * @param command
    * @return
    */
   public static boolean commandRun(String command) {
       log.info(command);
       try {
           //调用外部执行exe文件的javaAPI
           Process process = Runtime.getRuntime().exec("cmd /c " + command);
           if (process.waitFor() == 0) {   // 0 表示线程正常终止。
               return true;
           }

       } catch (IOException e) {
           e.printStackTrace();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       return false;
   }
}

```



>参考: 
>
>1. [MySQL mysqldump数据导出详解 - pursuer.chen - 博客园 (cnblogs.com)](https://www.cnblogs.com/chenmh/p/5300370.html)
>2. [windows下Java调用mysql的客户端备份和恢复 - 一直在路上的菜鸡 - 博客园 (cnblogs.com)](https://www.cnblogs.com/blogwangwang/p/10552795.html)
>3. [java实现mysql增量备份_MySQL备份案例_weixin_42204930的博客-CSDN博客](https://blog.csdn.net/weixin_42204930/article/details/112082353)
>4. [【MySQL】全量+增量的备份/恢复 - wwcom123 - 博客园 (cnblogs.com)](https://www.cnblogs.com/wwcom123/p/10920678.html)





## 服务器中修改过配置文件(my.ini)后就无法启动

### 原因不明

### 解决

在本机中修改配置，然后将修改后的配置文件放到服务器中对应的位置，就可以了！





## 设置数据库可以远程访问

```shell
GRANT ALL PRIVILEGES ON *.* TO ‘root’@’%’IDENTIFIED BY ‘sybase’ WITH GRANT OPTION;	#任意IP可连接
FLUSH PRIVILEGES;	#刷新
```





## 知识点

### 索引失效

模型数空运最快

模糊匹配

数据类型转换

使用函数

空->null

运算

最左原则

全表扫描更快











# SQL Server

## SQL Server中使用json

* ### 判断是否符合json格式`ISJSON()`

  ```sql
  select ISJSON('{}') as '{}',
  ISJSON('') as '空',
  ISJSON('{"name":"xiaoming","age":20}') as '{...}',
  ISJSON('[]') as '[]',
  ISJSON('[{"name":"小明"}]') as '[{...}]'
  ```

  

* ### 从Json对象中提取标量值 `JSON_VALUE`, (默认宽松模式)

  *使用`$`代替json对象本身；*
  *取对象的属性使用`$.prop`格式，如果属性名包含特殊格式，则使用`"`包裹，如：`$."first name"`；*
  *取数组的语法示例`'$[0].name'`、`'$.arr[0].name'`；*

  ```sql
  declare @jsontext nvarchar(max);
  set @jsontext='
  {
  	"name": "小明",
  	"first name": "first xiaoming",
  	"age": 20,
  	"sex": null,
  	"info": {
  		"addr": "xiaominglu"
  	},
  	"books": [{
  		"name": "语文",
  		"score": 85.5
  	}, {
  		"name": "数学",
  		"score": 98
  	}]
  }
  ';
  
  select JSON_VALUE(@jsontext,'$.name') as '$.name',
  JSON_VALUE(@jsontext,'$.abc') as '$.abc',
  JSON_VALUE(@jsontext,'$.age') as '$.age',
  JSON_VALUE(@jsontext,'$.sex') as '$.sex',
  JSON_VALUE(@jsontext,'$.info') as '$.info',
  JSON_VALUE(@jsontext,'$.info.addr') as '$.info.addr',
  JSON_VALUE(@jsontext,'$.books') as '$.books',
  JSON_VALUE(@jsontext,'$.books[0].name') as '$.books[0].name',
  JSON_VALUE(@jsontext,'$.books[1].score') as '$.books[1].score',
  JSON_VALUE(@jsontext,'$.books[2].name') as '$.books[2].name'
  ```

  

* ### 从Json字符串中提取对象或数组 `JSON_QUERY`

  *这个函数和`JSON_VALUE`是类似的，但它返回的是一个json对象，而不是标量值，如果你试图用JSON_QUERY函数返回一个标量值，那么你将得到一个*NULL*。*

  ```sql
  declare @jsontext nvarchar(max);
  set @jsontext='
  {
  	"name": "小明",
  	"first name": "first xiaoming",
  	"age": 20,
  	"sex": null,
  	"info": {
  		"addr": "xiaominglu"
  	},
  	"books": [{
  		"name": "语文",
  		"score": 85.5
  	}, {
  		"name": "数学",
  		"score": 98
  	}]
  }
  ';
  
  select 
  JSON_QUERY(@jsontext) as '无path',
  JSON_QUERY(@jsontext,'$') as '$',
  JSON_QUERY(@jsontext,'$.name') as '$.name',
  JSON_QUERY(@jsontext,'$.info') as '$.info',
  JSON_QUERY(@jsontext,'$.abc') as '$.abc',
  JSON_QUERY(@jsontext,'$.books') as '$.books',
  JSON_QUERY(@jsontext,'$.books[0]') as '$.books[0]'
  ```

  

* ### 更改JSON字符串的内容 `JSON_MODIFY`

  ```sql
  -- 更改json对象name属性值
  DECLARE @info NVARCHAR(100)='{"name":"John","skills":["C#","SQL"]}'
  PRINT @info
  SET @info=JSON_MODIFY(@info,'$.name','Mike')
  PRINT @info
  ```



> 参考:
>
> 1. [sqlserver：存储Json数据_火焰-CSDN博客_sqlserver存json](https://blog.csdn.net/u010476739/article/details/108559401)
> 2. [SQL Server解析Json格式数据_wangyanglongcc的博客-CSDN博客_sqljson格式提取](https://blog.csdn.net/qq_33246702/article/details/104483027)









# 数据库设计

## 数据库设计参考

> 参考：
>
> 1. [CRMEB数据库设计 - Wiki - Gitee.com](https://gitee.com/ZhongBangKeJi/CRMEB/wikis/数据字典/商品相关)
