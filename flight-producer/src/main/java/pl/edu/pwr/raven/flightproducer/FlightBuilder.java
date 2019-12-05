package pl.edu.pwr.raven.flightproducer;

import pl.edu.pwr.raven.flightproducer.avro.Flight;
import pl.edu.pwr.raven.flightproducer.avro.FlightStatus;
import pl.edu.pwr.raven.flightproducer.avro.FlightType;

import java.util.Optional;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public class FlightBuilder {

    private FlightBuilder() {
    }

    public static Optional<Flight> createFlight(String message) {
        Flight.Builder builder = Flight.newBuilder();
        int expectedArraySize = 20;
        String[] splitMessage = message.replace("\"", "").split(",");
        if (splitMessage.length != expectedArraySize) {
            return Optional.empty();
        } else {
            return Optional.of(
                    builder.setFlightSymbol(splitMessage[0])
                            .setAirline(splitMessage[1])
                            .setFlightType(FlightType.valueOf(splitMessage[2]))
                            .setScheduledDeparture(splitMessage[3])
                            .setDeparture(splitMessage[4])
                            .setScheduledArrival(splitMessage[5])
                            .setArrival(splitMessage[6])
                            .setFlightStatus(FlightStatus.valueOf(splitMessage[7]))
                            .setJustification(splitMessage[8])
                            .setAirportFrom(splitMessage[9])
                            .setAirportTo(splitMessage[10])
                            .setLongitudeTo(Double.parseDouble(splitMessage[11]))
                            .setLatitudeTo(Double.parseDouble(splitMessage[12]))
                            .setLongitudeFrom(Double.parseDouble(splitMessage[13]))
                            .setLatitudeFrom(Double.parseDouble(splitMessage[14]))
                            .setDepartureDelay(Integer.parseInt(splitMessage[15]))
                            .setArrivalDelay(Integer.parseInt(splitMessage[16]))
                            .setDistanceInMeters(Double.parseDouble(splitMessage[17]))
                            .setFlightNumber(Integer.parseInt(splitMessage[18]))
                            .setAirlineCode(splitMessage[19])
                            .build());
        }
    }
}
