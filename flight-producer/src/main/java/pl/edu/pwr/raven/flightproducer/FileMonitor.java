package pl.edu.pwr.raven.flightproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

public class FileMonitor extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(FileMonitor.class);

    private File file;

    private long monitorFrequency;

    private long readingDelay;

    private boolean startAtBeginning;

    private boolean tailing;

    private Set<FileMonitorListener> listeners;

    public FileMonitor(File file) {
        this.file = file;

        this.monitorFrequency = 5000;
        this.startAtBeginning = true;
        this.tailing = false;
        this.listeners = new HashSet<>();
        this.readingDelay = 1000;
    }

    public void addFileMonitorListener(FileMonitorListener fileMonitorListener) {
        this.listeners.add(fileMonitorListener);
    }

    public void removeFileMonitorListener(FileMonitorListener fileMonitorListener) {
        this.listeners.remove(fileMonitorListener);
    }

    protected void fireNewLine(String line) {
        this.listeners.forEach(listener -> listener.handleNewLine(line));
    }

    public void stopTailing() {
        this.tailing = false;
    }

    @Override
    public void run() {
        this.tailing = true;
        long filePointer = this.startAtBeginning ? 0 : this.file.length();

        try (RandomAccessFile accessFile = new RandomAccessFile(this.file, "r")) {
            readFile(filePointer, accessFile);
        } catch (IOException | InterruptedException e) {
            LOG.error("", e);
        }
    }

    private void readFile(long filePointer, RandomAccessFile accessFile) throws IOException, InterruptedException {
        long fileLength;
        while (this.tailing) {
            fileLength = this.file.length();
            if (fileLength < filePointer) {
                filePointer = 0;
            } else if (fileLength > filePointer) {
                filePointer = readData(filePointer, accessFile);
            }
            sleep(this.monitorFrequency);
        }
    }

    private long readData(long filePointer, RandomAccessFile accessFile) throws IOException, InterruptedException {
        accessFile.seek(filePointer);
        if (filePointer == 0) {
            // ignore CSV header
            accessFile.readLine();
        }

        String line;
        do {
            line = accessFile.readLine();
            this.fireNewLine(line);
            sleep(this.readingDelay);
        } while (line != null && !line.isBlank());

        return accessFile.getFilePointer();
    }
}