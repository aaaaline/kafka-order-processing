package com.kafka_order_processing.orderskafka.model;

import java.util.List;

public class Order {
    private String orderId;
    private String customerName;
    private long timestamp;
    private List<OrderItem> items;

    // getters e setters

    public static class OrderItem {
        private String productId;
        private int quantity;

        // getters e setters
    }
}