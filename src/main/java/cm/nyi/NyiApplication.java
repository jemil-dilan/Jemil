package cm.nyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NyiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NyiApplication.class, args);
    }
}
