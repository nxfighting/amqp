package com.vict.consumer;
import com.vict.producer.domain.BookOrder;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author
 * @create 2018-12-04 16:19
 */
@Component
public class MessageReceiver {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(
        bindings = @QueueBinding(
                value=@Queue(value="spring.queue",durable = "true"),
                exchange = @Exchange(name="spring.exchange",durable = "true",type = "direct"),
                key = "spring.queue"
        )
    )
    @RabbitHandler
    public void rec(@Payload BookOrder bookOrder,
                    @Headers Map<String,Object> headers,
                    Channel channel){
        System.out.println("消费开始.............");
       System.out.println("消息："+bookOrder.getOrderName());
       Long delivertTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            channel.basicAck(delivertTag,false);
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

        }
    }
}
