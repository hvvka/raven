package pl.edu.pwr.raven.flightproducer.acquisition;

/**
 * @author <a href="mailto:226154@student.pwr.edu.pl">Hanna Grodzicka</a>
 */
public interface FileMonitorListener {

    void handleNewLine(String line);
}
