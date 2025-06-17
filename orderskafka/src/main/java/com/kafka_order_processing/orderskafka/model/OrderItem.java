package com.kafka_order_processing.orderskafka.model;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
}