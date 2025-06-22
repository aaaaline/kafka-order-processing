package com.kafka_order_processing.inventoryservice.kafka;

import com.kafka_order_processing.inventoryservice.model.Order;
import com.kafka_order_processing.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final InventoryService inventoryService;

    public OrderConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "orders", groupId = "inventory_group")
    public void consumeOrder(Order order) {
        // O consumer delega a chamada e o restante da lógica é tratada no InventoryService
        inventoryService.processOrderInventory(order);
    }
}