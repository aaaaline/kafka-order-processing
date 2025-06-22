package com.kafka_order_processing.orderskafka.service;

import com.kafka_order_processing.orderskafka.dto.OrderItemDTO;
import com.kafka_order_processing.orderskafka.dto.OrderRequest;
import com.kafka_order_processing.orderskafka.kafka.OrderProducer;
import com.kafka_order_processing.orderskafka.model.Order;
import com.kafka_order_processing.orderskafka.model.OrderItem;
import com.kafka_order_processing.orderskafka.model.Product;
import com.kafka_order_processing.orderskafka.repository.ProductRepository;
import com.kafka_order_processing.orderskafka.repository.OrderRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderProducer producer;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(OrderProducer producer, ProductRepository productRepository, OrderRepository orderRepository) {
        this.producer = producer;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order processNewOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());

        request.getItems().stream()
                .map(dto -> createOrderItemFromDto(dto, order))
                .forEach(order::addItem);

        Order savedOrder = orderRepository.save(order);

        producer.sendOrder(savedOrder);
        return savedOrder;
    }

    private OrderItem createOrderItemFromDto(OrderItemDTO dto, Order order) {
        UUID productIdAsUUID = UUID.fromString(dto.getProductId());
        Product product = productRepository.findById(productIdAsUUID)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + dto.getProductId()));

        OrderItem item = new OrderItem();
        item.setProductId(productIdAsUUID);
        item.setProductName(product.getName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(product.getPrice());
        item.setOrder(order);
        return item;
    }
}