package pl.edu.pwr.raven.flightconsumer.flight;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public interface FlightRepository extends MongoRepository<Flight, String> {

    List<Flight> findByFlightSymbol(String flightSymbol);
}
