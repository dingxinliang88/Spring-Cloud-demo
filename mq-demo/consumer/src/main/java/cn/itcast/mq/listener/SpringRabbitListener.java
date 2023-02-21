package cn.itcast.mq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @author codejuzi
 */
@Component
public class SpringRabbitListener {

    // region WorkQueue

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1接收到消息：【" + msg + "】" + LocalTime.now());
        Thread.sleep(20);
    }

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2........接收到消息：【" + msg + "】" + LocalTime.now());
        Thread.sleep(200);
    }

    // endregion

    // region fanout exchange
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutExchange1(String msg) {
        System.out.println("消费者1........接收到消息：【" + msg + "】");
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutExchange2(String msg){
        System.err.println("消费者2........接收到消息：【" + msg + "】");
    }

    // endregion

    // region derect exchange

    @RabbitListener(bindings = @QueueBinding(
                value =  @Queue("direct.queue1"),
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}
            ))
    public void listenDirectExchange1(String msg){
        System.out.println("消费者接收到direct.queue1的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value =  @Queue("direct.queue2"),
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"}
    ))
    public void listenDirectExchange2(String msg){
        System.out.println("消费者接收到direct.queue2的消息：【" + msg + "】");
    }

    // endregion

    // region topic exchange

    @RabbitListener(bindings = @QueueBinding(
            value =  @Queue("topic.queue1"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = "china.#"
    ))
    public void listenTopicQueue1(String msg){
        System.out.println("消费者接收到topic.queue1(china.#)的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value =  @Queue("topic.queue2"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = "#.news"
    ))
    public void listenTopicQueue2(String msg){
        System.out.println("消费者接收到topic.queue2(#.news)的消息：【" + msg + "】");
    }

    // endregion

}
