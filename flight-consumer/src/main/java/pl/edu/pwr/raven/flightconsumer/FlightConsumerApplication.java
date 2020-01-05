package pl.edu.pwr.raven.flightconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;

@SpringBootApplication
public class FlightConsumerApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(FlightConsumerApplication.class);

    @Autowired
    private FlightRepository flightRepository;

    private MessageSender messageSender = new MessageSender();

    public static void main(String[] args) {
        SpringApplication.run(FlightConsumerApplication.class, args);
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener(flightRepository, messageSender);
    }

    @Override
    public void run(String... args) {
    }
}
