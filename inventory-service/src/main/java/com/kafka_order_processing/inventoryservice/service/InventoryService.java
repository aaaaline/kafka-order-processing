package com.kafka_order_processing.inventoryservice.service;

import com.kafka_order_processing.inventoryservice.dto.InventoryEventDTO;
import com.kafka_order_processing.inventoryservice.kafka.InventoryEventProducer;
import com.kafka_order_processing.inventoryservice.model.Order;
import com.kafka_order_processing.inventoryservice.model.OrderItem;
import com.kafka_order_processing.inventoryservice.model.Product;
import com.kafka_order_processing.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
        System.out.println("📦 Pedido recebido para processar inventário: " + order.getOrderId());
        try {
            if (order.getItems() == null || order.getItems().isEmpty()) {
                throw new IllegalArgumentException("O pedido não contém itens.");
            }

            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new IllegalStateException("Produto " + item.getProductId() + " não encontrado."));

                if (product.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Estoque insuficiente para o produto: " + product.getName());
                }

                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            }

            InventoryEventDTO successEvent = new InventoryEventDTO(
                    order.getOrderId(),
                    order.getCustomerName(),
                    InventoryEventDTO.Status.SUCCESS,
                    "Pedido processado e estoque reservado com sucesso."
            );
            inventoryEventProducer.sendInventoryEvent(successEvent);

        } catch (Exception e) { // Captura qualquer erro
            System.err.println("!!! ERRO INESPERADO ao processar o pedido " + order.getOrderId() + " !!!");
            e.printStackTrace(); // Imprime o erro completo no log

            InventoryEventDTO failureEvent = new InventoryEventDTO(
                    order.getOrderId(),
                    order.getCustomerName(),
                    InventoryEventDTO.Status.FAILURE,
                    "Erro inesperado no processamento: " + e.getMessage()
            );
            inventoryEventProducer.sendInventoryEvent(failureEvent);
        }
    }
}