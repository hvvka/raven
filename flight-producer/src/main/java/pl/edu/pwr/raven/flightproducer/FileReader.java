package pl.edu.pwr.raven.flightproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;

public class FileReader implements FileMonitorListener {

    private static final Logger LOG = LoggerFactory.getLogger(FileReader.class);

    private final FileMonitor fileMonitor;

    private Consumer<String> onNewRecord;

    public FileReader(String fileName) {
        File monitoredFile = new File(fileName);
        this.fileMonitor = new FileMonitor(monitoredFile);
        this.fileMonitor.addFileMonitorListener(this);
        this.fileMonitor.start();
    }

    public void setOnNewRecord(Consumer<String> onNewRecord) {
        this.onNewRecord = onNewRecord;
    }

    public void handleNewLine(String flightRecord) {
        this.onNewRecord.accept(flightRecord);
        sleepOneSecond();
    }

    private void sleepOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOG.error("Could not sleep", e);
        }
    }
}
