package pl.edu.pwr.raven.flightconsumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageSender {
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        String bootstrapAddress = "localhost:9092";
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);
    private final KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());

    public void send(Flight flight) {
        String topicName = "processed-data";
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, flight.toString());
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Sent message=[{}] with offset=[{}]", flight, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("Unable to send message=[{}]", flight.toString(), ex);
            }
        });
    }
}
