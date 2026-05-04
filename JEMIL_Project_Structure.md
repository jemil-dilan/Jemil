# JEMIL Backend — Guide de Structure du Projet

> Ce document décrit la structure complète du projet.  
> Il est ta référence pour implémenter chaque fichier dans le bon endroit.  
> Architecture : **Single Gradle Module · Hexagonal DDD · Outbox Pattern · Java 25 · Spring Boot 4.0**

---

## 📁 Structure Racine

```
jemil-backend/
├── build.gradle.kts                    ← build unique (plus de multi-module)
├── settings.gradle.kts                 ← nom du projet uniquement
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties   ← Gradle 8.7
│   └── libs.versions.toml             ← Version Catalog (toutes les versions ici)
├── config/
│   └── checkstyle/
│       └── checkstyle.xml             ← règles Checkstyle
├── docker-compose.yml                 ← PostgreSQL + SonarQube uniquement (RabbitMQ retiré)
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/cm/jemil/
    │   │   ├── JemilApplication.java   ← @SpringBootApplication
    │   │   ├── shared/
    │   │   ├── auth/
    │   │   ├── agency/
    │   │   ├── booking/
    │   │   ├── payment/
    │   │   ├── ticket/
    │   │   ├── validation/
    │   │   └── dashboard/
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-test.yml
    │       └── db/
    │           └── changelog/
    │               ├── db.changelog-master.xml
    │               └── migrations/
    └── test/
        └── java/cm/jemil/
            ├── architecture/           ← ArchUnit (règles globales)
            ├── auth/
            ├── agency/
            ├── booking/
            ├── payment/
            ├── ticket/
            └── validation/
```

---

## 📦 Package par Package

### `shared/` — Code transversal, zéro logique métier

```
shared/
├── exception/
│   ├── DomainException.java            ← abstract class — base de toutes les exceptions métier
│   ├── NotFoundException.java          ← extends DomainException
│   └── BusinessRuleException.java      ← extends DomainException
│
├── events/
│   └── DomainEvent.java               ← interface — marqueur pour tous les événements domaine
│
├── outbox/
│   ├── OutboxEvent.java               ← @Entity — table outbox_events en base
│   ├── OutboxEventStatus.java         ← enum : PENDING, SENT, FAILED
│   ├── OutboxRepository.java          ← JpaRepository<OutboxEvent, UUID>
│   ├── OutboxEventPublisher.java      ← interface : publish(Object domainEvent)
│   └── OutboxScheduler.java           ← @Scheduled toutes les 5s — lit PENDING et publie
│
├── audit/
│   ├── AuditLog.java                  ← @Entity — table audit_logs
│   ├── AuditLogRepository.java
│   └── AuditService.java              ← log(action, userId, details)
│
└── utils/
    ├── PhoneValidator.java             ← validation format +237XXXXXXXXX
    └── DateUtils.java                 ← helpers date/heure pour le Cameroun (UTC+1)
```

**Règle** : `shared/` ne dépend d'aucun autre package métier.  
**Règle** : Tous les autres packages PEUVENT utiliser `shared/`.

---

### `auth/` — Authentification et autorisation

```
auth/
├── domain/
│   ├── model/
│   │   ├── User.java                  ← Aggregate Root
│   │   ├── UserId.java                ← record UserId(UUID value)
│   │   └── UserRole.java              ← enum : PASSENGER, AGENCY_MANAGER, CONTROLLER, ADMIN
│   ├── port/
│   │   ├── in/
│   │   │   ├── RegisterUserUseCase.java
│   │   │   └── AuthenticateUseCase.java
│   │   └── out/
│   │       └── UserRepository.java    ← interface
│   └── exception/
│       └── AuthDomainException.java
│
├── application/
│   ├── RegisterUserService.java       ← implements RegisterUserUseCase
│   └── AuthenticateService.java       ← implements AuthenticateUseCase
│
└── infrastructure/
    ├── persistence/
    │   ├── UserJpaEntity.java          ← @Entity table users
    │   ├── UserJpaRepository.java      ← JpaRepository
    │   └── UserRepositoryAdapter.java  ← implements UserRepository (port out)
    ├── web/
    │   ├── AuthController.java         ← POST /auth/login · POST /auth/refresh
    │   ├── AuthExceptionHandler.java
    │   └── dto/
    │       ├── LoginRequest.java
    │       ├── LoginResponse.java      ← { token, refreshToken, expiresAt }
    │       └── RefreshRequest.java
    └── security/
        ├── JwtService.java             ← generate() · validate() · extractUserId()
        ├── JwtFilter.java              ← OncePerRequestFilter
        ├── SecurityConfig.java         ← @Configuration @EnableWebSecurity
        └── UserDetailsServiceImpl.java ← implements UserDetailsService
```

