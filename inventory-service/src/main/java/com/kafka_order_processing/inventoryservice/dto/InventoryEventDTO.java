package com.kafka_order_processing.inventoryservice.dto;

import java.util.UUID;

public class InventoryEventDTO {
    public enum InventoryStatus {
        SUCCESS,
        FAILURE_OUT_OF_STOCK
    }

    private UUID orderId;
    private InventoryStatus status;
    private String message;

    public InventoryEventDTO(String orderId, InventoryStatus success, String message) {}

    public InventoryEventDTO(UUID orderId, InventoryStatus status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}