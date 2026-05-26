package cm.jemil.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "cm.jemil")
class HexagonalArchitectureTest {

  private static final String AUTH = "cm.jemil.auth..";
  private static final String AGENCY = "cm.jemil.agency..";
  private static final String BOOKING = "cm.jemil.booking..";
  private static final String PAYMENT = "cm.jemil.payment..";
  private static final String TICKET = "cm.jemil.ticket..";
  private static final String SHARED = "cm.jemil.shared..";

  @ArchTest
  static final ArchRule auth_must_not_depend_on_sibling_modules =
      noClasses()
          .that()
          .resideInAPackage(AUTH)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(AGENCY, BOOKING, PAYMENT, TICKET);

  @ArchTest
  static final ArchRule agency_must_not_depend_on_sibling_modules =
      noClasses()
          .that()
          .resideInAPackage(AGENCY)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(AUTH, BOOKING, PAYMENT, TICKET);

  @ArchTest
  static final ArchRule booking_must_not_depend_on_sibling_modules =
      noClasses()
          .that()
          .resideInAPackage(BOOKING)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(AUTH, AGENCY, PAYMENT, TICKET);

  @ArchTest
  static final ArchRule payment_must_not_depend_on_sibling_modules =
      noClasses()
          .that()
          .resideInAPackage(PAYMENT)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(AUTH, AGENCY, BOOKING, TICKET);

  @ArchTest
  static final ArchRule ticket_must_not_depend_on_sibling_modules =
      noClasses()
          .that()
          .resideInAPackage(TICKET)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(AUTH, AGENCY, BOOKING, PAYMENT);

  @ArchTest
  static final ArchRule application_must_not_depend_on_adapter_packages =
      noClasses()
          .that()
          .resideInAPackage("cm.jemil..application..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("cm.jemil..adapter..");

  @ArchTest
  static final ArchRule shared_must_not_depend_on_business_modules =
      noClasses()
          .that()
          .resideInAPackage(SHARED)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(AUTH, AGENCY, BOOKING, PAYMENT, TICKET);
}
