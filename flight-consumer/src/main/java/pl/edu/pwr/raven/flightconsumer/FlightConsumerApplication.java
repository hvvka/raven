package pl.edu.pwr.raven.flightconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.edu.pwr.raven.flightconsumer.flight.Flight;
import pl.edu.pwr.raven.flightconsumer.flight.FlightRepository;

@SpringBootApplication
public class FlightConsumerApplication implements CommandLineRunner {

    @Autowired
    private FlightRepository flightRepository;

    public static void main(String[] args) {
        SpringApplication.run(FlightConsumerApplication.class, args);
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

    @Override
    public void run(String... args) throws Exception {
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-30 08:58:00","2016-01-30 08:58:00","2016-01-30 10:35:00","2016-01-30 10:35:00","Confirmed","SBCT","SBPA",0,0,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-13 12:13:00","2016-01-13 12:13:00","2016-01-13 21:30:00","2016-01-13 21:30:00","Confirmed","SBPA","KMIA",2,0,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-29 12:13:00","2016-01-29 12:13:00","2016-01-29 21:30:00","2016-01-29 21:30:00","Confirmed","SBPA","KMIA",0,0,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-19 12:13:00","2016-01-18 12:03:00","2016-01-19 21:30:00","2016-01-18 20:41:00","Confirmed","SBPA","KMIA",-1450,-1489,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-30 12:13:00","2016-01-30 12:13:00","2016-01-30 21:30:00","2016-01-30 21:30:00","Confirmed","SBPA","KMIA",0,0,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-03 23:05:00","2016-01-03 23:05:00","2016-01-04 07:50:00","2016-01-04 07:50:00","Confirmed","KMIA","SBCT",0,0,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-05 23:05:00","2016-01-05 23:35:00","2016-01-06 07:50:00","2016-01-06 08:35:00","Confirmed","KMIA","SBCT",30,45,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-18 12:13:00","2016-01-18 13:09:00","2016-01-18 21:30:00","2016-01-18 22:24:00","Confirmed","SBPA","KMIA",56,54,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-22 23:05:00","2016-01-22 23:05:00","2016-01-23 07:50:00","2016-01-23 07:50:00","Confirmed","KMIA","SBCT",0,0,0,203,"AAL"));
        flightRepository.save(new Flight("AAL - 203","AMERICAN AIRLINES INC","2016-01-15 23:05:00","2016-01-15 23:55:00","2016-01-16 07:50:00","2016-01-16 08:28:00","Confirmed","KMIA","SBCT",50,38,0,203,"AAL"));

        flightRepository.findAll().forEach(System.out::println);
    }
}
