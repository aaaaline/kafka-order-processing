package com.kafka_order_processing.orderskafka.service;

import com.kafka_order_processing.orderskafka.dto.OrderItemDTO;
import com.kafka_order_processing.orderskafka.dto.OrderRequest;
import com.kafka_order_processing.orderskafka.kafka.OrderProducer;
import com.kafka_order_processing.orderskafka.model.Order;
import com.kafka_order_processing.orderskafka.model.Order.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderProducer producer;

    public OrderService(OrderProducer producer) {
        this.producer = producer;
    }

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerName(request.getCustomerName());
        order.setTimestamp(System.currentTimeMillis());

        List<OrderItem> items = request.getItems().stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setProductId(dto.getProductId());
            item.setQuantity(dto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        producer.sendOrder(order);
        return order;
    }
}
