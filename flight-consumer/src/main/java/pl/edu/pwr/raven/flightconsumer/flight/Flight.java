package pl.edu.pwr.raven.flightconsumer.flight;

import org.springframework.data.annotation.Id;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public class Flight {

    @Id
    public String id;

    public String flightSymbol;
    public String airline;
    public String scheduledDeparture;
    public String departure;
    public String scheduledArrival;
    public String arrival;
    public String flightStatus;
    public String airportFrom;
    public String airportTo;
    public double departureDelay;
    public double arrivalDelay;
    public double relativeDelay;
    public int flightNumber;
    public String airlineCode;

    public Flight(String flightSymbol, String airline, String scheduledDeparture, String departure, String scheduledArrival, String arrival, String flightStatus, String airportFrom, String airportTo, double departureDelay, double arrivalDelay, double relativeDelay, int flightNumber, String airlineCode) {
        this.flightSymbol = flightSymbol;
        this.airline = airline;
        this.scheduledDeparture = scheduledDeparture;
        this.departure = departure;
        this.scheduledArrival = scheduledArrival;
        this.arrival = arrival;
        this.flightStatus = flightStatus;
        this.airportFrom = airportFrom;
        this.airportTo = airportTo;
        this.departureDelay = departureDelay;
        this.arrivalDelay = arrivalDelay;
        this.relativeDelay = relativeDelay;
        this.flightNumber = flightNumber;
        this.airlineCode = airlineCode;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", flightSymbol='" + flightSymbol + '\'' +
                ", airline='" + airline + '\'' +
                ", scheduledDeparture='" + scheduledDeparture + '\'' +
                ", departure='" + departure + '\'' +
                ", scheduledArrival='" + scheduledArrival + '\'' +
                ", arrival='" + arrival + '\'' +
                ", flightStatus='" + flightStatus + '\'' +
                ", airportFrom='" + airportFrom + '\'' +
                ", airportTo='" + airportTo + '\'' +
                ", departureDelay=" + departureDelay +
                ", arrivalDelay=" + arrivalDelay +
                ", relativeDelay=" + relativeDelay +
                ", flightNumber='" + flightNumber + '\'' +
                ", airlineCode='" + airlineCode + '\'' +
                '}';
    }
}
