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

        final String flightSymbol = json.getString("flightSymbol");
        final String airline = json.getString("airline");
        final String scheduledDeparture = json.getString("scheduledDeparture");
        final String departure = json.getString("departure");
        final String scheduledArrival = json.getString("scheduledArrival");
        final String arrival = json.getString("arrival");
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

    private static double calculateRelativeDelay(String departure,
                                                 String arrival,
                                                 double departureDelay,
                                                 double arrivalDelay)
            throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date departureDate = simpleDateFormat.parse(departure);
        final Date arrivalDate = simpleDateFormat.parse(arrival);

        final long diffInMillis = Math.abs(arrivalDate.getTime() - departureDate.getTime());
        final long diff = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);

        return (departureDelay + arrivalDelay) / (double) (diff);
    }
}
