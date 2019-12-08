package pl.edu.pwr.raven.flightproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import pl.edu.pwr.raven.flightproducer.acquisition.DirectoryMonitor;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
@Service
public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    private File csvOutputFile;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${data.input}")
    private String inputDirectory;

    @Value(value = "${data.output}")
    private String outputFile;

    @Value(value = "${topic.name}")
    private String topicName;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    void initOutputFile() {
        this.csvOutputFile = new File(outputFile);
    }

    public void sendMessage(String message) {
        FlightBuilder.createFlight(message).ifPresent(flight -> {
            saveToCsv(message);
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, flight.toString());
            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    LOG.info("Sent message=[{}] with offset=[{}]", flight, result.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(Throwable ex) {
                    LOG.error("Unable to send message=[{}]", message, ex);
                }
            });
        });
    }

    private void saveToCsv(String message) {
        try (FileWriter pw = new FileWriter(csvOutputFile, true)) {
            pw.append(message + '\n');
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    public void sendFlights() {
        DirectoryMonitor directoryMonitor = new DirectoryMonitor(inputDirectory);
        directoryMonitor.setOnNewRecord(this::sendMessage);
        directoryMonitor.start();
    }
}
