// buildSrc/src/main/kotlin/jemil.spring-conventions.gradle.kts
//
// Convention pour les modules Spring Boot.
// Hérite de jemil.java-conventions et ajoute tout ce qui est spécifique à Spring.
//
// Usage : plugins { id("jemil.spring-conventions") }

plugins {
    id("jemil.java-conventions") // hérite de toutes les règles Java communes
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// ── Dépendances Spring communes à tous les modules Spring ─────
dependencies {
    // Spring Boot de base
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Logs structurés JSON (utile pour les environnements cloud)
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Tests Spring
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito") // on utilise notre version de mockito
    }
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.postgresql:postgresql")

    // ArchUnit — vérifie que l'architecture hexagonale est respectée
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")

    // Cucumber — tests e2e en langage naturel (Gherkin)
    testImplementation("io.cucumber:cucumber-java:7.16.1")
    testImplementation("io.cucumber:cucumber-spring:7.16.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.16.1")

    // RestAssured — appels HTTP dans les tests e2e
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:json-path:5.4.0")
}

// ── Propriétés Spring communes ────────────────────────────────
// (les propriétés spécifiques restent dans application.yml de chaque module)
