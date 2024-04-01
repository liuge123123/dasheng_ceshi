package com.tcc.config;

import com.tcc.common.utils.Constant;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 
 * @date 2017/11/16
 * rabbit初始化配置
 */
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.virtual-host}")
    private String vhost;


    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * rabbitmq的配置
     * @return
     */
    public Map<String, Object> getConfig(){
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("port", port);
        result.put("host", host);
        result.put("virtualHost", vhost);
        return result;
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(){
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//        return rabbitTemplate;
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
       // factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(Constant.MQ.EXCAHNGE_DIRECT);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(Constant.MQ.EXCAHNGE_FANOUT);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(Constant.MQ.EXCAHNGE_TOPIC);
    }


}
