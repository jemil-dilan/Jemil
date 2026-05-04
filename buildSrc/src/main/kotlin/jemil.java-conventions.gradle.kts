// buildSrc/src/main/kotlin/jemil.java-conventions.gradle.kts
//
// Convention partagée par TOUS les modules.
// Pense à ce fichier comme le "règlement de copropriété" :
// tout module qui applique ce plugin respecte automatiquement ces règles.
//
// Usage dans un module : plugins { id("jemil.java-conventions") }

import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    checkstyle
    jacoco
    id("com.diffplug.spotless")
    id("net.ltgt.errorprone")
}

// ── Java 21 pour tous les modules ────────────────────────────
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

// ── Dépendances communes à tous les modules ───────────────────
dependencies {
    // Error Prone — analyse statique à la compilation
    errorprone("com.google.errorprone:error_prone_core:2.26.1")

    // Tests communs à tous les modules
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ── Spotless — formatage automatique du code ──────────────────
// Lance : ./gradlew spotlessApply  → formate
// Lance : ./gradlew spotlessCheck  → vérifie sans modifier
spotless {
    java {
        // Palantir Java Format : plus souple que Google Style, très utilisé en entreprise
        palantirJavaFormat("2.39.0")
        removeUnusedImports()           // supprime les imports inutilisés
        trimTrailingWhitespace()        // supprime les espaces en fin de ligne
        endWithNewline()                // ligne vide en fin de fichier
        formatAnnotations()             // formate les annotations correctement

        // Ne formate pas le code généré (OpenAPI, MapStruct)
        targetExclude("**/generated/**", "build/**")
    }
}

// ── Error Prone — détection de bugs à la compilation ─────────
tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode = true
        // Ces checks font ÉCHOUER la compilation si violés
        error(
            "UnusedVariable",           // variable déclarée mais jamais utilisée
            "UnnecessaryParentheses",   // parenthèses inutiles
            "MissingOverride",          // @Override manquant
            "EqualsHashCode",           // equals() sans hashCode() ou vice versa
            "StringSplitter",           // String.split() avec comportement inattendu
        )
        // Ces checks sont des warnings (ne font pas échouer)
        warn(
            "FieldCanBeFinal",          // champ qui pourrait être final
        )
    }
}

// ── Checkstyle — conventions de code ─────────────────────────
checkstyle {
    toolVersion    = "10.14.2"
    configFile     = rootProject.file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false           // fait échouer le build si violation
}

// ── Tests — configuration JUnit 5 ────────────────────────────
tasks.withType<Test>().configureEach {
    useJUnitPlatform()                 // active JUnit 5
    jvmArgs("-XX:+EnableDynamicAgentLoading") // silence warning Java 21

    // Affiche les résultats des tests dans la console
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// ── Jacoco — couverture de code ───────────────────────────────
// Lance : ./gradlew jacocoTestReport → génère le rapport HTML
tasks.jacocoTestReport {
    dependsOn(tasks.test) // les tests doivent tourner avant le rapport

    reports {
        xml.required  = true  // pour SonarQube
        html.required = true  // pour lecture humaine → build/reports/jacoco/
    }

    // Exclure le code qu'on ne teste pas (généré automatiquement)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/generated/**",
                    "**/*MapperImpl.class",  // MapStruct
                    "**/*Application.class", // main Spring Boot
                )
            }
        })
    )
}

// Le rapport Jacoco se génère automatiquement après les tests
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
