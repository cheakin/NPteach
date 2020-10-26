package com.bilibili;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.lang.model.SourceVersion;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class Springboot04DataApplicationTests {

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() throws SQLException {
        System.out.println("dataSource数据源 = " + dataSource.getClass());

        //获取连接
        Connection connection = dataSource.getConnection();
        System.out.println(connection);

        // xxxTemplate:SpringBoot已经配置好的模板bean，拿来即用

        //关闭连接
        connection.close();

    }

}
