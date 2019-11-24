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

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${data.directory}")
    private String inputDirectory;

    @Value(value = "${topic.name}")
    private String topicName;

    public void sendMessage(String message) {
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

    public void sendFlights() {
        DirectoryMonitor directoryMonitor = new DirectoryMonitor(inputDirectory);
        directoryMonitor.setOnNewRecord(this::sendMessage);
        // TODO: AVRO schema + writing to a CSV file (output/valid-flights.csv)
        // directoryMonitor.setOnNewRecord(this::saveToCSV);
        directoryMonitor.start();
    }
}