---

### `agency/` — Agences et routes

```
agency/
├── domain/
│   ├── model/
│   │   ├── Agency.java                ← Aggregate Root
│   │   ├── AgencyId.java              ← record AgencyId(UUID value)
│   │   ├── AgencyStatus.java          ← enum : ACTIVE, SUSPENDED, INACTIVE
│   │   ├── Route.java                 ← Entity (appartient à Agency)
│   │   ├── RouteId.java               ← record RouteId(UUID value)
│   │   ├── Schedule.java              ← Entity (horaire de départ sur une route)
│   │   └── ScheduleId.java            ← record ScheduleId(UUID value)
│   ├── port/
│   │   ├── in/
│   │   │   ├── RegisterAgencyUseCase.java
│   │   │   ├── AddRouteUseCase.java
│   │   │   └── SearchSchedulesUseCase.java
│   │   └── out/
│   │       └── AgencyRepository.java
│   ├── event/
│   │   └── AgencyRegisteredEvent.java ← implements DomainEvent
│   └── exception/
│       └── AgencyDomainException.java ← extends DomainException
│
├── application/
│   ├── RegisterAgencyService.java
│   ├── AddRouteService.java
│   └── SearchSchedulesService.java
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/
    │   │   ├── AgencyJpaEntity.java
    │   │   ├── RouteJpaEntity.java
    │   │   └── ScheduleJpaEntity.java  ← colonne available_seats + @Version
    │   ├── repository/
    │   │   ├── AgencyJpaRepository.java
    │   │   └── ScheduleJpaRepository.java
    │   └── adapter/
    │       └── AgencyRepositoryAdapter.java
    └── web/
        ├── AgencyController.java       ← POST /agencies · GET /agencies · PATCH /agencies/{id}/suspend
        ├── RouteController.java        ← POST /agencies/{id}/routes
        ├── ScheduleController.java     ← GET /routes/search
        ├── AgencyExceptionHandler.java
        └── mapper/
            └── AgencyWebMapper.java
```

---

### `booking/` — Cœur transactionnel du système

```
booking/
├── domain/
│   ├── model/
│   │   ├── Booking.java               ← Aggregate Root — le plus important du projet
│   │   ├── BookingId.java             ← record BookingId(UUID value)
│   │   ├── BookingStatus.java         ← enum : PENDING_PAYMENT, PAID, BOARDED, CANCELLED, EXPIRED
│   │   ├── PassengerId.java           ← record (wraps UserId)
│   │   └── SeatNumber.java            ← record SeatNumber(int value) — validation 1-100
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateBookingUseCase.java
│   │   │   └── CancelBookingUseCase.java
│   │   └── out/
│   │       └── BookingRepository.java
│   ├── event/
│   │   ├── BookingCreatedEvent.java    ← implements DomainEvent
│   │   └── BookingCancelledEvent.java  ← implements DomainEvent
│   └── exception/
│       └── BookingDomainException.java ← extends DomainException
│
├── application/
│   ├── CreateBookingService.java       ← implements CreateBookingUseCase
│   ├── CancelBookingService.java       ← implements CancelBookingUseCase
│   └── BookingExpiryScheduler.java    ← @Scheduled toutes les 2min — expire PENDING_PAYMENT > 15min
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/
    │   │   └── BookingJpaEntity.java   ← table bookings
    │   ├── repository/
    │   │   └── BookingJpaRepository.java
    │   └── adapter/
    │       └── BookingRepositoryAdapter.java
    └── web/
        ├── BookingController.java      ← POST /bookings · DELETE /bookings/{id} · GET /bookings/{id}
        ├── BookingExceptionHandler.java
        └── dto/
            ├── CreateBookingRequest.java
            └── BookingResponse.java    ← { bookingId, status, expiresAt, seatNumber }
```

