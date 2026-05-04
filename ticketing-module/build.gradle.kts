// ticketing-module/build.gradle.kts
plugins {
    id("jemil.spring-conventions")
    alias(libs.plugins.openapi.generator)
}

dependencies {
    implementation(project(":booking-module"))
    implementation(project(":payment-module"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.liquibase.core)
    runtimeOnly(libs.postgresql)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.springdoc.openapi)

    // JWT pour génération et validation des QR codes
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // QR code generation
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}

openApiGenerate {
    generatorName = "spring"
    inputSpec     = "$projectDir/src/main/resources/api/ticketing-api.yml"
    outputDir     = layout.buildDirectory.dir("generated/openapi").get().asFile.path
    apiPackage    = "cm.jemil.ticketing.infrastructure.web.generated"
    modelPackage  = "cm.jemil.ticketing.infrastructure.web.generated.model"
    configOptions = mapOf(
        "interfaceOnly" to "true", "useSpringBoot3" to "true",
        "useTags" to "true", "documentationProvider" to "springdoc",
        "openApiNullable" to "false", "useJakartaEe" to "true",
    )
}
sourceSets.main { java.srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java")) }
tasks.compileJava { dependsOn(tasks.openApiGenerate) }
