package pl.edu.pwr.raven.flightconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;
import pl.edu.pwr.raven.flightconsumer.flight.StringToFlightConverter;

import java.text.ParseException;

public class MessageListener {

    public MessageListener(FlightRepository flightRepository, MessageSender sender) {
        this.flightRepository = flightRepository;
        this.sender = sender;
    }

    private FlightRepository flightRepository;
    private MessageSender sender;
    private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(topics = "${topic.name}", groupId = "flight", containerFactory = "flightKafkaListenerContainerFactory")
    public void flightListener(String input) {
        LOG.info("Received message: [{}]", input);

        try {
            final Flight flight = StringToFlightConverter.convertToFlight(input);
            LOG.info("Converted message: {}", flight.toString());

            LOG.info("Saving to database");
            flightRepository.save(flight);

            LOG.info("Sending processed data to topic");
            sender.send(flight);
        }
        catch (ParseException ex)
        {
            LOG.error("Could not parse input to Flight structure");
            ex.printStackTrace();
        }
    }
}
