package com.kafka_order_processing.notificationservice.kafka;

import com.kafka_order_processing.notificationservice.dto.InventoryEventDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class NotificationConsumer {

    private final AtomicReference<InventoryEventDTO> latestEvent = new AtomicReference<>();

    @KafkaListener(topics = "inventory-events", groupId = "notification_group")
    public void consumeInventoryEvent(InventoryEventDTO event) {
        System.out.println("📬 Notificação Recebida:");
        System.out.println("==============================================");
        System.out.println("  Pedido ID: " + event.orderId());
        System.out.println("  Cliente: " + event.customerName());
        System.out.println("  Status: " + event.status());

        if (event.status() == InventoryEventDTO.Status.SUCCESS) {
            System.out.println("  Email/SMS Simulado: 'Olá " + event.customerName() + ", seu pedido " + event.orderId() + " foi confirmado e está sendo preparado!'");
        } else {
            System.out.println("  Email/SMS Simulado: 'Olá " + event.customerName() + ", infelizmente houve um problema com seu pedido " + event.orderId() + ". Motivo: " + event.message() + "'");
        }
        System.out.println("==============================================");

        latestEvent.set(event);
    }

    public InventoryEventDTO getLatestEvent() {
        return latestEvent.get();
    }
}