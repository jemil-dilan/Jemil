package cm.jemil.agency.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests d'architecture : vérifie que les règles hexagonales sont respectées.
 *
 * <p>Ces tests s'exécutent comme des tests JUnit normaux.
 * Si quelqu'un viole l'architecture (ex: importer Spring dans le domaine),
 * le build ÉCHOUE avec un message clair.
 *
 * <p>C'est ton gardien automatique d'architecture.
 * Il remplace les code reviews manuelles pour les règles structurelles.
 */
@AnalyzeClasses(
        packages = "cm.jemil.agency",
        importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    // ── Règle 1 : Le domaine ne dépend de RIEN d'externe ──────
    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..infrastructure..",
                            "..web..",
                            "..persistence..",
                            "..messaging..")
                    .because("Le domaine est indépendant de l'infrastructure. " +
                             "C'est le principe fondamental de l'architecture hexagonale.");

    // ── Règle 2 : Le domaine ne connaît pas Spring ────────────
    @ArchTest
    static final ArchRule domain_must_not_use_spring =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("org.springframework..")
                    .because("Le domaine doit être testable sans Spring. " +
                             "Les annotations Spring vont dans l'infrastructure.");

    // ── Règle 3 : Le domaine ne connaît pas JPA ───────────────
    @ArchTest
    static final ArchRule domain_must_not_use_jpa =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "org.hibernate..")
                    .because("Le domaine ne sait pas comment il est persisté.");

    // ── Règle 4 : Controllers appellent les ports, pas les services ─
    @ArchTest
    static final ArchRule controllers_must_use_ports_not_services =
            noClasses()
                    .that().resideInAPackage("..web.controller..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application.usecase..")
                    .because("Les controllers doivent dépendre des interfaces (ports), " +
                             "pas des implémentations (services). " +
                             "Utilise le port in: RegisterAgencyUseCase, pas RegisterAgencyService.");

    // ── Règle 5 : Les adapters implémentent les ports ─────────
    @ArchTest
    static final ArchRule adapters_must_implement_ports =
            classes()
                    .that().resideInAPackage("..persistence.adapter..")
                    .should().implement(
                            com.tngtech.archunit.base.DescribedPredicate.describe(
                                    "un port out du domaine",
                                    clazz -> clazz.getPackageName().contains("domain.port.out")))
                    .because("Chaque adapter de persistance doit implémenter un port sortant du domaine.");

    // ── Règle 6 : Application ne dépend pas de l'infrastructure ─
    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..infrastructure..",
                            "..persistence..",
                            "..web..")
                    .because("La couche application dépend uniquement du domaine (ports). " +
                             "Elle ne sait pas comment les données sont stockées ou transportées.");

    // ── Règle 7 : Entités JPA restent dans l'infrastructure ───
    @ArchTest
    static final ArchRule jpa_entities_stay_in_infrastructure =
            classes()
                    .that().resideInAPackage("..persistence.entity..")
                    .should().resideInAPackage("..infrastructure..")
                    .because("Les entités JPA sont un détail d'implémentation. " +
                             "Seule l'infrastructure les connaît.");
}
