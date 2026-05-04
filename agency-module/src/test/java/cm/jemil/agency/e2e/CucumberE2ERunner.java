package cm.jemil.agency.e2e;

import org.junit.platform.suite.api.*;

/**
 * Point d'entrée des tests Cucumber.
 *
 * <p>Lance tous les fichiers .feature dans src/test/resources/features/.
 * Le glue pointe vers le package qui contient les step definitions.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
    @ConfigurationParameter(
            key = "cucumber.glue",
            value = "cm.jemil.agency.e2e"),
    @ConfigurationParameter(
            key = "cucumber.plugin",
            value = "pretty, json:build/cucumber-reports/agency-report.json, html:build/cucumber-reports/agency-report.html"),
    @ConfigurationParameter(
            key = "cucumber.publish.quiet",
            value = "true")
})
public class CucumberE2ERunner {}
