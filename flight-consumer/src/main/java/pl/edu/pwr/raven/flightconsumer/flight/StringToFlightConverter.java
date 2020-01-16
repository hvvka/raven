package pl.edu.pwr.raven.flightconsumer.flight;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StringToFlightConverter {

    private StringToFlightConverter() {
    }

    public static Flight convertToFlight(String input) throws ParseException {
        JSONObject json = new JSONObject(input);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final String flightSymbol = json.getString("flightSymbol");
        final String airline = json.getString("airline");
        final Date scheduledDeparture = format.parse(json.getString("scheduledDeparture"));
        final Date departure = format.parse(json.getString("departure"));
        final Date scheduledArrival = format.parse(json.getString("scheduledArrival"));
        final Date arrival = format.parse(json.getString("arrival"));
        final String flightStatus = json.getString("flightStatus");
        final String airportFrom = json.getString("airportFrom");
        final String airportTo = json.getString("airportTo");
        final double departureDelay = json.getDouble("departureDelay");
        final double arrivalDelay = json.getDouble("arrivalDelay");
        final double relativeDelay = calculateRelativeDelay(departure, arrival, departureDelay, arrivalDelay);
        final int flightNumber = json.getInt("flightNumber");
        final String airlineCode = json.getString("airlineCode");

        return new FlightBuilder()
                .setFlightSymbol(flightSymbol)
                .setAirline(airline)
                .setScheduledDeparture(scheduledDeparture)
                .setDeparture(departure)
                .setScheduledArrival(scheduledArrival)
                .setArrival(arrival).setFlightStatus(flightStatus)
                .setAirportFrom(airportFrom).setAirportTo(airportTo)
                .setDepartureDelay(departureDelay).setArrivalDelay(arrivalDelay)
                .setRelativeDelay(relativeDelay)
                .setFlightNumber(flightNumber)
                .setAirlineCode(airlineCode)
                .createFlight();
    }

    private static double calculateRelativeDelay(Date departure,
                                                 Date arrival,
                                                 double departureDelay,
                                                 double arrivalDelay) {
        long diffInMillis = Math.abs(arrival.getTime() - departure.getTime());
        long diff = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);

        return (departureDelay + arrivalDelay) / (double) (diff);
    }
}
