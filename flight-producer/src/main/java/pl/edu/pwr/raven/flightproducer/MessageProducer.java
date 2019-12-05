package pl.edu.pwr.raven.flightproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import pl.edu.pwr.raven.flightproducer.acquisition.DirectoryMonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    private final File csvOutputFile;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${data.input}")
    private String inputDirectory;

    @Value(value = "${data.output}")
    private String outputFile;

    @Value(value = "${topic.name}")
    private String topicName;

    public MessageProducer() throws IOException {
        this.csvOutputFile = new File(outputFile);
        this.csvOutputFile.createNewFile();
    }

    public void sendMessage(String message) {
        saveToCsv(message);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("Unable to send message=[{}]", message, ex);
            }
        });
    }

    private void saveToCsv(String message) {
        // TODO: validate message
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(message);
        } catch (FileNotFoundException e) {
            LOG.error("", e);
        }
    }

    public void sendFlights() {
        DirectoryMonitor directoryMonitor = new DirectoryMonitor(inputDirectory);
        directoryMonitor.setOnNewRecord(this::sendMessage);
        // TODO: AVRO schema + writing to a CSV file (output/valid-flights.csv)
        // directoryMonitor.setOnNewRecord(this::saveToCSV);
        directoryMonitor.start();
    }
}
