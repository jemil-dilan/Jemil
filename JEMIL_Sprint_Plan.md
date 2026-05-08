# 🚌 JEMIL — Plan de Sprints MVP

> **Projet** : JEMIL Transport Ecosystem — Backend Spring Boot  
> **Architecture** : Modular Monolith + DDD Hexagonal + Event-Driven (Outbox)  
> **Stack** : Java 21 · Spring Boot 3.2 · Gradle Kotlin · PostgreSQL · Liquibase · RabbitMQ · ArchUnit · Cucumber  
> **Équipe** : Solo Backend Developer  
> **Durée totale estimée** : 11 à 14 semaines  
> **Objectif** : Premier billet vendu via JEMIL en conditions réelles

---

## 🗺️ Vue d'ensemble des Sprints

| Sprint | Nom | Durée | Objectif principal |
|--------|-----|-------|--------------------|
| S0 | Fondation & Architecture | 1,5 sem. | Base technique prête, tout le monde peut coder |
| S1 | Agency & Route Management | 2 sem. | Les agences et routes sont gérables via API |
| S2 | Booking Core & Seat Locking | 2 sem. | Réservation fiable avec gestion de disponibilité |
| S3 | Payment MTN MoMo | 3 sem. | Paiements résilients face aux réseaux africains |
| S4 | Ticket & Validation Offline | 2 sem. | Billets QR + validation contrôleur sans réseau |
| S5 | Dashboard Agence | 1,5 sem. | Manager peut piloter son activité via API |
| S6 | Production Readiness | 2 sem. | MVP prêt pour le pilote terrain |

---

## ✅ Definition of Done (globale)

Chaque ticket est **Done** quand :
- [ ] Code compilé sans warning Error Prone ni Checkstyle
- [ ] Tests unitaires écrits et passants (domaine pur, sans Spring)
- [ ] Test d'intégration ou scénario Cucumber e2e couvrant le happy path
- [ ] Couverture Jacoco ≥ 70% sur les classes modifiées
- [ ] ArchUnit : aucune violation des règles hexagonales
- [ ] Spotless : code formaté (`./gradlew spotlessCheck`)
- [ ] OpenAPI spec mise à jour si endpoint ajouté ou modifié
- [ ] PR reviewée (auto-review : relire soi-même avant de merger)

---

## 🏗️ Sprint 0 — Fondation & Architecture

**Durée** : 1,5 semaine  
**Objectif** : Base technique saine. Tout le reste des sprints repose dessus.  
**Definition of Done sprint** : Projet compile · Swagger accessible · Auth JWT fonctionnelle · ArchUnit valide l'architecture · Docker Compose démarre en une commande

---

### 📦 BUILD / SETUP

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S0-01 | `build` | Initialiser le projet Gradle Kotlin multi-module | Structure déjà créée — vérifier que `./gradlew build` passe | 🔴 Critique |
| S0-02 | `build` | Configurer Docker Compose complet | PostgreSQL ×5 + RabbitMQ + SonarQube · Vérifier `docker compose up -d` | 🔴 Critique |
| S0-03 | `build` | Valider les conventions Gradle (buildSrc) | `spotlessCheck` + `checkstyleMain` + `errorprone` passent sur agency-module | 🔴 Critique |
| S0-04 | `build` | Configurer Liquibase master changelog | Un changelog par module · Vérifier migration V1 agency sur DB réelle | 🔴 Critique |

---

