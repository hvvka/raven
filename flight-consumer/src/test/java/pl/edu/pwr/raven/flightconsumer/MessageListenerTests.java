package pl.edu.pwr.raven.flightconsumer;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightBuilder;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;

import static org.mockito.Mockito.verify;

@EnableKafka
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = false,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = { "raw-flight-data" }
        )
public class MessageListenerTests {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListenerTests.class);

    private static final String TOPIC_NAME = "raw-flight-data";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka =
            new EmbeddedKafkaRule(1, false, TOPIC_NAME);

    @MockBean
    private MessageProducer producerMock;

    @MockBean
    private FlightRepository mockRepository;

    @Autowired
    private MessageListener sut;

    @Autowired
    private KafkaTemplate<String, String> producer;

    @Before
    public void setUp() {
        this.producer.setDefaultTopic(TOPIC_NAME);
    }

    @Test
    public void dummyTest() throws Exception {
        final String validFlightRecord = "{" +
                "\"flightSymbol\": \"AAL - 203\"," +
                "\"airline\": \"AMERICAN AIRLINES INC\"," +
                "\"flightType\": \"International\"," +
                "\"scheduledDeparture\": \"2016-01-13 12:13:00\"," +
                "\"departure\": \"2016-01-13 12:13:00\"," +
                "\"scheduledArrival\": \"2016-01-13 21:30:00\"," +
                "\"arrival\": \"2016-01-13 21:30:00\"," +
                "\"flightStatus\": \"Confirmed\"," +
                "\"justification\": \"NA\"," +
                "\"airportFrom\": \"SBPA\"," +
                "\"airportTo\": \"KMIA\"," +
                "\"longitudeTo\": -80.2870457," +
                "\"latitudeTo\": 25.795865," +
                "\"longitudeFrom\": -51.1753811," +
                "\"latitudeFrom\": -29.9934732," +
                "\"departureDelay\": 0," +
                "\"arrivalDelay\": 0," +
                "\"distanceInMeters\": 6910628.87158164," +
                "\"flightNumber\": 203," +
                "\"airlineCode\": \"AAL\"" +
                "}";
        final Flight expectedFlight =  new FlightBuilder()
                                            .setFlightSymbol("AAL - 203")
                                            .setAirline("AMERICAN AIRLINES INC")
                                            .setScheduledDeparture("2016-01-13 12:13:00")
                                            .setDeparture("2016-01-13 12:13:00")
                                            .setScheduledArrival("2016-01-13 21:30:00")
                                            .setArrival("2016-01-13 21:30:00")
                                            .setFlightStatus("Confirmed")
                                            .setAirportFrom("SBPA")
                                            .setAirportTo("KMIA")
                                            .setDepartureDelay(0)
                                            .setArrivalDelay(0)
                                            .setRelativeDelay(0)
                                            .setFlightNumber(203)
                                            .setAirlineCode("AAL")
                                            .createFlight();
        Thread.sleep(1000);
        producer.send(TOPIC_NAME, validFlightRecord);
        producer.flush();

        // verify(mockRepository).save(expectedFlight);
        verify(producerMock).send(expectedFlight);
    }
}