**Règles métier dans `Booking.java`** :
- `pay()` → `PENDING_PAYMENT` → `PAID` (interdit si déjà PAID ou CANCELLED)
- `cancel()` → tout statut sauf BOARDED → `CANCELLED`
- `expire()` → `PENDING_PAYMENT` → `EXPIRED` (appelé par le scheduler)
- `board()` → `PAID` → `BOARDED` (appelé par validation)

---

### `payment/` — Paiement MTN MoMo

```
payment/
├── domain/
│   ├── model/
│   │   ├── Payment.java               ← Aggregate Root
│   │   ├── PaymentId.java             ← record PaymentId(UUID value)
│   │   ├── PaymentStatus.java         ← enum : INITIATED, PENDING, CONFIRMED, FAILED, REFUNDED
│   │   └── PaymentAttempt.java        ← Entity — log de chaque tentative MoMo
│   ├── port/
│   │   ├── in/
│   │   │   ├── InitiatePaymentUseCase.java
│   │   │   └── HandleWebhookUseCase.java
│   │   └── out/
│   │       ├── PaymentRepository.java
│   │       └── MomoGateway.java       ← interface — le domaine ne connaît pas MTN
│   ├── event/
│   │   ├── PaymentConfirmedEvent.java ← implements DomainEvent
│   │   └── PaymentFailedEvent.java    ← implements DomainEvent
│   └── exception/
│       └── PaymentDomainException.java
│
├── application/
│   ├── InitiatePaymentService.java    ← implements InitiatePaymentUseCase
│   ├── HandleWebhookService.java      ← implements HandleWebhookUseCase
│   └── PaymentReconciliationScheduler.java ← @Scheduled toutes les 10min
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/
    │   │   ├── PaymentJpaEntity.java
    │   │   └── PaymentAttemptJpaEntity.java
    │   ├── repository/
    │   │   └── PaymentJpaRepository.java
    │   └── adapter/
    │       └── PaymentRepositoryAdapter.java
    ├── web/
    │   ├── PaymentController.java      ← POST /payments/initiate
    │   ├── WebhookController.java      ← POST /payments/webhook/momo
    │   └── dto/
    │       ├── InitiatePaymentRequest.java
    │       ├── PaymentResponse.java
    │       └── MomoWebhookPayload.java
    └── external/
        ├── MomoApiClient.java          ← implements MomoGateway (port out)
        ├── MomoApiConfig.java          ← @ConfigurationProperties("momo.api")
        └── MomoSignatureVerifier.java  ← HMAC-SHA256 webhook verification
```

---

### `ticket/` — Génération QR + SMS

```
ticket/
├── domain/
│   ├── model/
│   │   ├── Ticket.java                ← Entity (pas Aggregate — appartient au Booking)
│   │   ├── TicketId.java              ← record TicketId(UUID value)
│   │   └── TicketStatus.java          ← enum : ISSUED, VALIDATED, EXPIRED, REFUNDED
│   ├── port/
│   │   ├── in/
│   │   │   └── IssueTicketUseCase.java
│   │   └── out/
│   │       ├── TicketRepository.java
│   │       ├── QrCodeGenerator.java   ← interface
│   │       └── SmsNotifier.java       ← interface
│   ├── event/
│   │   └── TicketIssuedEvent.java     ← implements DomainEvent
│   └── exception/
│       └── TicketDomainException.java
│
├── application/
│   ├── IssueTicketService.java        ← écoute PaymentConfirmedEvent → génère ticket
│   └── TicketExpiryScheduler.java     ← expire les tickets non validés après le départ
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/
    │   │   └── TicketJpaEntity.java    ← table tickets
    │   ├── repository/
    │   │   └── TicketJpaRepository.java
    │   └── adapter/
    │       └── TicketRepositoryAdapter.java
    ├── web/
    │   ├── TicketController.java       ← GET /tickets/{id}/qr · GET /tickets/{id}
    │   └── dto/
    │       └── TicketResponse.java
    ├── qr/
    │   ├── ZxingQrCodeGenerator.java  ← implements QrCodeGenerator
    │   └── JwtQrTokenService.java     ← génère + valide le JWT embarqué dans le QR
    └── sms/
        ├── AfricasTalkingSmsNotifier.java ← implements SmsNotifier
        └── SmsConfig.java             ← @ConfigurationProperties("africastalking")
```

