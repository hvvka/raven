package pl.edu.pwr.raven.flightproducer;

import java.io.File;
import java.util.function.Consumer;

public class FileReader implements FileMonitorListener {

    private FileMonitor fileMonitor;

    private Consumer<String> onNewRecord;

    public FileReader(String filename) {
        fileMonitor = new FileMonitor(new File(filename), 1000, true);
        fileMonitor.addFileMonitorListener(this);
        fileMonitor.start();
    }

    public void setOnNewRecord(Consumer<String> onNewRecord) {
        this.onNewRecord = onNewRecord;
    }

    /**
     * A new flightRecord has been added to the tailed log file
     *
     * @param flightRecord The new flightRecord that has been added to the tailed log file
     */
    public void newLogFileLine(String flightRecord) {
        System.out.println(flightRecord);
        this.onNewRecord.accept(flightRecord);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
