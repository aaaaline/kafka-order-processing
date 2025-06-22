package com.kafka_order_processing.orderskafka.repository;

import com.kafka_order_processing.orderskafka.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}