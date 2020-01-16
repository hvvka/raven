package pl.edu.pwr.raven.flightconsumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.pwr.raven.flightconsumer.MessageProducerTests.getExpectedValidFlight;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
@EmbeddedKafka(
        topics = {"raw-flight-data", "processed-data"}
)
public class MessageListenerTests {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListenerTests.class);

    private static final String RAW_FLIGHT_DATA = "raw-flight-data";

    private static final String PROCESSED_DATA = "processed-data";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private FlightRepository flightRepository;

    private Consumer<String, String> consumer;

    @Before
    public void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new StringDeserializer()).createConsumer();
        consumer.subscribe(singleton(PROCESSED_DATA));
        consumer.poll(0);
    }

    private static String removeId(String flight) {
        return flight.replaceAll("id='.*'", "id=''");
    }

    @Test
    public void consumerReceivesFlightAndSavesItToDb() {
        // given
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();

        // when
        producer.send(new ProducerRecord<>(RAW_FLIGHT_DATA, null, getValidFlightJsonRecord()));
        producer.flush();

        // then
        Flight validFlight = getExpectedValidFlight();
        Awaitility.await().timeout(Durations.TEN_SECONDS)
                .until(() -> !flightRepository.findAll().isEmpty());
        Flight receivedFlight = flightRepository.findAll().get(0);
        assertThat(receivedFlight).isEqualToIgnoringGivenFields(validFlight, "id");
    }

    @Test
    public void consumerReceivesFlightAndProducerSendsItToNewTopic() {
        // given
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();

        // when
        producer.send(new ProducerRecord<>(RAW_FLIGHT_DATA, null, getValidFlightJsonRecord()));
        producer.flush();

        // then
        String validFlight = removeId(getExpectedValidFlight().toString());
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, PROCESSED_DATA);
        String receivedFlight = removeId(singleRecord.value());

        LOG.debug("Received: {}", singleRecord);
        assertThat(singleRecord).isNotNull();
        assertThat(receivedFlight).isEqualTo(validFlight);
    }

    private String getValidFlightJsonRecord() {
        return "{" +
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
    }
}
