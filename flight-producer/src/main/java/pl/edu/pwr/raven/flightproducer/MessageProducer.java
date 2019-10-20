package pl.edu.pwr.raven.flightproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${data.file}")
    private String inputFileName;

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
        readFile();
    }

    private void readFile() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)))) {
            readLines(br);
        } catch (IOException | InterruptedException e) {
            LOG.error("Failed to open file {}", inputFileName, e);
        }
    }

    private void readLines(BufferedReader br) throws IOException, InterruptedException {
        // Omit CSV header
        br.readLine();

        String flightRecord;
        while ((flightRecord = br.readLine()) != null) {
            this.sendMessage(flightRecord);
            Thread.sleep(1000);
        }
    }
}
