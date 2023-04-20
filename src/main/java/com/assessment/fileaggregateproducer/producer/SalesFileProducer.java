package com.assessment.fileaggregateproducer.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SalesFileProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SalesFileProducer.class);
    private KafkaProducer<String, String> kafkaProducer;
    @Value("${kafka.topics.file-aggregation}")
    private String kafkaTopic;

    @Autowired
    SalesFileProducer(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendSalesData(String salesData) throws IOException {
        LOG.info("Sending file data to topic {}", kafkaTopic);
        try {
            kafkaProducer.send(new ProducerRecord<>(kafkaTopic, salesData));
        } catch (InvalidTopicException e) {
            throw new IOException(e);
        }
    }
}
