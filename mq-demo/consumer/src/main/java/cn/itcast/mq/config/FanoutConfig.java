package cn.itcast.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author codejuzi
 */
@Configuration
public class FanoutConfig {

    /**
     * 声明Fanout交换机
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("itcast.fanout");
    }

    /**
     * 声明队列1
     */
    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout.queue1");
    }

    /**
     * 绑定queue1到exchange
     */
    @Bean
    public Binding bindingQueue1(Queue fanoutQueue1, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    /**
     * 声明队列2
     */
    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout.queue2");
    }

    /**
     * 绑定queue2到exchange
     */
    @Bean
    public Binding bindingQueue2(Queue fanoutQueue2, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }


    @Bean
    public Queue objectQueue() {
        return new Queue("object.queue");
    }
}
