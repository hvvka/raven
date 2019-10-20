package pl.edu.pwr.raven.flightconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(topics = "${topic.name}", groupId = "flight", containerFactory = "flightKafkaListenerContainerFactory")
    public void flightListener(String flight) {
        LOG.info("Received message: [{}]", flight);
    }
}
