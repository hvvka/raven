package pl.edu.pwr.raven.flightconsumer.flight;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
@Document(collection = "Flights")
public class Flight {

    @Id
    public String id;
    public String flightSymbol;
    @Indexed
    public String airline;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date scheduledDeparture;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date departure;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date scheduledArrival;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date arrival;
    public String flightStatus;
    @Indexed
    public String airportFrom;
    @Indexed
    public String airportTo;
    public Double departureDelay;
    public Double arrivalDelay;
    public Double relativeDelay;
    public Integer flightNumber;
    public String airlineCode;

    public Flight(String flightSymbol,
                  String airline,
                  Date scheduledDeparture,
                  Date departure,
                  Date scheduledArrival,
                  Date arrival,
                  String flightStatus,
                  String airportFrom,
                  String airportTo,
                  double departureDelay,
                  double arrivalDelay,
                  double relativeDelay,
                  int flightNumber,
                  String airlineCode) {
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
