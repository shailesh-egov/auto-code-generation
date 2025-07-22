package org.egov.producer;

import lombok.extern.slf4j.Slf4j;
import org.egov.web.models.BankAccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BankAccountProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public BankAccountProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Pushes bank account request to Kafka topic
     *
     * @param topic   Kafka topic name
     * @param request Bank account request to be published
     */
    public void push(String topic, BankAccountRequest request) {
        log.info("Publishing bank account request to topic: {}", topic);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, request);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published bank account request to topic: {} with offset: {}",
                            topic, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish bank account request to topic: {}", topic, ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error while publishing to topic: {}", topic, e);
            throw new RuntimeException("Failed to publish message to Kafka topic", e);
        }
    }

    /**
     * Pushes any generic object to Kafka topic
     *
     * @param topic Kafka topic name
     * @param value Object to be published
     */
    public void push(String topic, Object value) {
        log.info("Publishing message to topic: {}", topic);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, value);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published message to topic: {} with offset: {}",
                            topic, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish message to topic: {}", topic, ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error while publishing to topic: {}", topic, e);
            throw new RuntimeException("Failed to publish message to Kafka topic", e);
        }
    }

    /**
     * Pushes message with a specific key to Kafka topic
     *
     * @param topic Kafka topic name
     * @param key   Message key
     * @param value Message value
     */
    public void push(String topic, String key, Object value) {
        log.info("Publishing message with key: {} to topic: {}", key, topic);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, value);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published message with key: {} to topic: {} with offset: {}",
                            key, topic, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish message with key: {} to topic: {}", key, topic, ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error while publishing message with key: {} to topic: {}", key, topic, e);
            throw new RuntimeException("Failed to publish message to Kafka topic", e);
        }
    }
}