package com.kafka_order_processing.orderskafka.controller;

import com.kafka_order_processing.orderskafka.dto.OrderRequest;
import com.kafka_order_processing.orderskafka.model.Order;
import com.kafka_order_processing.orderskafka.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = service.createOrder(request);
        return ResponseEntity.ok(order);
    }
}
