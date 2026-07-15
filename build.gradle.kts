import com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep
import org.gradle.kotlin.dsl.register
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    java
    checkstyle
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.mgmt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.spotless)
    alias(libs.plugins.error.prone)
}

group = "cm.jemil"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // ── Core Spring Boot ──────────────────────────────────────────
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.liquibase)

    // ── Data ────────────────────────────────────────────────────
    implementation(libs.liquibase.core)
    runtimeOnly(libs.postgresql)

    // ── Security ────────────────────────────────────────────────
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // ── Mapping ─────────────────────────────────────────────────
    implementation(libs.mapstruct)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)

    // ── OpenAPI / Docs ──────────────────────────────────────────
    implementation(libs.springdoc.openapi)
    implementation("org.apache.tomcat.embed:tomcat-embed-el")

    // ── Observability ────────────────────────────────────────────
    implementation(libs.logstash.logback.encoder)

    // ── Error Prone ──────────────────────────────────────────────
    errorprone("com.google.errorprone:error_prone_core:2.43.0")

    // ── Testing ──────────────────────────────────────────────────
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.mockito")
    }
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation(libs.archunit.junit5)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.mapstruct.processor)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.spring)
    testImplementation(libs.cucumber.junit.platform)
    testImplementation(libs.rest.assured)
    testImplementation(libs.rest.assured.json.path)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.awaitility)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ── Spotless ─────────────────────────────────────────────────
spotless {
    java {
        toggleOffOn()
        palantirJavaFormat(
            libs.versions.palantir.format
                .get(),
        )
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
        formatAnnotations()
        targetExclude("build/**")
    }
    kotlin {
        targetExclude("build/**")
        target("**/*.kts")
        ktlint("1.5.0")
    }
    format("xml", {
        targetExclude("build/**")
        target("src/**/*.xml")
        eclipseWtp(EclipseWtpFormatterStep.XML)
    })

    yaml {
        targetExclude("build/**")
        target("src/*/resources/**/*.yaml", "src/*/resources/**/*.yml", "openapi/**/*.yaml")
        targetExclude("src/test/resources/docker-compose.yml")
        jackson()
            .feature("ORDER_MAP_ENTRIES_BY_KEYS", true)
    }
    gherkin {
        targetExclude("build/**")
        target("src/test/resources/**/*.feature")
        gherkinUtils()
            .version("9.0.0")
    }
}

// ── Checkstyle ───────────────────────────────────────────────
checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    exclude("**/build/**")
    exclude("**/generated/**")
    exclude("**/module-info.java")
}

