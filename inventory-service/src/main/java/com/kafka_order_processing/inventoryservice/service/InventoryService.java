package com.kafka_order_processing.inventoryservice.service;

import com.kafka_order_processing.inventoryservice.dto.InventoryEventDTO;
import com.kafka_order_processing.inventoryservice.kafka.InventoryEventProducer;
import com.kafka_order_processing.orderskafka.model.Order;
import com.kafka_order_processing.orderskafka.model.OrderItem;
import com.kafka_order_processing.orderskafka.model.Product;
import com.kafka_order_processing.orderskafka.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryEventProducer inventoryEventProducer;

    public InventoryService(ProductRepository productRepository, InventoryEventProducer inventoryEventProducer) {
        this.productRepository = productRepository;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    @Transactional
    public void processOrderInventory(Order order) {
        try {
            for (OrderItem item : order.getItems()) {
                Optional<Product> productOpt = productRepository.findById(item.getProductId());

                if (productOpt.isEmpty()) {
                    throw new IllegalStateException("Produto com ID " + item.getProductId() + " não encontrado.");
                }

                Product product = productOpt.get();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Estoque insuficiente para o produto: " + product.getName());
                }

                // Deduz a quantidade do estoque
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            }

            // Se tudo correu bem, publica um evento de sucesso
            InventoryEventDTO successEvent = new InventoryEventDTO(order.getOrderId(), InventoryEventDTO.InventoryStatus.SUCCESS, "Estoque reservado com sucesso.");
            inventoryEventProducer.sendInventoryEvent(successEvent);

        } catch (IllegalStateException e) {
            // Se falhou (e.g., sem estoque), publica um evento de falha
            System.err.println("Falha ao processar o inventário para o pedido " + order.getOrderId() + ": " + e.getMessage());
            InventoryEventDTO failureEvent = new InventoryEventDTO(order.getOrderId(), InventoryEventDTO.InventoryStatus.FAILURE_OUT_OF_STOCK, e.getMessage());
            inventoryEventProducer.sendInventoryEvent(failureEvent);
            // A anotação @Transactional garantirá o rollback das alterações no estoque.
        }
    }
}