### ⚙️ FEAT

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S0-05 | `feat` | JWT Authentication + SecurityConfig | `JwtFilter` · `SecurityConfig` · endpoints `/auth/login` et `/auth/refresh` | 🔴 Critique |
| S0-06 | `feat` | Gestion des rôles | Enum `UserRole` : `PASSENGER`, `AGENCY_MANAGER`, `CONTROLLER`, `ADMIN` | 🔴 Critique |
| S0-07 | `feat` | Entité `User` + migration Liquibase | Table `users` avec `role`, `phone`, `email`, `password_hash` | 🔴 Critique |
| S0-08 | `feat` | Outbox Pattern — infrastructure de base | Table `outbox_events` · `OutboxEvent` entity · `OutboxRepository` · `OutboxScheduler` (`@Scheduled` toutes les 5s) | 🔴 Critique |
| S0-09 | `feat` | Global Exception Handler | `@RestControllerAdvice` · Codes d'erreur standardisés : `DOMAIN_RULE_VIOLATION`, `NOT_FOUND`, `UNAUTHORIZED` | 🟠 Haute |
| S0-10 | `feat` | Configuration OpenAPI / Swagger + CORS | Swagger UI accessible sur `/swagger-ui.html` · CORS configuré pour le dev Android | 🟠 Haute |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S0-11 | `test` | ArchUnit — règles hexagonales agency-module | Tests déjà créés — vérifier qu'ils passent après auth ajoutée | 🔴 Critique |
| S0-12 | `test` | Test JWT : génération + validation + expiration | Tests unitaires purs sur `JwtService` sans Spring | 🟠 Haute |
| S0-13 | `test` | Testcontainers de base — connexion DB | Vérifier que le contexte Spring démarre avec PostgreSQL réel | 🟠 Haute |

---

### 📝 DOCS

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S0-14 | `docs` | ADR-001 : Choix Modular Monolith vs Microservices | Documenter la décision + raison + moment du switch | 🟡 Normale |
| S0-15 | `docs` | ADR-002 : Choix Liquibase vs Flyway | Documenter pourquoi Liquibase (XML structuré, rollback natif) | 🟡 Normale |
| S0-16 | `docs` | ADR-003 : Choix Outbox Pattern vs ApplicationEvent direct | Documenter pourquoi Outbox (garantie at-least-once delivery) | 🟡 Normale |

---

## 🏢 Sprint 1 — Agency & Route Management

**Durée** : 2 semaines  
**Objectif** : Les agences partenaires et leurs routes sont gérables via API. Base de données de référence pour le booking.  
**Definition of Done sprint** : CRUD agences complet · Routes ajoutables · Tests Cucumber passants · OpenAPI spec agency finalisée

---

### ⚙️ FEAT

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S1-01 | `feat` | `Agency` Aggregate Root complet | Déjà créé — revoir `suspend()`, `reactivate()`, `addRoute()` avec les retours terrain | 🔴 Critique |
| S1-02 | `feat` | `Route` Entity + `RouteId` Value Object | Déjà créé — ajouter `Schedule` (horaires de départ) | 🔴 Critique |
| S1-03 | `feat` | `Schedule` Entity — horaires de départ | `departureTime`, `arrivalTime`, `daysOfWeek`, `availableSeats` | 🔴 Critique |
| S1-04 | `feat` | Migration Liquibase V3 — table `schedules` | Colonnes : `route_id`, `departure_time`, `arrival_time`, `days_of_week`, `available_seats` | 🔴 Critique |
| S1-05 | `feat` | API `POST /agencies` | Enregistrer une agence · Rôle requis : `ADMIN` | 🔴 Critique |
| S1-06 | `feat` | API `GET /agencies` + `GET /agencies/{id}` | Liste des agences actives + détail par ID | 🔴 Critique |
| S1-07 | `feat` | API `POST /agencies/{id}/routes` | Ajouter une route à une agence · Rôle requis : `ADMIN` ou `AGENCY_MANAGER` | 🔴 Critique |
| S1-08 | `feat` | API `GET /routes/search` | Recherche `?origin=Douala&destination=Yaoundé&date=2026-05-10` · Retourne les schedules disponibles | 🔴 Critique |
| S1-09 | `feat` | API `PATCH /agencies/{id}/suspend` | Suspendre une agence · Rôle requis : `ADMIN` | 🟠 Haute |
| S1-10 | `feat` | `AgencyRegisteredEvent` via Outbox | Quand agence créée → event dans outbox → publié via Spring ApplicationEvent | 🟠 Haute |

---

