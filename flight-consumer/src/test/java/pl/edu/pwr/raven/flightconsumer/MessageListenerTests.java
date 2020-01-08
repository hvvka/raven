package pl.edu.pwr.raven.flightconsumer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightBuilder;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;

import java.util.Map;

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

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka =
            new EmbeddedKafkaRule(1, true, TOPIC_NAME);

    private MessageProducer producerMock = Mockito.mock(MessageProducer.class);

    @MockBean
    private FlightRepository mockRepository;

    private MessageListener sut;

    private KafkaTemplate<String, String> producer;

    @Before
    public void setUp() {
        this.producer = buildKafkaTemplate();
        this.producer.setDefaultTopic(TOPIC_NAME);

//        // wait until the partitions are assigned
//        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
//                .getListenerContainers()) {
//            ContainerTestUtils.waitForAssignment(messageListenerContainer,
//                    embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
//        }`

        this.sut = new MessageListener(mockRepository, producerMock);
    }

    private KafkaTemplate<String, String> buildKafkaTemplate() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
        senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(senderProps);
        return new KafkaTemplate<>(pf);
    }

    @Test
    public void dummyTest() {
        final String validFlightRecord = "" +
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
                "\"airlineCode\": \"AAL\"";
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
        producer.sendDefault("1", validFlightRecord);

        verify(mockRepository).save(expectedFlight);
        verify(producerMock).send(expectedFlight);
    }
}
