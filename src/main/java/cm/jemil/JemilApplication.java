package cm.jemil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JemilApplication {

    public static void main(String[] args) {
        SpringApplication.run(JemilApplication.class, args);
    }
}
