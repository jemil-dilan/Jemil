// notification-module/build.gradle.kts
// Ce module ne dépend d'aucun autre module métier.
// Il consomme uniquement des événements via RabbitMQ.
// C'est le premier candidat à extraire en microservice indépendant.
plugins {
    id("jemil.spring-conventions")
}

dependencies {
    // Pas de project() dependency — découplage total via messaging
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.liquibase.core)
    runtimeOnly(libs.postgresql)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // Africa's Talking SDK pour SMS au Cameroun
    implementation("com.africastalking:core:3.4.4")

    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}
