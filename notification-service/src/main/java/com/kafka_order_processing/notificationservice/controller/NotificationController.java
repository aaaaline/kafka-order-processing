package com.kafka_order_processing.notificationservice.controller;

import com.kafka_order_processing.notificationservice.dto.InventoryEventDTO;
import com.kafka_order_processing.notificationservice.kafka.NotificationConsumer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // NOVO IMPORT
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
// Permite requisições do seu frontend. Ajuste a porta se o seu frontend rodar em outra.
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationConsumer notificationConsumer;

    public NotificationController(NotificationConsumer notificationConsumer) {
        this.notificationConsumer = notificationConsumer;
    }

    /**
     * Retorna a última notificação de evento de inventário processada.
     * O frontend pode fazer polling deste endpoint para obter atualizações.
     * @return ResponseEntity contendo a última notificação ou 204 No Content se nenhuma.
     */
    @GetMapping("/latest")
    public ResponseEntity<InventoryEventDTO> getLatestNotification() {
        InventoryEventDTO latestEvent = notificationConsumer.getLatestEvent();
        if (latestEvent != null) {
            return ResponseEntity.ok(latestEvent);
        } else {
            return ResponseEntity.noContent().build(); // Retorna 204 se não houver notificação
        }
    }
}