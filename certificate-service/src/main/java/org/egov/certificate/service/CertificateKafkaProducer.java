package org.egov.certificate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CertificateKafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object message) {
        log.info("Publishing message to topic: {}", topic);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
            
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to publish message to topic: {} - Error: {}", 
                            topic, throwable.getMessage(), throwable);
                } else {
                    log.info("Successfully published message to topic: {} with offset: {}", 
                            topic, result.getRecordMetadata().offset());
                }
            });
            
        } catch (Exception e) {
            log.error("Exception occurred while publishing message to topic: {} - Error: {}", 
                    topic, e.getMessage(), e);
            throw e;
        }
    }
}