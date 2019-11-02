package pl.edu.pwr.raven.flightproducer;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

/**
 * A log file tailer is designed to monitor a log file and send notifications
 * when new lines are added to the log file. This class has a notification
 * strategy similar to a SAX parser: implement the LogFileTailerListener interface,
 * create a LogFileTailer to tail your log file, add yourself as a listener, and
 * start the LogFileTailer. It is your job to interpret the results, build meaningful
 * sets of data, etc. This tailer simply fires notifications containing new log file lines,
 * one at a time.
 */
public class FileMonitor extends Thread {

    /**
     * How frequently to check for file changes; defaults to 5 seconds
     */
    private long sampleInterval = 5000;

    private long readingDelay = 1000;

    private File logfile;

    /**
     * Defines whether the log file tailer should include the entire contents
     * of the existing log file or tail from the end of the file when the tailer starts
     */
    private boolean startAtBeginning;

    /**
     * Is the monitor currently tailing?
     */
    private boolean tailing = false;

    private Set<FileMonitorListener> listeners;

    public FileMonitor(File file) {
        this.logfile = file;
        this.startAtBeginning = false;
        this.listeners = new HashSet();
    }

    /**
     * Creates a new log file tailer
     *
     * @param file             The file to tail
     * @param sampleInterval   How often to check for updates to the log file (default = 5000ms)
     * @param startAtBeginning Should the tailer simply tail or should it process the entire
     *                         file and continue tailing (true) or simply start tailing from the
     *                         end of the file
     */
    public FileMonitor(File file, long sampleInterval, boolean startAtBeginning) {
        this.logfile = file;
        this.sampleInterval = sampleInterval;
        this.startAtBeginning = startAtBeginning;
        this.listeners = new HashSet();
    }

    public void addFileMonitorListener(FileMonitorListener l) {
        this.listeners.add(l);
    }

    public void removeFileMonitorListener(FileMonitorListener l) {
        this.listeners.remove(l);
    }

    protected void fireNewLogFileLine(String line) {
        for (FileMonitorListener listener : this.listeners) {
            listener.newLogFileLine(line);
        }
    }

    public void stopTailing() {
        this.tailing = false;
    }

    @Override
    public void run() {
        // The file pointer keeps track of where we are in the file
        long filePointer = 0;

        // Determine start point
        if (!this.startAtBeginning) {
            filePointer = this.logfile.length();
        }

        try {
            this.tailing = true;
            RandomAccessFile file = new RandomAccessFile(logfile, "r");
            while (this.tailing) {
                try {
                    // Compare the length of the file to the file pointer
                    long fileLength = this.logfile.length();
                    System.out.println("File length: " + fileLength + ", pointer: " + filePointer);
                    if (fileLength < filePointer) {
                        // Log file must have been rotated or deleted
                        // reopen the file and reset the file pointer
                        file = new RandomAccessFile(logfile, "r");
                        filePointer = 0;
                    } else if (fileLength > filePointer) {
                        // There is data to read
                        file.seek(filePointer);
                        String line = file.readLine();
                        if (filePointer == 0) line = file.readLine();
                        while (line != null && !line.isBlank()) {
                            this.fireNewLogFileLine(line);
                            sleep(this.readingDelay);
                            line = file.readLine();
                        }
                        filePointer = file.getFilePointer();
                    }

                    // Sleep for the specified interval
                    sleep(this.sampleInterval);
                } catch (Exception e) {
                }
            }

            // Close the file that we are tailing
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}