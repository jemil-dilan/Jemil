// buildSrc/build.gradle.kts
//
// buildSrc est un projet Gradle spécial.
// Il est compilé AVANT le projet principal.
// Tout ce qu'on déclare ici est disponible dans tous les build.gradle.kts.
// C'est ici qu'on déclare les plugins utilisés dans les conventions.

plugins {
    `kotlin-dsl` // permet d'écrire les conventions en Kotlin
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Ces plugins sont utilisés dans les fichiers .gradle.kts de conventions
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.4")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.1.0")
    implementation("org.openapi.generator:org.openapi.generator.gradle.plugin:7.4.0")
}
