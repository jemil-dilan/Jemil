package cm.jemil.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "cm.jemil")
class HexagonalArchitectureTest {

    private static final String AUTH = "cm.jemil.auth..";
    private static final String AGENCY = "cm.jemil.agency..";
    private static final String BOOKING = "cm.jemil.booking..";
    private static final String PAYMENT = "cm.jemil.payment..";
    private static final String TICKET = "cm.jemil.ticket..";
    private static final String TRIP = "cm.jemil.trip..";
    private static final String SHARED = "cm.jemil.shared..";

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
            .resideInAPackage("cm.jemil..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("cm.jemil..adapter..");

    @ArchTest
    static final ArchRule SHARED_MUST_NOT_DEPEND_ON_BUSINESS_MODULES = noClasses()
            .that()
            .resideInAPackage(SHARED)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(AUTH, AGENCY, BOOKING, PAYMENT, TICKET, TRIP);
}