// ── Tests ────────────────────────────────────────────────────
tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading", "--add-reads", "org.hibernate.validator=ALL-UNNAMED")
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// ── Jacoco ───────────────────────────────────────────────────
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

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
    taskConfigPackage: String? = null,
    taskModelNamePrefix: String? = null,
    taskApiNameSuffix: String? = null,
    extraConfigOptions: Map<String, String> = emptyMap(),
) {
    tasks.register<GenerateTask>(taskName) {
        group = "openapi"
        description = "Generate $outputDirName OpenAPI sources from $specFile"
        generatorName = "spring"
        templateDir.set("$projectDir/specs/openapi/templates/$libraryName")
        inputSpec.set(specFile)
        outputDir.set(
            layout.buildDirectory
                .dir("generated/sources/openapi/$outputDirName")
                .get()
                .asFile.path,
        )
        taskApiPackage?.let { apiPackage.set(it) }
        modelPackage.set(taskModelPackage)
        taskModelNamePrefix?.let { modelNamePrefix.set(it) }
        taskApiNameSuffix?.let { apiNameSuffix.set(it) }
        configOptions.set(
            mutableMapOf(
                "dateLibrary" to "java8-localdatetime",
                "library" to libraryName,
                "interfaceOnly" to "true",
                "useTags" to "true",
                "useJakartaEe" to "true",
                "useJackson3" to "true",
                "useSpringBoot4" to "true",
                "openApiNullable" to "false",
            ).apply {
                putAll(extraConfigOptions)
                taskConfigPackage?.let { put("configPackage", it) }
            },
        )
        typeMappings.set(
            mapOf(
                "time" to "java.time.LocalTime",
            ),
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
    taskName = "agencyOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/inbound/agency.yaml",
    outputDirName = "agency",
    libraryName = "spring-boot",
    taskApiPackage = "cm.jemil.generated.agency.adapter.rest.inbound.api",
    taskModelPackage = "cm.jemil.generated.agency.adapter.rest.inbound.dto",
    taskConfigPackage = "cm.jemil.generated.agency.adapter.rest.inbound.config",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "agencyDomainEventsOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/outbound/domain-events.yaml",
    outputDirName = "agency-events",
    libraryName = "spring-http-interface",
    taskModelPackage = "cm.jemil.generated.agency.adapter.messaging.outbound.dto",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "bookingOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/inbound/booking.yaml",
    outputDirName = "booking",
    libraryName = "spring-boot",
    taskApiPackage = "cm.jemil.generated.booking.adapter.rest.inbound.api",
    taskModelPackage = "cm.jemil.generated.booking.adapter.rest.inbound.dto",
    taskConfigPackage = "cm.jemil.generated.booking.adapter.rest.inbound.config",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "bookingDomainEventsOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/outbound/booking-domain-events.yaml",
    outputDirName = "booking-events",
    libraryName = "spring-http-interface",
    taskModelPackage = "cm.jemil.generated.booking.adapter.messaging.outbound.dto",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)
registerOpenApiGenerateTask(
    taskName = "paymentOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/inbound/payment.yaml",
    outputDirName = "payment",
    libraryName = "spring-boot",
    taskApiPackage = "cm.jemil.generated.payment.adapter.rest.inbound.api",
    taskModelPackage = "cm.jemil.generated.payment.adapter.rest.inbound.dto",
    taskConfigPackage = "cm.jemil.generated.payment.adapter.rest.inbound.config",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "paymentDomainEventsOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/outbound/payment-domain-events.yaml",
    outputDirName = "payment-events",
    libraryName = "spring-http-interface",
    taskModelPackage = "cm.jemil.generated.payment.adapter.messaging.outbound.dto",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "ticketOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/inbound/ticket.yaml",
    outputDirName = "ticket",
    libraryName = "spring-boot",
    taskApiPackage = "cm.jemil.generated.ticket.adapter.rest.inbound.api",
    taskModelPackage = "cm.jemil.generated.ticket.adapter.rest.inbound.dto",
    taskConfigPackage = "cm.jemil.generated.ticket.adapter.rest.inbound.config",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

registerOpenApiGenerateTask(
    taskName = "ticketDomainEventsOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/outbound/domain-events.yaml",
    outputDirName = "ticket-events",
    libraryName = "spring-http-interface",
    taskModelPackage = "cm.jemil.generated.ticket.adapter.messaging.outbound.dto",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

// Auth OpenAPI Generation
registerOpenApiGenerateTask(
    taskName = "authOpenApiGenerate",
    specFile = "$rootDir/specs/openapi/inbound/auth.yaml",
    outputDirName = "auth",
    libraryName = "spring-boot",
    taskApiPackage = "cm.jemil.generated.auth.adapter.rest.inbound.api",
    taskModelPackage = "cm.jemil.generated.auth.adapter.rest.inbound.dto",
    taskConfigPackage = "cm.jemil.generated.auth.adapter.rest.inbound.config",
    extraConfigOptions =
        mapOf(
            "skipDefaultInterface" to "true",
        ),
)

tasks.compileJava.get().dependsOn(
    tasks["agencyOpenApiGenerate"],
    tasks["agencyDomainEventsOpenApiGenerate"],
    tasks["bookingOpenApiGenerate"],
    tasks["bookingDomainEventsOpenApiGenerate"],
    tasks["paymentOpenApiGenerate"],
    tasks["paymentDomainEventsOpenApiGenerate"],
    tasks["ticketOpenApiGenerate"],
    tasks["ticketDomainEventsOpenApiGenerate"],
    tasks["authOpenApiGenerate"],
)

// Inclure le code OpenAPI généré dans les sources Java
sourceSets.main.get().java.srcDirs(
    layout.buildDirectory
        .dir("generated/sources/openapi/agency/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/agency-events/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/booking/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/booking-events/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/payment/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/payment-events/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/ticket/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/ticket-events/src/main/java")
        .get()
        .asFile.path,
    layout.buildDirectory
        .dir("generated/sources/openapi/auth/src/main/java")
        .get()
        .asFile.path,
)

// ── Sonar ────────────────────────────────────────────────────
sonar {
    properties {
        property("sonar.projectKey", "jemil-backend")
        property("sonar.projectName", "JEMIL Transport Backend")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "http://localhost:9000")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        property("sonar.java.coveragePlugin", "jacoco")
    }
}
