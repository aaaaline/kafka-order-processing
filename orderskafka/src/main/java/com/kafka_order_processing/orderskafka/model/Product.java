package com.kafka_order_processing.orderskafka.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer quantity; // Current stock
}