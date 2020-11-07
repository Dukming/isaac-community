package com.dkm.isaaccommunity.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {

    final static String questionQueue = "queue_index_question";
    final static String answerQueue = "queue_index_answer";


    //声明问题队列
    @Bean
    public Queue queueQuestion() {
        return new Queue(TopicRabbitConfig.questionQueue);
    }


    //声明回答队列
    @Bean
    public Queue queueAnswer() {
        return new Queue(TopicRabbitConfig.answerQueue);
    }


    // 声明一个Topic类型的交换机
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("exchange_topic_index");
    }

    //绑定问题队列到交换机,并且指定routingKey
    @Bean
    Binding bindingExchangeMessage(Queue queueQuestion, TopicExchange exchange) {
        return BindingBuilder.bind(queueQuestion).to(exchange).with("index.question.#");
    }

    //绑定回答队列到交换机,并且指定routingKey
    @Bean
    Binding bindingExchangeMessages(Queue queueAnswer, TopicExchange exchange) {
        return BindingBuilder.bind(queueAnswer).to(exchange).with("index.answer.#");
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
//        return template;
//    }
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//        return factory;
//    }

}
