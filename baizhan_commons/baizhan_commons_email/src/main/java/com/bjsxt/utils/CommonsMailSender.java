package com.bjsxt.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommonsMailSender {

    //java mail 发送邮件的类
    @Autowired
    private JavaMailSender javaMailSender;

    //freemarker配置类，可以把freemarker模板转换为String字符串
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //从配置文件中取到发送方
    @Value("${spring.mail.username}")
    private String from;

    /**
     * @param to      向谁发送邮件
     * @param subject 主题
     * @param content 内容，内容中为订单主键
     * @throws MessagingException
     * @throws IOException
     * @throws TemplateException
     */
    public void send(String to, String subject, String content) throws MessagingException, IOException, TemplateException {

        //MimeMessage java mail 中封装的实体类，包含了邮件的全部信息
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        //由于MimeMessage提供的API不是很方便，解决办法是：使用MimeMessageHelper，更容易操作
        //操作helper实际上就是在操作mimeMessage
        //参数1：实际实体对象，参数2：是否有附件，参数3：编码方式
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

        //设置接收方
        helper.setTo(to);

        //设置发送方
        helper.setFrom(from);

        //设置主题
        helper.setSubject(subject);

        //获取模板，把模板内容解析后转换为字符串
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("order.ftl");

        //创建map，根据map的key设置freemarker中内容
        Map<String, Object> model = new HashMap<>();

        model.put("orderId", content);

        //把模板转换为String字符串
        String freemarkString = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        //java mail中发送内容时支持String参数
        //参数1：发送的内容     参数2：是否解析HTML
        helper.setText(freemarkString, true);
        javaMailSender.send(mimeMessage);
    }

}
