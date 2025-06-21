package com.kafka_order_processing.inventoryservice.kafka;

import com.kafka_order_processing.inventoryservice.service.InventoryService;
import com.kafka_order_processing.orderskafka.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "orders", groupId = "inventory-group")
    public void consumeOrder(Order order) {
        System.out.println("🧾 Pedido recebido para processamento de inventário: " + order.getOrderId());
        inventoryService.processOrderInventory(order);
    }
}