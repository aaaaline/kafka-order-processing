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
    public ResponseEntity<Order> registerOrder(@RequestBody OrderRequest request) {
        Order createdOrder = service.processNewOrder(request);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Serviço de pedidos operacional.");
    }
}