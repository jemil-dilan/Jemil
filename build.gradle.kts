// ============================================================
// build.gradle.kts RACINE
//
// Ce fichier gère UNIQUEMENT ce qui s'applique à TOUS les modules.
// Il ne contient PAS de dépendances applicatives.
// Chaque module a son propre build.gradle.kts pour ses dépendances spécifiques.
// ============================================================

plugins {
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.spotless) apply false // appliqué dans les conventions
}

// ── SonarQube (analyse qualité centralisée) ──────────────────
sonar {
    properties {
        property("sonar.projectKey",       "jemil-backend")
        property("sonar.projectName",      "JEMIL Transport Backend")
        property("sonar.host.url",         System.getenv("SONAR_HOST_URL") ?: "http://localhost:9000")
        property("sonar.token",            System.getenv("SONAR_TOKEN")    ?: "")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths",
            subprojects.map { "${it.buildDir}/reports/jacoco/test/jacocoTestReport.xml" }.joinToString(",")
        )
        property("sonar.exclusions",
            "**/generated/**,**/mapper/**Impl.java,**/*Application.java"
        )
    }
}

// ── Tâche utilitaire : lancer tous les tests depuis la racine ─
tasks.register("testAll") {
    group       = "verification"
    description = "Lance tous les tests de tous les modules"
    dependsOn(subprojects.map { "${it.path}:test" })
}

// ── Tâche utilitaire : formatter tous les modules ─────────────
tasks.register("formatAll") {
    group       = "formatting"
    description = "Applique Spotless sur tous les modules"
    dependsOn(subprojects.map { "${it.path}:spotlessApply" })
}
