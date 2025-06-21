package com.kafka_order_processing.orderskafka.controller;

import com.kafka_order_processing.orderskafka.model.Product;
import com.kafka_order_processing.orderskafka.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Endpoint para listar todos os produtos disponíveis.
     * Mapeia para requisições GET para /products.
     *
     * @return Uma ResponseEntity contendo uma lista de objetos Product se encontrada,
     * ou uma resposta vazia com status HTTP apropriado.
     */
    @GetMapping // Mapeia este método para requisições GET para /products
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products); 
    }
}