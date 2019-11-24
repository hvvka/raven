package pl.edu.pwr.raven.flightproducer.acquisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public class DirectoryMonitor extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryMonitor.class);

    private Path directoryPath;

    private Consumer<String> onNewRecord;

    public DirectoryMonitor(String directoryPath) {
        this.directoryPath = Paths.get(directoryPath);
    }

    @Override
    public void run() {
        addExistingFiles();
        watchNewFiles();
    }

    private void addExistingFiles() {
        File directory = directoryPath.toFile();
        Arrays.stream(directory.listFiles())
                .map(file -> new FileReader(file.getAbsolutePath()))
                .forEach(fileReader -> fileReader.setOnNewRecord(this.onNewRecord));
    }

    private void watchNewFiles() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    String filePath = this.directoryPath.resolve(String.valueOf(event.context())).toString();
                    LOG.info("Added new file: {}", filePath);
                    FileReader fileReader = new FileReader(filePath);
                    fileReader.setOnNewRecord(this.onNewRecord);
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("", e);
        }
    }

    public void setOnNewRecord(Consumer<String> onNewRecord) {
        this.onNewRecord = onNewRecord;
    }
}
