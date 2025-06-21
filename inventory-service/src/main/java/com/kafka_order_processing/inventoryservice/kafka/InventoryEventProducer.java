package com.kafka_order_processing.inventoryservice.kafka;

import com.kafka_order_processing.inventoryservice.dto.InventoryEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "inventory-events";

    public InventoryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendInventoryEvent(InventoryEventDTO event) {
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
        System.out.println("✅ Evento de inventário enviado para o pedido: " + event.getOrderId() + " com status: " + event.getStatus());
    }
}