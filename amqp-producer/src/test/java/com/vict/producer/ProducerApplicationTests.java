package com.vict.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerApplicationTests {
    @Autowired
    private MessagePusher messagePusher;
    @Test
    public void contextLoads() {
        messagePusher.send();
    }

}
