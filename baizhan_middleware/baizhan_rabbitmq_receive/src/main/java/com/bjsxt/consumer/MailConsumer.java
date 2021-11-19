package com.bjsxt.consumer;

import com.bjsxt.message.vo.BaizhanMailMessage;
import com.bjsxt.utils.CommonsMailSender;
import freemarker.template.TemplateException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * 发送邮件消息 消费者
 */
@Component
public class MailConsumer {

    @Autowired
    private CommonsMailSender mailSender;

    /**
     * 需要使用javamail工具
     * 需要准备：
     * 一个可以发送邮件的可登录的邮箱
     * 邮箱：
     * 授权码：
     *
     * @param message
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "${baizhan.trade.mailMQ.queue}", autoDelete = "false"),
                    exchange = @Exchange(value = "${baizhan.trade.mailMQ.exchange}", type = "topic"),
                    key = "#"
            )
    })
    public void onMessage(BaizhanMailMessage message) {

        try {
            mailSender.send(message.getTo(), "百战电商平台订单确认提醒", message.getOrderId().toString());
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

}
