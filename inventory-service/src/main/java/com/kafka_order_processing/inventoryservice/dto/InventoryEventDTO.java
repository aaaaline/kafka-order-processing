package com.kafka_order_processing.inventoryservice.dto;

import java.util.UUID;

public record InventoryEventDTO(
        UUID orderId,
        String customerName,
        Status status,
        String message
) {
    public enum Status {
        SUCCESS,
        FAILURE
    }
}