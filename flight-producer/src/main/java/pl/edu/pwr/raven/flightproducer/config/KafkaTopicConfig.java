package pl.edu.pwr.raven.flightproducer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;

    @Value(value = "${topic.name}")
    private String topicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic rawFlightDataTopic() {
        return new NewTopic(topicName, 1, (short) 1);
    }
}
