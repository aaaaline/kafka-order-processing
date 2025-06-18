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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerName(request.getCustomerName());
        order.setTimestamp(System.currentTimeMillis());

        List<OrderItem> itemList = request.getItems().stream()
                .map(this::createOrderItemFromDto)
                .collect(Collectors.toList());
        order.setItems(itemList);

        Order savedOrder = orderRepository.save(order);

        producer.sendOrder(savedOrder);
        return savedOrder;
    }

    private OrderItem createOrderItemFromDto(OrderItemDTO dto) {
        UUID productId = UUID.fromString(dto.getProductId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + dto.getProductId()));

        OrderItem item = new OrderItem();
        item.setProductId(dto.getProductId());
        item.setProductName(product.getName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(product.getPrice());
        return item;
    }
}