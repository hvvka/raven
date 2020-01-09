package pl.edu.pwr.raven.flightproducer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pwr.raven.flightproducer.avro.Flight;
import pl.edu.pwr.raven.flightproducer.avro.FlightStatus;
import pl.edu.pwr.raven.flightproducer.avro.FlightType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
public class MessageProducerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducerTest.class);

    private static final String TOPIC_NAME = "raw-flight-data";

    private static final String CSV_HEADER = "\"Flight.No\",\"Airline\",\"Flight.Type\",\"Scheduled.Departure\",\"Departure\",\"Scheduled.Arrival\",\"Arrival\",\"Flight.Status\",\"Justification\",\"Airport.From\",\"Airport.To\",\"Longitude.To\",\"Latitude.To\",\"Longitude.From\",\"Latitude.From\",\"Departure.Delay\",\"Arrival.Delay\",\"Distance.In.Meters\",\"Flight.No2\",\"AirLine.Code\"\n";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, TOPIC_NAME);

    @Autowired
    private MessageProducer messageProducer;

    private KafkaMessageListenerContainer<String, String> container;

    private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;

    @Value("${data.output}")
    private String outputFile;

    @Before
    public void setUp() {
        consumerRecords = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false",
                embeddedKafka.getEmbeddedKafka());
        DefaultKafkaConsumerFactory<String, String> consumer = new DefaultKafkaConsumerFactory<>(consumerProperties);
        container = new KafkaMessageListenerContainer<>(consumer, containerProperties);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            LOG.info("Listened message={}", record.toString());
            consumerRecords.add(record);
        });
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
    }

    @After
    public void tearDown() throws FileNotFoundException {
        container.stop();
        PrintWriter pw = new PrintWriter(outputFile);
        pw.close();
    }

    @Test
    public void sendFlightToKafkaSuccessful() throws InterruptedException, IOException {
        // given
        String validFlightRecord = "\"AAL - 203\",\"AMERICAN AIRLINES INC\",\"International\",2016-01-30 08:58:00,2016-01-30 08:58:00,2016-01-30 10:35:00,2016-01-30 10:35:00,\"Confirmed\",NA,\"SBCT\",\"SBPA\",-51.1753811,-29.9934732,-49.1724811,-25.5327132,0,0,532259.855202391,\"203\",\"AAL\"\n";
        String fileName = "success.csv";
        File inputFile = getInputFile(validFlightRecord, fileName);

        // when
        messageProducer.sendFlights();
        ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);
        LOG.info("Received: {}", received);

        // then
        String expectedFlight = getExpectedFlight();
        Assert.assertEquals(expectedFlight, received.value());

        // cleanup
        inputFile.delete();
    }

    @Test
    public void sendFlightToKafkaFails() throws InterruptedException, IOException {
        // given
        String invalidFlightRecord = "\"AAL - 203\",\"AMERICAN AIRLINES INC\",\"Invalid\",2016-01-30 08:58:00,2016-01-30 08:58:00,2016-01-30 10:35:00,2016-01-30 10:35:00,\"Confirmed\",NA,\"SBCT\",\"SBPA\",-51.1753811,-29.9934732,-49.1724811,-25.5327132,0,0,532259.855202391,\"203\",\"AAL\"";
        String fileName = "fail.csv";
        File inputFile = getInputFile(invalidFlightRecord, fileName);

        // when
        messageProducer.sendMessage(invalidFlightRecord);
        ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);
        LOG.info("Received: {}", received);

        // then
        Assert.assertNull(received);

        // cleanup
        inputFile.delete();
    }

    private File getInputFile(String flightRecord, String fileName) throws IOException {
        Path inputFile = Paths.get("src/test/resources/input/" + fileName);
        Files.createFile(inputFile);
        try (FileWriter pw = new FileWriter(inputFile.toFile())) {
            pw.append(CSV_HEADER);
            pw.append(flightRecord);
        }
        return inputFile.toFile();
    }

    private String getExpectedFlight() {
        return Flight.newBuilder().setFlightSymbol("AAL - 203")
                .setAirline("AMERICAN AIRLINES INC")
                .setFlightType(FlightType.valueOf("International"))
                .setScheduledDeparture("2016-01-30 08:58:00")
                .setDeparture("2016-01-30 08:58:00")
                .setScheduledArrival("2016-01-30 10:35:00")
                .setArrival("2016-01-30 10:35:00")
                .setFlightStatus(FlightStatus.valueOf("Confirmed"))
                .setJustification("NA")
                .setAirportFrom("SBCT")
                .setAirportTo("SBPA")
                .setLongitudeTo(Double.parseDouble("-51.1753811"))
                .setLatitudeTo(Double.parseDouble("-29.9934732"))
                .setLongitudeFrom(Double.parseDouble("-49.1724811"))
                .setLatitudeFrom(Double.parseDouble("-25.5327132"))
                .setDepartureDelay(Integer.parseInt("0"))
                .setArrivalDelay(Integer.parseInt("0"))
                .setDistanceInMeters(Double.parseDouble("532259.855202391"))
                .setFlightNumber(Integer.parseInt("203"))
                .setAirlineCode("AAL")
                .build()
                .toString();
    }
}
