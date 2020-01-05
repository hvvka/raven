package pl.edu.pwr.raven.flightconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;

@Service
public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${topic.send.name}")
    private String topicName;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Flight flight) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, flight.toString());
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Sent message=[{}] with offset=[{}]", flight, result.getRecordMetadata().offset());
                // TODO: Send to other topic for visualisation
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("Unable to send message=[{}]", flight.toString(), ex);
            }
        });
    }
}
