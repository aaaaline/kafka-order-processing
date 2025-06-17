package com.kafka_order_processing.orderskafka.dto;

import java.util.List;

public class OrderRequest {
    private String customerName;
    private List<OrderItemDTO> items;

    // getters e setters
}