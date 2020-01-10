package pl.edu.pwr.raven.flightconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;
import pl.edu.pwr.raven.flightconsumer.flight.StringToFlightConverter;

import java.text.ParseException;

@Service
public class MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);

    private FlightRepository flightRepository;

    private MessageProducer messageProducer;

    public MessageListener(FlightRepository flightRepository, MessageProducer messageProducer) {
        this.flightRepository = flightRepository;
        this.messageProducer = messageProducer;
    }

    @KafkaListener(topics = "${topic.receive.name}", groupId = "flight", containerFactory = "flightKafkaListenerContainerFactory")
    public void flightListener(String input) {
        LOG.info("Received message: [{}]", input);

        try {
            Flight flight = StringToFlightConverter.convertToFlight(input);
            LOG.info("Converted message: {}", flight);

            LOG.info("Saving to database");
            flightRepository.save(flight);

            LOG.info("Sending processed data to topic");
            messageProducer.send(flight);
        } catch (ParseException ex) {
            LOG.error("Could not parse input to Flight structure", ex);
        }
    }
}
