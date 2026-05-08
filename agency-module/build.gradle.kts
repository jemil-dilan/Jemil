import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

// Agency-module/build.gradle.kts
//
// Module responsable de : agences, routes, horaires, places.
// Tous les autres modules dépendent de celui-ci (source des données de base).
//
// Explication de la communication inter-modules :
// Si booking-module a besoin de vérifier qu'une route existe,
// il appelle l'API REST d'agency-module (en microservices)
// OU il dépend de ce module via implementation (project(":agency-module"))
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
// ---------------------------------------------------------------------------
// Factorized OpenAPI generation function
// ---------------------------------------------------------------------------
fun registerOpenApiGenerateTask(
    taskName: String,
    specFile: String,
    outputDirName: String,
    taskModelPackage: String,
    libraryName: String = "spring-http-interface",
    taskApiPackage: String? = null,
    taskModelNamePrefix: String? = null,
    taskApiNameSuffix: String? = null,
    extraConfigOptions: Map<String, String> = emptyMap(),
) {
    tasks.register<GenerateTask>(taskName) {
        generatorName = "spring"
        templateDir.set("$projectDir/specs/openapi/templates/$libraryName")
        inputSpec = specFile
        outputDir =
            layout.buildDirectory
                .dir("generated/sources/openapi/$outputDirName")
                .get()
                .asFile.path
        taskApiPackage?.let { apiPackage = it }
        modelPackage = taskModelPackage
        taskModelNamePrefix?.let { modelNamePrefix = it }
        taskApiNameSuffix?.let { apiNameSuffix = it }
        configOptions =
            mutableMapOf(
                "dateLibrary" to "java8-localdatetime",
                "library" to libraryName,
                "interfaceOnly" to "true",
                "useTags" to "true",
                "useJakartaEe" to "true",
                "useJackson3" to "true",
                "useSpringBoot4" to "true",
                "openApiNullable" to "false",
            ).apply { putAll(extraConfigOptions) }
        typeMappings =
            mapOf(
                "time" to "java.time.LocalTime",
            )
        inputs.file(inputSpec)
        inputs.dir(templateDir)
        outputs.dir(outputDir)
        doFirst {
            delete(outputDir)
        }
    }
}

registerOpenApiGenerateTask(
    taskName = "mainOpenApiGenerate",
    specFile = "$projectDir/specs/openapi/inbound/main.yaml",
    outputDirName = "main",
    libraryName = "spring-boot",
    taskApiPackage = "cm.milou.generated.jemil.agency.adapter.rest.inbound.api",
    taskModelPackage = "cm.milou.generated.jemil.agency.adapter.rest.inbound.dto",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "mainDomainEventsOpenApiGenerate",
    specFile = "$projectDir/specs/openapi/outbound/domain-events.yaml",
    outputDirName = "main-events",
    libraryName = "spring-http-interface",
    taskModelPackage = "cm.lao.generated.jemil.agency.adapter.messaging.outbound.dto",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)


tasks.compileJava.get().dependsOn(
    tasks["mainOpenApiGenerate"],
    tasks["mainDomainEventsOpenApiGenerate"],
)

// Inclure le code OpenAPI généré dans les sources Java
sourceSets.main.get().java.srcDirs(
    layout.buildDirectory
        .dir("generated/sources/openapi/main/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/main-events/src/main/java")
        .get()
        .asFile.path,
)

