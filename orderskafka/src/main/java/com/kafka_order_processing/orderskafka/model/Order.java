package com.kafka_order_processing.orderskafka.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    private String orderId;
    private String customerName;
    private long timestamp;
    private List<OrderItem> items;

    public Order() {
        this.orderId = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toEpochMilli();
        this.items = new ArrayList<>();
    }

    public Order(String customerName, List<OrderItem> items) {
        this();
        this.customerName = customerName;
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", timestamp=" + timestamp +
                ", items=" + items +
                '}';
    }
}
