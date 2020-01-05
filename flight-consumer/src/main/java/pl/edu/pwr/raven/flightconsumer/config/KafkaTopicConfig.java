package pl.edu.pwr.raven.flightconsumer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;

    @Value(value = "${topic.receive.name}")
    private String topicReceiveName;

    @Value(value = "${topic.send.name}")
    private String topicSendName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(config);
    }

    public NewTopic rawFlightDataTopic() {
        return new NewTopic(topicReceiveName, 1, (short) 1);
    }

    public NewTopic processedDataTopic() {
        return new NewTopic(topicSendName, 1, (short) 1);
    }
}
