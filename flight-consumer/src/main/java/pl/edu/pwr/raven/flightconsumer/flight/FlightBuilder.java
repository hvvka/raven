package pl.edu.pwr.raven.flightconsumer.flight;

public class FlightBuilder {

    private String flightSymbol;
    private String airline;
    private String scheduledDeparture;
    private String departure;
    private String scheduledArrival;
    private String arrival;
    private String flightStatus;
    private String airportFrom;
    private String airportTo;
    private double departureDelay;
    private double arrivalDelay;
    private double relativeDelay;
    private int flightNumber;
    private String airlineCode;

    public FlightBuilder setFlightSymbol(String flightSymbol) {
        this.flightSymbol = flightSymbol;
        return this;
    }

    public FlightBuilder setAirline(String airline) {
        this.airline = airline;
        return this;
    }

    public FlightBuilder setScheduledDeparture(String scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
        return this;
    }

    public FlightBuilder setDeparture(String departure) {
        this.departure = departure;
        return this;
    }

    public FlightBuilder setScheduledArrival(String scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
        return this;
    }

    public FlightBuilder setArrival(String arrival) {
        this.arrival = arrival;
        return this;
    }

    public FlightBuilder setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
        return this;
    }

    public FlightBuilder setAirportFrom(String airportFrom) {
        this.airportFrom = airportFrom;
        return this;
    }

    public FlightBuilder setAirportTo(String airportTo) {
        this.airportTo = airportTo;
        return this;
    }

    public FlightBuilder setDepartureDelay(double departureDelay) {
        this.departureDelay = departureDelay;
        return this;
    }

    public FlightBuilder setArrivalDelay(double arrivalDelay) {
        this.arrivalDelay = arrivalDelay;
        return this;
    }

    public FlightBuilder setRelativeDelay(double relativeDelay) {
        this.relativeDelay = relativeDelay;
        return this;
    }

    public FlightBuilder setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
        return this;
    }

    public FlightBuilder setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
        return this;
    }

    public Flight createFlight() {
        return new Flight(flightSymbol,
                airline,
                scheduledDeparture,
                departure,
                scheduledArrival,
                arrival,
                flightStatus,
                airportFrom,
                airportTo,
                departureDelay,
                arrivalDelay,
                relativeDelay,
                flightNumber,
                airlineCode);
    }
}