---

### `validation/` — Contrôleur offline-first

```
validation/
├── domain/
│   ├── model/
│   │   ├── ValidationRecord.java      ← Entity — log de chaque scan
│   │   ├── ValidationId.java
│   │   └── ValidationStatus.java      ← enum : VALID, ALREADY_USED, EXPIRED, UNKNOWN
│   ├── port/
│   │   ├── in/
│   │   │   ├── ValidateTicketUseCase.java  ← valide QR ou code SMS
│   │   │   └── DownloadManifestUseCase.java ← télécharge manifeste pour usage offline
│   │   └── out/
│   │       └── ValidationRepository.java
│   └── event/
│       └── TicketValidatedEvent.java  ← implements DomainEvent
│
├── application/
│   ├── ValidateTicketService.java
│   └── DownloadManifestService.java
│
└── infrastructure/
    ├── persistence/
    │   ├── entity/
    │   │   └── ValidationRecordJpaEntity.java ← table validation_records
    │   ├── repository/
    │   │   └── ValidationJpaRepository.java
    │   └── adapter/
    │       └── ValidationRepositoryAdapter.java
    └── web/
        ├── ValidationController.java   ← POST /tickets/validate · GET /manifests/download
        └── dto/
            ├── ValidateTicketRequest.java  ← { qrToken } ou { smsCode, scheduleId }
            ├── ValidationResponse.java    ← { status, passengerName, seatNumber, scheduleInfo }
            └── ManifestResponse.java      ← liste passagers pour usage offline
```

---

### `dashboard/` — Read models, zéro logique domaine

```
dashboard/
├── application/
│   ├── GetManifestQuery.java           ← query object
│   ├── GetOccupancyQuery.java
│   ├── GetRevenueQuery.java
│   ├── ManifestQueryService.java       ← requêtes SQL directes (JPQL ou native)
│   ├── OccupancyQueryService.java
│   └── RevenueQueryService.java
│
└── infrastructure/
    └── web/
        ├── DashboardController.java    ← GET /dashboard/manifests
        │                                  GET /dashboard/occupancy
        │                                  GET /dashboard/revenue
        │                                  GET /dashboard/manifests/export
        └── dto/
            ├── ManifestDto.java
            ├── OccupancyDto.java       ← { scheduleId, totalSeats, bookedSeats, occupancyRate }
            └── RevenueDto.java         ← { date, totalRevenue, ticketCount }
```

**Règle** : `dashboard/` ne modifie jamais de données. Lecture seule. Pas de domaine, pas d'events.

---

## 🗃️ Migrations Liquibase

```
resources/db/changelog/
├── db.changelog-master.xml            ← inclut tout dans l'ordre
└── migrations/
    ├── V01__create_users.xml
    ├── V02__create_outbox_events.xml
    ├── V03__create_audit_logs.xml
    ├── V04__create_agencies.xml
    ├── V05__create_routes.xml
    ├── V06__create_schedules.xml
    ├── V07__create_bookings.xml
    ├── V08__create_payments.xml
    ├── V09__create_payment_attempts.xml
    ├── V10__create_tickets.xml
    ├── V11__create_validation_records.xml
    └── V12__create_consent_logs.xml
```

---

## 🧪 Structure des Tests

