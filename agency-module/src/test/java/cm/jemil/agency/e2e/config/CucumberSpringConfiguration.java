package cm.jemil.agency.e2e.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Configuration Cucumber + Spring Boot + Testcontainers.
 *
 * <p>Tout test Cucumber dans ce module utilise CETTE configuration.
 * - Spring Boot démarre en mode test sur un port aléatoire
 * - PostgreSQL réel (via Docker) remplace H2 en mémoire
 * - Les propriétés DB sont injectées dynamiquement depuis le container
 *
 * <p>Avantage : tes tests e2e tournent sur une vraie PostgreSQL,
 * exactement comme en production. Pas de surprise.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    // Le container PostgreSQL démarre une seule fois pour tous les tests
    // (réutilisé entre les scénarios pour la performance)
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("agency_db_test")
                    .withUsername("jemil_test")
                    .withPassword("jemil_test_secret");

    @LocalServerPort
    protected int port;

    // Injecte les propriétés du container dans Spring
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Désactive RabbitMQ en test (on le mocke)
        registry.add("spring.rabbitmq.host", () -> "localhost");
        registry.add("spring.autoconfigure.exclude",
                () -> "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration");
    }
}