### 🔧 REFACTOR

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S1-11 | `refactor` | Compléter `AgencyRepositoryAdapter` avec schedules | Mapping `Schedule` domaine ↔ `ScheduleJpaEntity` | 🔴 Critique |
| S1-12 | `refactor` | Valider spec OpenAPI `agency-api.yml` avec le endpoint search | Ajouter les paramètres de recherche et la réponse `ScheduleResponse` | 🟠 Haute |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S1-13 | `test` | Tests unitaires `Agency` + `Route` + `Schedule` | Déjà commencé — compléter les cas limites | 🔴 Critique |
| S1-14 | `test` | Scénarios Cucumber agency-registration.feature | Déjà créé — vérifier que tous les steps passent avec Testcontainers | 🔴 Critique |
| S1-15 | `test` | Scénario Cucumber : recherche de routes disponibles | `Quand je recherche "Douala" → "Yaoundé" le "2026-05-15"` | 🟠 Haute |
| S1-16 | `test` | Test d'intégration : `AgencyRepositoryAdapter` | Vérifier save/find sur PostgreSQL réel via Testcontainers | 🟠 Haute |

---

## 📋 Sprint 2 — Booking Core & Seat Locking

**Durée** : 2 semaines  
**Objectif** : Un passager peut réserver une place. La place est verrouillée pendant 15 min. Les doublons sont impossibles.  
**Definition of Done sprint** : Réservation créée · Siège verrouillé · Timeout automatique · Concurrence testée

---

### ⚙️ FEAT

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S2-01 | `feat` | `Booking` Aggregate Root | `BookingId` · `PassengerId` · `ScheduleId` · `SeatNumber` · `BookingStatus` enum | 🔴 Critique |
| S2-02 | `feat` | `BookingStatus` enum + transitions métier | `PENDING_PAYMENT` → `PAID` → `BOARDED` · `CANCELLED` · `EXPIRED` · Méthodes : `pay()`, `cancel()`, `expire()`, `board()` | 🔴 Critique |
| S2-03 | `feat` | Seat Locking — verrou optimiste sur `Schedule` | `@Version` sur `ScheduleJpaEntity` · Lever `SeatUnavailableException` si concurrent | 🔴 Critique |
| S2-04 | `feat` | `BookingExpiryScheduler` — expiration automatique | `@Scheduled` toutes les 2 min · Expire les bookings `PENDING_PAYMENT` de plus de 15 min · Libère le siège | 🔴 Critique |
| S2-05 | `feat` | Migration Liquibase V1 booking — table `bookings` | `id`, `passenger_id`, `schedule_id`, `seat_number`, `status`, `expires_at`, `created_at` | 🔴 Critique |
| S2-06 | `feat` | API `POST /bookings` | Créer une réservation · Rôle : `PASSENGER` · Retourne `bookingId` + `expiresAt` | 🔴 Critique |
| S2-07 | `feat` | API `DELETE /bookings/{id}` | Annuler une réservation avant paiement · Rôle : `PASSENGER` (propriétaire uniquement) | 🔴 Critique |
| S2-08 | `feat` | API `GET /bookings/{id}` | Détail d'une réservation + statut courant | 🟠 Haute |
| S2-09 | `feat` | `BookingCreatedEvent` via Outbox | Booking créé → event outbox → sera consommé par Payment en S3 | 🟠 Haute |
| S2-10 | `feat` | `BookingCancelledEvent` via Outbox | Annulation → libération siège via event | 🟠 Haute |

---

### 🔧 REFACTOR

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S2-11 | `refactor` | `booking-module` dépend de `agency-module` | Via `project(":agency-module")` · Vérifier ArchUnit : pas d'accès à l'infrastructure agency | 🔴 Critique |
| S2-12 | `refactor` | Gestion transactions + locking pessimiste si nécessaire | `@Transactional` + `@Lock(PESSIMISTIC_WRITE)` sur `findByScheduleIdAndSeatNumber` | 🟠 Haute |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S2-13 | `test` | Tests unitaires `BookingAggregate` | Toutes les transitions de statut · Cas limites : siège déjà pris, booking expiré | 🔴 Critique |
| S2-14 | `test` | Test de concurrence — double réservation | 2 threads réservent le même siège simultanément → un seul réussit | 🔴 Critique |
| S2-15 | `test` | Test expiration automatique | Booking créé avec `expiresAt` dans le passé → scheduler l'expire → siège libéré | 🔴 Critique |
| S2-16 | `test` | Scénario Cucumber : flow réservation complet | `Quand "Jean Kamga" réserve la place 12 sur "Douala → Yaoundé"` | 🟠 Haute |
| S2-17 | `test` | Scénario Cucumber : siège indisponible | `Alors la réservation échoue avec "SEAT_UNAVAILABLE"` | 🟠 Haute |

