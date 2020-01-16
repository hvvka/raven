package pl.edu.pwr.raven.flightconsumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EmbeddedKafka(
        topics = {"raw-flight-data", "processed-data"}
)
public class MessageProducerTests {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducerTests.class);

    private static final String PROCESSED_DATA = "processed-data";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MessageProducer messageProducer;

    private Consumer<String, String> consumer;

    @Before
    public void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new StringDeserializer()).createConsumer();
        consumer.subscribe(singleton(PROCESSED_DATA));
        consumer.poll(0);
    }

    @After
    public void tearDown() {
        consumer.close();
    }

    @Test
    public void sendFlightToProcessedDataTopicSuccessfully() {
        // given
        Flight validFlight = getExpectedValidFlight();

        // when
        messageProducer.send(validFlight);

        // then
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, PROCESSED_DATA);
        LOG.debug("Received: {}", singleRecord);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.value()).isEqualTo(validFlight.toString());
    }

    static Flight getExpectedValidFlight() {
        return new FlightBuilder()
                .setFlightSymbol("AAL - 203")
                .setAirline("AMERICAN AIRLINES INC")
                .setScheduledDeparture(new Date(1452683580000L))
                .setDeparture(new Date(1452683580000L))
                .setScheduledArrival(new Date(1452717000000L))
                .setArrival(new Date(1452717000000L))
                .setFlightStatus("Confirmed")
                .setAirportFrom("SBPA")
                .setAirportTo("KMIA")
                .setDepartureDelay(0)
                .setArrivalDelay(0)
                .setRelativeDelay(0)
                .setFlightNumber(203)
                .setAirlineCode("AAL")
                .createFlight();
    }
}
