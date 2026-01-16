package com.contract.management.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String CONTRACT_EXCHANGE = "contract.exchange";
    
    // 队列名称
    public static final String CONTRACT_CREATED_QUEUE = "contract.created.queue";
    public static final String CONTRACT_UPDATED_QUEUE = "contract.updated.queue";
    public static final String CONTRACT_DELETED_QUEUE = "contract.deleted.queue";
    public static final String CONTRACT_STATUS_CHANGED_QUEUE = "contract.status.changed.queue";
    
    // 路由键
    public static final String CONTRACT_CREATED_ROUTING_KEY = "contract.created";
    public static final String CONTRACT_UPDATED_ROUTING_KEY = "contract.updated";
    public static final String CONTRACT_DELETED_ROUTING_KEY = "contract.deleted";
    public static final String CONTRACT_STATUS_CHANGED_ROUTING_KEY = "contract.status.changed";

    /**
     * 消息转换器 - 使用JSON格式
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        // 开启发送确认
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("消息发送失败: " + cause);
            }
        });
        // 开启返回确认
        template.setReturnsCallback(returned -> {
            System.err.println("消息被退回: " + returned.getMessage());
        });
        return template;
    }

    /**
     * 监听器容器工厂配置
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    /**
     * 声明交换机
     */
    @Bean
    public TopicExchange contractExchange() {
        return new TopicExchange(CONTRACT_EXCHANGE, true, false);
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue contractCreatedQueue() {
        return QueueBuilder.durable(CONTRACT_CREATED_QUEUE).build();
    }

    @Bean
    public Queue contractUpdatedQueue() {
        return QueueBuilder.durable(CONTRACT_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue contractDeletedQueue() {
        return QueueBuilder.durable(CONTRACT_DELETED_QUEUE).build();
    }

    @Bean
    public Queue contractStatusChangedQueue() {
        return QueueBuilder.durable(CONTRACT_STATUS_CHANGED_QUEUE).build();
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding contractCreatedBinding() {
        return BindingBuilder
                .bind(contractCreatedQueue())
                .to(contractExchange())
                .with(CONTRACT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding contractUpdatedBinding() {
        return BindingBuilder
                .bind(contractUpdatedQueue())
                .to(contractExchange())
                .with(CONTRACT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding contractDeletedBinding() {
        return BindingBuilder
                .bind(contractDeletedQueue())
                .to(contractExchange())
                .with(CONTRACT_DELETED_ROUTING_KEY);
    }

    @Bean
    public Binding contractStatusChangedBinding() {
        return BindingBuilder
                .bind(contractStatusChangedQueue())
                .to(contractExchange())
                .with(CONTRACT_STATUS_CHANGED_ROUTING_KEY);
    }
}