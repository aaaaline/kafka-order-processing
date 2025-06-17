package com.kafka_order_processing.orderskafka.kafka;

import com.kafka_order_processing.orderskafka.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    private static final String TOPIC = "orders";

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(Order order) {
        kafkaTemplate.send(TOPIC, order.getOrderId(), order);
    }
}