---

## 💳 Sprint 3 — Payment MTN MoMo

**Durée** : 3 semaines *(le plus critique — prévoir du buffer)*  
**Objectif** : Paiements fiables et résilients. Un paiement MoMo confirmé déclenche automatiquement la génération du billet.  
**Definition of Done sprint** : Paiement initié · Webhook reçu et vérifié · Booking passe à PAID · Réconciliation automatique · Doublons gérés

---

### ⚙️ FEAT

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S3-01 | `feat` | `Payment` Aggregate + `PaymentStatus` enum | `INITIATED` → `PENDING` → `CONFIRMED` · `FAILED` · `REFUNDED` | 🔴 Critique |
| S3-02 | `feat` | `PaymentAttempt` Entity — log de chaque tentative | `referenceId`, `amount`, `currency`, `momoStatus`, `attemptedAt` | 🔴 Critique |
| S3-03 | `feat` | Migration Liquibase V1 payment — tables `payments` + `payment_attempts` | Index sur `booking_id`, `reference_id` | 🔴 Critique |
| S3-04 | `feat` | Intégration MTN MoMo API — `RequestToPay` | `MomoApiClient` (RestTemplate ou WebClient) · Sandbox d'abord · Gestion timeout réseau | 🔴 Critique |
| S3-05 | `feat` | Idempotency Key — éviter les doublons de paiement | `idempotencyKey` = `bookingId + attemptNumber` · Vérifié avant chaque appel MoMo | 🔴 Critique |
| S3-06 | `feat` | Correlation ID — traçabilité bout en bout | Header `X-Correlation-ID` propagé dans tous les logs et appels MoMo | 🟠 Haute |
| S3-07 | `feat` | API `POST /payments/initiate` | Initie le paiement MoMo · Retourne `paymentId` + `status: PENDING` | 🔴 Critique |
| S3-08 | `feat` | Webhook Controller `POST /payments/webhook/momo` | Reçoit les callbacks MTN · Vérifie la signature · Met à jour le statut | 🔴 Critique |
| S3-09 | `feat` | Vérification signature webhook MoMo | HMAC-SHA256 sur le payload · Rejeter si signature invalide (HTTP 401) | 🔴 Critique |
| S3-10 | `feat` | `PaymentConfirmedEvent` via Outbox | Payment confirmé → event → consommé par Ticketing en S4 | 🔴 Critique |
| S3-11 | `feat` | Job de réconciliation `@Scheduled` | Toutes les 10 min · Vérifie les paiements `PENDING` depuis plus de 5 min · Interroge MoMo API pour le statut | 🟠 Haute |
| S3-12 | `feat` | Gestion des paiements échoués + retry | Max 3 tentatives · Délai exponentiel · Après 3 échecs → booking `CANCELLED` | 🟠 Haute |

---

### 🔧 REFACTOR

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S3-13 | `refactor` | Transition `Booking → PAID` via Outbox Event | `PaymentService` publie `PaymentConfirmedEvent` → `BookingService` écoute et appelle `booking.pay()` | 🔴 Critique |
| S3-14 | `refactor` | Gestion des callbacks MoMo dupliqués | Vérifier si paiement déjà `CONFIRMED` avant de traiter → idempotent | 🔴 Critique |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S3-15 | `test` | Tests unitaires `PaymentAggregate` — transitions statuts | Tous les cas : confirm, fail, refund | 🔴 Critique |
| S3-16 | `test` | Test webhook MoMo — signature valide + invalide | Simuler un callback avec signature correcte et incorrecte | 🔴 Critique |
| S3-17 | `test` | Test réconciliation — paiement bloqué en PENDING | Simuler un timeout MoMo · Vérifier que la réconciliation le résout | 🔴 Critique |
| S3-18 | `test` | Test idempotency — même booking payé deux fois | Deuxième appel retourne le même résultat sans double débit | 🔴 Critique |
| S3-19 | `test` | Scénario Cucumber : flow paiement complet simulé | `Quand "Jean" paie sa réservation via MTN MoMo` · `Alors le booking passe à PAID` | 🟠 Haute |
| S3-20 | `test` | Scénario Cucumber : paiement timeout | `Quand le réseau MoMo ne répond pas` · `Alors la réconciliation résout le statut` | 🟠 Haute |

