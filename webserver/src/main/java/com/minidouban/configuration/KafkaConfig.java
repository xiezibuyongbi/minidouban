package com.minidouban.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("minidouban", 8, (short) 2);
    }

    @Bean
    public NewTopic updateTopic() {
        return new NewTopic("minidouban", 10, (short) 2);
    }
}
