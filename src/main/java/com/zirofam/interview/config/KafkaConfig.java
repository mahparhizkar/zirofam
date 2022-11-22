package com.zirofam.interview.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.zirofam.interview.config.KafkaConstants.KAFKA_UPDATE_WALLET_TOPIC;

@Configuration
public class KafkaConfig {

    @Value("${kafka.partition.count}")
    private int partitionCount;

    @Bean
    public NewTopic topicExample() {
        return TopicBuilder.name(KAFKA_UPDATE_WALLET_TOPIC)
                .partitions(partitionCount)
                .build();
    }
}
