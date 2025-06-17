package com.kafka_order_processing.orderskafka.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productId;
    private Integer quantity;
    private Double price;
}