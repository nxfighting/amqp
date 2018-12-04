package com.vict.producer;

import com.vict.producer.domain.BookOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 消息推送
 *
 * @author
 * @create 2018-12-04 15:11
 */
@Component
public class MessagePusher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void send(){

        for (int i = 0; i < 10000; i++) {
            BookOrder order = new BookOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderName(String.format("第[%s]个订单消息",i+1));
            rabbitTemplate.convertAndSend(order);
            System.out.println("推送"+i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
