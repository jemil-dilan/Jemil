// booking-module/build.gradle.kts
//
// Module responsable de : réservations, passagers, manifestes.
// Dépend de agency-module pour vérifier les routes et horaires.

plugins {
    id("jemil.spring-conventions")
    alias(libs.plugins.openapi.generator)
}

dependencies {
    // ── Dépendance inter-module ───────────────────────────────
    // booking-module a besoin de connaître les routes et agences.
    // Pendant la phase monolithe, on référence le module directement.
    // Quand on extrait en microservices, on supprime cette ligne
    // et on appelle l'API REST de agency-module via un FeignClient.
    implementation(project(":agency-module"))

    // ── Persistence ──────────────────────────────────────────
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.liquibase.core)
    runtimeOnly(libs.postgresql)

    // ── Messaging ────────────────────────────────────────────
    implementation(libs.spring.boot.starter.amqp)

    // ── Mapping ──────────────────────────────────────────────
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // ── Docs ─────────────────────────────────────────────────
    implementation(libs.springdoc.openapi)

    // ── Security ─────────────────────────────────────────────
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // ── Tests ─────────────────────────────────────────────────
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}

openApiGenerate {
    generatorName = "spring"
    inputSpec     = "$projectDir/src/main/resources/api/booking-api.yml"
    outputDir     = layout.buildDirectory.dir("generated/openapi").get().asFile.path
    apiPackage    = "cm.jemil.booking.infrastructure.web.generated"
    modelPackage  = "cm.jemil.booking.infrastructure.web.generated.model"
    configOptions = mapOf(
        "interfaceOnly"         to "true",
        "useSpringBoot3"        to "true",
        "useTags"               to "true",
        "documentationProvider" to "springdoc",
        "openApiNullable"       to "false",
        "useJakartaEe"          to "true",
    )
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java"))
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}
