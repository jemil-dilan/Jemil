// agency-module/build.gradle.kts
//
// Module responsable de : agences, routes, horaires, places.
// Tous les autres modules dépendent de celui-ci (source des données de base).
//
// Explication de la communication inter-modules :
// Si booking-module a besoin de vérifier qu'une route existe,
// il appelle l'API REST de agency-module (en microservices)
// OU il dépend de ce module via implementation(project(":agency-module"))
// pendant la phase monolithe modulaire.

plugins {
    id("jemil.spring-conventions") // hérite de toutes les conventions communes
    alias(libs.plugins.openapi.generator)
}

dependencies {
    // ── Persistence ──────────────────────────────────────────
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.liquibase.core)
    runtimeOnly(libs.postgresql)

    // ── Messaging (pour émettre des événements domaine) ──────
    implementation(libs.spring.boot.starter.amqp)

    // ── Mapping DTO ↔ Domain ──────────────────────────────────
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // ── Documentation API auto-générée ────────────────────────
    implementation(libs.springdoc.openapi)

    // ── JWT (pour sécuriser les endpoints) ───────────────────
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // ── Tests spécifiques à ce module ────────────────────────
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}

// ── Génération des interfaces controller depuis le spec OpenAPI ─
// Tu écris le contrat YAML → OpenAPI génère l'interface Java
// → Tu implémentes l'interface → garantit que l'implémentation respecte le contrat
openApiGenerate {
    generatorName = "spring"
    inputSpec     = "$projectDir/src/main/resources/api/agency-api.yml"
    outputDir     = layout.buildDirectory.dir("generated/openapi").get().asFile.path
    apiPackage    = "cm.jemil.agency.infrastructure.web.generated"
    modelPackage  = "cm.jemil.agency.infrastructure.web.generated.model"
    configOptions = mapOf(
        "interfaceOnly"          to "true",  // génère UNIQUEMENT l'interface, pas l'impl
        "useSpringBoot3"         to "true",
        "useTags"                to "true",
        "documentationProvider"  to "springdoc",
        "openApiNullable"        to "false",
        "useJakartaEe"           to "true",  // Spring Boot 3 utilise jakarta.*
    )
}

// Inclure le code OpenAPI généré dans les sources Java
sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java"))
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}