---

### 📝 DOCS

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S3-21 | `docs` | Documentation technique webhooks MoMo | Payload attendu · Signature · Cas d'erreur · Procédure de rejeu | 🟡 Normale |

---

## 🎫 Sprint 4 — Ticket & Validation Offline

**Durée** : 2 semaines  
**Objectif** : Un billet QR est généré à la confirmation du paiement. Le contrôleur peut le valider sans réseau stable.  
**Definition of Done sprint** : QR généré · Code SMS envoyé · Validation offline fonctionnelle · Synchronisation différée testée

---

### ⚙️ FEAT

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S4-01 | `feat` | `Ticket` Entity + `TicketStatus` enum | `ISSUED` · `VALIDATED` · `EXPIRED` · `REFUNDED` | 🔴 Critique |
| S4-02 | `feat` | Migration Liquibase V1 ticketing — table `tickets` | `booking_id`, `qr_token`, `sms_code`, `status`, `issued_at`, `validated_at` | 🔴 Critique |
| S4-03 | `feat` | Génération QR Code — JWT signé (ZXing) | Payload : `ticketId`, `bookingId`, `passengerId`, `scheduleId`, `exp` · Signé HMAC-SHA256 | 🔴 Critique |
| S4-04 | `feat` | Génération code SMS de secours | 6 chiffres aléatoires sécurisés · Hashé en base · Valide 24h | 🔴 Critique |
| S4-05 | `feat` | Envoi SMS via Africa's Talking | `SmsService` · Template : `"JEMIL - Code: 482910 - Bus Douala>YDE 08h00"` | 🔴 Critique |
| S4-06 | `feat` | `TicketIssuedEvent` via Outbox | `PaymentConfirmedEvent` consommé → ticket généré → `TicketIssuedEvent` publié | 🔴 Critique |
| S4-07 | `feat` | API `GET /tickets/{id}/qr` | Retourne l'image QR (PNG base64) · Rôle : `PASSENGER` (propriétaire) | 🔴 Critique |
| S4-08 | `feat` | API `GET /manifests/download?scheduleId={id}` | Télécharge le manifeste complet du voyage (liste passagers + codes) · Rôle : `CONTROLLER` | 🔴 Critique |
| S4-09 | `feat` | API `POST /tickets/validate` | Valide un QR ou un code SMS · Rôle : `CONTROLLER` · Fonctionne offline avec manifeste en cache | 🔴 Critique |
| S4-10 | `feat` | Stratégie offline — validation sans réseau | Manifeste téléchargé → stocké localement (Android) · Validation par code SMS · Sync différée des validations | 🔴 Critique |
| S4-11 | `feat` | `TicketValidatedEvent` via Outbox | Validation → event → consommé par Dashboard (S5) pour stats temps réel | 🟠 Haute |
| S4-12 | `feat` | Détection doublon QR — un ticket = une validation | Si token déjà `VALIDATED` → HTTP 409 + message `"Billet déjà utilisé"` | 🔴 Critique |

---

### 🔧 REFACTOR

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S4-13 | `refactor` | Stratégie résolution conflits offline | `first-write-wins` + champ `version` sur la validation · Sync au retour réseau | 🟠 Haute |
| S4-14 | `refactor` | `ticketing-module` consomme `PaymentConfirmedEvent` | Via Spring ApplicationEvent · Pas d'appel direct au payment-module | 🔴 Critique |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S4-15 | `test` | Tests unitaires `Ticket` — génération + transitions | QR valide · SMS valide · Doublon détecté | 🔴 Critique |
| S4-16 | `test` | Test JWT QR — token expiré rejeté | Token avec `exp` dans le passé → HTTP 401 | 🔴 Critique |
| S4-17 | `test` | Test validation offline simulée | Manifeste téléchargé · Réseau coupé simulé · Validation par SMS · Sync au retour | 🔴 Critique |
| S4-18 | `test` | Test doublon QR — même ticket validé deux fois | Deuxième validation → HTTP 409 | 🔴 Critique |
| S4-19 | `test` | Scénario Cucumber : flow ticket complet | `Quand le paiement est confirmé` → `Alors "Jean" reçoit un QR et un SMS` | 🟠 Haute |
| S4-20 | `test` | Scénario Cucumber : contrôleur valide le billet | `Quand le contrôleur scanne le QR de "Jean"` → `Alors le billet est marqué VALIDATED` | 🟠 Haute |

