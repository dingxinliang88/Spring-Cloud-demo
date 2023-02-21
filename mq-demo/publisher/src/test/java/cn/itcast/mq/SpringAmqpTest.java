package cn.itcast.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author codejuzi
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringAmqpTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleQueue() {
        // 队列名称
        String queueName = "simple.queue";
        // 消息
        String message = "hello, spring amqp!";
        // 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }

    @Test
    public void testWorkQueue() {
        // 队列名称
        String queueName = "simple.queue";
        // 消息
        String message = "hello, message__";

        for (int i = 0; i < 50; i++) {
            // 发送消息
            rabbitTemplate.convertAndSend(queueName, message + (i + 1));
        }
    }

    @Test
    public void testFanoutExchange() {
        // 队列名称
        String exchangeName = "itcast.fanout";
        // 消息
        String message = "hello, every one, this is codejuzi";

        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }

    @Test
    public void testDirectExchange() {
        // 队列名称
        String exchangeName = "itcast.direct";
        // 消息
        String message = "hello, red";

        rabbitTemplate.convertAndSend(exchangeName, "red", message);
    }

    @Test
    public void testSendTopicExchange() {
        // 交换机名称
        String exchangeName = "itcast.topic";
        // 消息
        String message = "喜报！孙悟空大战哥斯拉，胜!";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
    }

    @Test
    public void testSendMap() throws InterruptedException {
        // 准备消息
        Map<String,Object> msg = new HashMap<>();
        msg.put("name", "Jack");
        msg.put("age", 21);
        // 发送消息
        rabbitTemplate.convertAndSend("object.queue","", msg);
    }

}
