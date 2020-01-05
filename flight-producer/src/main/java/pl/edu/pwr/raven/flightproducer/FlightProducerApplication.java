package pl.edu.pwr.raven.flightproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
@SpringBootApplication
public class FlightProducerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FlightProducerApplication.class, args);

        MessageProducer producer = context.getBean(MessageProducer.class);
        producer.sendFlights();

        context.close();
    }
}