```
test/java/cm/jemil/
│
├── architecture/
│   └── HexagonalArchitectureTest.java     ← ArchUnit — règles globales pour TOUS les packages
│
├── auth/
│   ├── domain/
│   │   └── UserTest.java                  ← tests unitaires User aggregate
│   └── e2e/
│       ├── AuthStepDefinitions.java
│       └── CucumberRunner.java
│
├── agency/
│   ├── domain/
│   │   └── AgencyTest.java
│   ├── application/
│   │   └── RegisterAgencyServiceTest.java ← mock du repository
│   └── e2e/
│       └── AgencyStepDefinitions.java
│
├── booking/
│   ├── domain/
│   │   └── BookingTest.java               ← test toutes les transitions de statut
│   ├── application/
│   │   └── CreateBookingServiceTest.java
│   └── e2e/
│       └── BookingStepDefinitions.java
│
├── payment/
│   ├── domain/
│   │   └── PaymentTest.java
│   └── e2e/
│       └── PaymentStepDefinitions.java    ← flow paiement simulé
│
├── ticket/
│   ├── domain/
│   │   └── TicketTest.java
│   └── e2e/
│       └── TicketStepDefinitions.java
│
├── validation/
│   ├── domain/
│   │   └── ValidationTest.java
│   └── e2e/
│       └── ValidationStepDefinitions.java ← offline flow testé
│
└── shared/
    └── CucumberSpringConfiguration.java   ← config Testcontainers partagée par tous les e2e
```

```
test/resources/
├── application-test.yml
└── features/
    ├── auth/
    │   └── auth.feature
    ├── agency/
    │   └── agency-registration.feature
    ├── booking/
    │   └── booking-flow.feature
    ├── payment/
    │   └── payment-flow.feature
    ├── ticket/
    │   └── ticket-issuance.feature
    └── validation/
        └── ticket-validation.feature
```

---

## ⚙️ docker-compose.yml — Ce qui reste

```yaml
services:
  postgres:          # port 5432 — une seule DB, schemas par domaine
  sonarqube:         # port 9000
  postgres-sonar:    # DB dédiée à SonarQube
```

**RabbitMQ retiré** — remplacé par l'Outbox Pattern (table `outbox_events` + scheduler).  
**Redis retiré** — seat locking via `@Version` optimistic locking sur `ScheduleJpaEntity`.  
**Une seule PostgreSQL** — schemas séparés par domaine si besoin, mais une seule instance suffit au MVP.

---

## 📏 Règles d'architecture — ArchUnit (HexagonalArchitectureTest.java)

```
1. domain/ ne dépend de rien d'externe
   → pas de Spring, pas de JPA, pas d'infrastructure

2. domain/ ne dépend pas des autres domaines
   → booking ne connaît pas agency directement
   → communication via events (DomainEvent) uniquement

3. application/ dépend uniquement de domain/
   → pas d'accès direct à persistence/ ou web/

4. infrastructure/ est la seule couche à connaître Spring, JPA, HTTP

5. dashboard/ ne contient pas de logique domaine
   → pas d'Aggregate, pas d'events, pas de ports out

6. shared/ ne contient pas de logique métier
   → pas de référence à agency, booking, payment, etc.
```

---

## 🔗 Communication entre domaines

```
Au lieu d'appeler directement BookingService depuis PaymentService :

PaymentService
    → publie PaymentConfirmedEvent dans outbox_events (via OutboxEventPublisher)
    → OutboxScheduler lit outbox_events toutes les 5s
    → publie via Spring ApplicationEventPublisher
    → IssueTicketService écoute via @EventListener(PaymentConfirmedEvent.class)
    → génère le ticket

Même chose pour :
BookingCreatedEvent     → PaymentService (pour initier le paiement)
TicketIssuedEvent       → (log audit)
TicketValidatedEvent    → DashboardQueryService (stats temps réel)
```

---

## ✅ Checklist avant de coder

- [ ] `JemilApplication.java` créé avec `@SpringBootApplication`
- [ ] `application.yml` configuré (datasource, liquibase, jwt secret)
- [ ] `docker-compose.yml` allégé (PostgreSQL + SonarQube uniquement)
- [ ] `build.gradle.kts` unique avec toutes les dépendances
- [ ] `db.changelog-master.xml` créé avec includes dans l'ordre
- [ ] `HexagonalArchitectureTest.java` créé en premier — avant tout code métier
- [ ] `shared/outbox/` implémenté avant tout autre domaine
- [ ] `shared/exception/` implémenté avant tout autre domaine
- [ ] Coder dans cet ordre : `shared` → `auth` → `agency` → `booking` → `payment` → `ticket` → `validation` → `dashboard`
