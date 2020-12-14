package com.bilibili;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@SpringBootTest
class Springboot09TaskApplicationTests {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    void contextLoads() {
        //一个简单邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("通知");   //主题
        message.setText("Thanks for you test!");    //内容
        message.setTo("ckisaboy@qq.com");   //收件人
        message.setFrom("ckisaboy@qq.com"); //发件人

        mailSender.send(message);
    }

    @Test
    void contextLoads2() throws MessagingException {
        //一个复杂邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        //组装
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        //正文
        helper.setSubject("公告");
        helper.setText("<p style='color:red'>Thanks for U look it!</p>");

        //附件
        helper.addAttachment("1.jpg",new File("X:/img/1.jpg"));

        helper.setTo("ckisaboy@qq.com");   //收件人
        helper.setFrom("ckisaboy@qq.com"); //发件人

        mailSender.send(mimeMessage);
    }

}