---

## 📊 Sprint 5 — Dashboard Agence

**Durée** : 1,5 semaine  
**Objectif** : Le manager d'agence peut piloter son activité quotidienne via les APIs.  
**Definition of Done sprint** : Manifeste du jour accessible · Taux d'occupation calculé · CA journalier retourné · Export fonctionnel

---

### ⚙️ FEAT

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S5-01 | `feat` | API `GET /dashboard/manifests?scheduleId={id}&date={date}` | Liste complète des passagers d'un voyage · Rôle : `AGENCY_MANAGER` | 🔴 Critique |
| S5-02 | `feat` | API `GET /dashboard/occupancy?routeId={id}&date={date}` | Taux d'occupation en % · Places vendues / places totales | 🔴 Critique |
| S5-03 | `feat` | API `GET /dashboard/revenue?agencyId={id}&date={date}` | CA journalier en FCFA · Nb billets vendus | 🔴 Critique |
| S5-04 | `feat` | API `GET /dashboard/manifests/export` | Export JSON ou CSV du manifeste · Header `Accept: text/csv` ou `application/json` | 🟠 Haute |
| S5-05 | `feat` | Notifications SMS confirmation départ | SMS auto envoyé au passager 30 min avant le départ · `@Scheduled` | 🟠 Haute |
| S5-06 | `feat` | Read Models optimisés — CQRS léger | Vues SQL dédiées aux queries dashboard · Pas de logique dans les requêtes JPA | 🟡 Normale |

---

### 🔧 REFACTOR

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S5-07 | `refactor` | `TicketValidatedEvent` consommé par dashboard | Mise à jour en temps réel du taux d'occupation via event | 🟠 Haute |
| S5-08 | `refactor` | Sécuriser les endpoints dashboard par agence | Un manager ne voit que les données de SON agence · Vérification dans le use case | 🔴 Critique |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S5-09 | `test` | Tests d'intégration dashboard — données réelles | Scénario complet : agence → route → schedule → booking → paiement → dashboard | 🔴 Critique |
| S5-10 | `test` | Scénario Cucumber : manager consulte son manifeste | `Quand le manager de "Global Voyages" consulte le voyage de 08h00` | 🟠 Haute |
| S5-11 | `test` | Test isolation — manager ne voit pas l'agence concurrente | Manager de "Global Voyages" ne peut pas voir les données de "Trésor Voyages" | 🔴 Critique |

---

## 🚀 Sprint 6 — Production Readiness

**Durée** : 2 semaines  
**Objectif** : MVP prêt pour le pilote terrain avec 1-2 agences réelles.  
**Definition of Done sprint** : Couverture ≥ 75% · Rate limiting actif · Audit log critique · Docker Compose final · Postman Collection complète · Guide déploiement écrit

---

### 🔒 SECURITY / REFACTOR

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S6-01 | `security` | Rate limiting — protection APIs critiques | Bucket4j ou Spring rate limiter · `/payments/initiate` : max 5/min · `/auth/login` : max 10/min | 🔴 Critique |
| S6-02 | `security` | Audit logging — actions critiques | `AuditLog` entity · Logger : login, paiement initié, billet validé, agence suspendue | 🔴 Critique |
| S6-03 | `security` | Validation renforcée des inputs | `@NotNull`, `@Size`, `@Pattern` sur tous les DTOs · Test injection SQL basique | 🔴 Critique |
| S6-04 | `security` | Chiffrement données sensibles en base | Numéros de téléphone passagers chiffrés (AES-256) · `@Convert` JPA | 🟠 Haute |
| S6-05 | `refactor` | Revue globale technical debt | Revoir tous les `TODO` et `FIXME` laissés pendant les sprints | 🟠 Haute |
| S6-06 | `refactor` | Optimisations performance — requêtes N+1 | Activer `spring.jpa.show-sql=true` en staging · Identifier et corriger les N+1 | 🟠 Haute |

