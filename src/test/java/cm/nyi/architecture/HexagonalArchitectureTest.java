package cm.nyi.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "cm.nyi")
class HexagonalArchitectureTest {

    private static final String AUTH = "cm.nyi.auth..";
    private static final String AGENCY = "cm.nyi.agency..";
    private static final String BOOKING = "cm.nyi.booking..";
    private static final String PAYMENT = "cm.nyi.payment..";
    private static final String TICKET = "cm.nyi.ticket..";
    private static final String TRIP = "cm.nyi.trip..";
    private static final String SHARED = "cm.nyi.shared..";

    @ArchTest
    static final ArchRule AUTH_MUST_NOT_DEPEND_ON_SIBLING_MODULES = noClasses()
            .that()
            .resideInAPackage(AUTH)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AGENCY, BOOKING, PAYMENT, TICKET, TRIP);

    @ArchTest
    static final ArchRule AGENCY_MUST_NOT_DEPEND_ON_SIBLING_MODULES = noClasses()
            .that()
            .resideInAPackage(AGENCY)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, BOOKING, PAYMENT, TICKET, TRIP);

    @ArchTest
    static final ArchRule BOOKING_MUST_NOT_DEPEND_ON_SIBLING_MODULES = noClasses()
            .that()
            .resideInAPackage(BOOKING)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, AGENCY, PAYMENT, TICKET, TRIP);

    @ArchTest
    static final ArchRule PAYMENT_MUST_NOT_DEPEND_ON_SIBLING_MODULES = noClasses()
            .that()
            .resideInAPackage(PAYMENT)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, AGENCY, BOOKING, TICKET, TRIP);

    @ArchTest
    static final ArchRule TICKET_MUST_NOT_DEPEND_ON_SIBLING_MODULES = noClasses()
            .that()
            .resideInAPackage(TICKET)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, AGENCY, BOOKING, PAYMENT, TRIP);

    @ArchTest
    static final ArchRule TRIP_MUST_NOT_DEPEND_ON_SIBLING_MODULES = noClasses()
            .that()
            .resideInAPackage(TRIP)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, AGENCY, BOOKING, PAYMENT, TICKET);

    @ArchTest
    static final ArchRule APPLICATION_MUST_NOT_DEPEND_ON_ADAPTER_PACKAGES = noClasses()
            .that()
            .resideInAPackage("cm.nyi..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("cm.nyi..adapter..");

    @ArchTest
    static final ArchRule SHARED_MUST_NOT_DEPEND_ON_BUSINESS_MODULES = noClasses()
            .that()
            .resideInAPackage(SHARED)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, AGENCY, BOOKING, PAYMENT, TICKET, TRIP);

    @ArchTest
    static final ArchRule DOMAIN_MUST_NOT_DEPEND_ON_FRAMEWORK = noClasses()
            .that()
            .resideInAPackage("cm.nyi..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "jakarta.servlet..", "org.hibernate..")
            .as("Domain layer must not depend on framework annotations (Spring, JPA, etc.)");

    @ArchTest
    static final ArchRule AUTH_DOMAIN_MUST_NOT_DEPEND_ON_FRAMEWORK = noClasses()
            .that()
            .resideInAPackage("cm.nyi.auth.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "jakarta.servlet..", "org.hibernate..")
            .as("Auth domain layer must not depend on framework annotations (Spring, JPA, etc.)");
}
