package com.tcc.common.utils;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 阿里云工具类
 */
@Component
public class RabbitMqUtils {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 点对点
     *
     * @param routingKey
     * @param msg
     */
    public void sendMsg(String routingKey, Object msg) {
        String exchange = Constant.MQ.EXCAHNGE_DIRECT;
        DirectExchange directExchange = new DirectExchange(exchange);
        Queue queue = new Queue(routingKey);
        Binding bindingBuilder = BindingBuilder.bind(queue).to(directExchange).with(routingKey);
        rabbitAdmin.declareExchange(directExchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(bindingBuilder);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }

    /**
     * Fanout模式（广播）
     *
     * @param routingKey
     * @param msg
     */
    public void sendFanoutMsg(String routingKey, Object msg) {
        String exchange = Constant.MQ.EXCAHNGE_FANOUT;
        FanoutExchange fanoutExchange = new FanoutExchange(exchange);
        rabbitAdmin.declareExchange(fanoutExchange);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }

    /**
     * topic模式（订阅）
     *
     * @param routingKey
     * @param msg
     */
    public void sendTopicMsg(String routingKey, Object msg) {
        String exchange = Constant.MQ.EXCAHNGE_TOPIC;
        TopicExchange topicExchange = new TopicExchange(exchange);
        rabbitAdmin.declareExchange(topicExchange);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }

}