---

### ⚖️ COMPLIANCE

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S6-07 | `compliance` | Logs de consentement — préparation loi 2024/017 | Enregistrer : qui a consenti, quand, à quoi · Table `consent_logs` | 🔴 Critique |
| S6-08 | `compliance` | Politique RGPD-Cameroun — endpoint suppression données | `DELETE /users/me` · Anonymisation des données passager | 🟠 Haute |

---

### 🧪 TEST

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S6-09 | `test` | Couverture Jacoco ≥ 75% sur tous les modules core | Identifier les gaps · Ajouter les tests manquants | 🔴 Critique |
| S6-10 | `test` | ArchUnit complet — tous les modules | Vérifier que les règles hexagonales sont respectées sur booking, payment, ticketing | 🔴 Critique |
| S6-11 | `test` | Test e2e bout en bout — flow complet | `Recherche → Réservation → Paiement → QR → Validation contrôleur → Dashboard` | 🔴 Critique |
| S6-12 | `test` | Test de charge basique | 50 requêtes simultanées sur `/bookings` · Vérifier qu'aucun siège n'est double-réservé | 🟠 Haute |

---

### 🛠️ BUILD / DEVOPS

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S6-13 | `build` | Docker Compose final — production-like | Profiles `dev` / `staging` · Variables d'environnement externalisées dans `.env` | 🔴 Critique |
| S6-14 | `build` | Configuration variables d'environnement | Aucune valeur sensible dans le code · Tout via `System.getenv()` ou `application-prod.yml` (gitignored) | 🔴 Critique |
| S6-15 | `build` | CI basique — GitHub Actions | `./gradlew build test` sur chaque push · Badge de build dans le README | 🟠 Haute |

---

### 📝 DOCS

| ID | Type | Tâche | Détail | Priorité |
|----|------|-------|--------|----------|
| S6-16 | `docs` | Swagger complet — tous les endpoints documentés | Description · Exemples de requête/réponse · Codes d'erreur | 🔴 Critique |
| S6-17 | `docs` | Postman Collection finale | Collection importable · Variables d'environnement · Flow complet documenté | 🔴 Critique |
| S6-18 | `docs` | Guide de déploiement | Prérequis · Commandes pas à pas · Vérifications post-déploiement | 🔴 Critique |

---

## 📈 Récapitulatif

| Sprint | Tickets Critiques 🔴 | Tickets Hauts 🟠 | Tickets Normaux 🟡 | Total |
|--------|---------------------|-----------------|-------------------|-------|
| S0 — Fondation | 8 | 4 | 3 | **15** |
| S1 — Agency | 9 | 4 | 0 | **13** |
| S2 — Booking | 10 | 4 | 0 | **14** |
| S3 — Payment | 10 | 5 | 1 | **16** |
| S4 — Ticketing | 11 | 3 | 0 | **14** |
| S5 — Dashboard | 5 | 4 | 1 | **10** |
| S6 — Production | 12 | 6 | 0 | **18** |
| **TOTAL** | **65** | **30** | **5** | **100** |

---

## 📋 Règles de travail

| Règle | Description |
|-------|-------------|
| **Happy Path d'abord** | Toujours terminer le cas nominal avant les cas limites |
| **Pas de feature creep** | Toute idée hors MVP → Backlog Phase 2. Pas de débat. |
| **Fin de sprint** | Démo interne (tester le flow manuellement) + commit tagué `sprint-X-done` |
| **Blocage** | Si bloqué plus de 2h → noter le problème + passer à la tâche suivante |
| **Suivi** | GitHub Projects ou Notion · Colonnes : `Todo` → `In Progress` → `Review` → `Done` |

---

## 🔖 Légende des priorités

| Icône | Niveau | Signification |
|-------|--------|---------------|
| 🔴 | Critique | Sprint ne peut pas se terminer sans ce ticket |
| 🟠 | Haute | Doit être fait dans ce sprint si possible |
| 🟡 | Normale | Peut glisser au sprint suivant sans impact |
