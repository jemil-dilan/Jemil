# JEMIL Backend — Transport Ecosystem

Plateforme intelligente de mobilité interurbaine au Cameroun.

---

## Architecture

Ce projet utilise une **architecture hexagonale (Ports & Adapters)** avec un **monolithe modulaire**
organisé en domaines métier. Chaque module est indépendant et peut être extrait
en microservice autonome sans modifier le domaine.

```
┌─────────────────────────────────────────────────────────┐
│                    Clients (REST / Events)              │
└──────────┬──────────┬──────────┬──────────┬────────────-┘
           │          │          │          │
    ┌──────▼──┐ ┌─────▼───┐ ┌───▼─────┐ ┌──▼────────┐
    │ Agency  │ │ Booking │ │ Payment │ │ Ticketing │
    │ Module  │ │ Module  │ │ Module  │ │  Module   │
    └─────────┘ └─────────┘ └─────────┘ └───────────┘
           │          │          │          │
    ┌──────▼──────────▼──────────▼──────────▼─────────┐
    │           Spring ApplicationEvents (phase 1)    │
    │              → RabbitMQ (phase 2)               │
    └─────────────────────────────────────────────────┘
           │
    ┌──────▼──────────┐
    │  Notification   │
    │    Module       │
    └─────────────────┘
```

### Structure d'un module (Architecture Hexagonale)

```
module-name/
└── src/main/java/cm/jemil/{module}/
    ├── domain/                    ← Cœur métier (0 dépendance externe)
    │   ├── model/                 ← Aggregates, Entities, Value Objects
    │   ├── port/
    │   │   ├── in/                ← Use cases (interfaces appelées par les controllers)
    │   │   └── out/               ← Repositories, Publishers (interfaces impl. par infra)
    │   ├── event/                 ← Événements domaine (immuables)
    │   └── exception/             ← Exceptions métier
    ├── application/
    │   └── usecase/               ← Orchestration (implémente les ports in)
    └── infrastructure/
        ├── persistence/
        │   ├── entity/            ← Entités JPA (séparées du domaine)
        │   ├── repository/        ← Spring Data JPA interfaces
        │   └── adapter/           ← Implémente les ports out du domaine
        ├── web/
        │   ├── controller/        ← REST controllers + exception handlers
        │   └── mapper/            ← Conversion Aggregate ↔ DTO
        └── messaging/             ← Publishers / Consumers d'événements
```

---

## Modules

| Module                | Port | Responsabilité                    |
|-----------------------|------|-----------------------------------|
| `agency-module`       | 8081 | Agences, routes, horaires         |
| `booking-module`      | 8082 | Réservations, passagers           |
| `payment-module`      | 8083 | Paiements MoMo, Stripe            |
| `ticketing-module`    | 8084 | QR codes, validation embarquement |
| `notification-module` | 8085 | SMS, email (Africa's Talking)     |

---

## Prérequis

- Java 25
- Docker & Docker Compose
- (Gradle Wrapper inclus — pas besoin d'installer Gradle)

---

## Démarrage rapide

### 1. Lancer l'environnement Docker
```bash
docker compose up -d
```

Cela démarre :
- RabbitMQ sur `localhost:5672` (UI: `localhost:15672`)
- PostgreSQL pour chaque module (ports 5432-5436)
- SonarQube sur `localhost:9000`

### 2. Lancer un module (ex: agency)
```bash
./gradlew :agency-module:bootRun
```

### 3. Accéder à la documentation API
```
http://localhost:8081/swagger-ui.html
```

---

## Commandes Gradle utiles

```bash
# Compiler tout le projet
./gradlew build

# Lancer tous les tests
./gradlew testAll

# Lancer les tests d'un module spécifique
./gradlew :agency-module:test

# Formatter tout le code (Spotless)
./gradlew formatAll

# Vérifier le formatage sans modifier
./gradlew :agency-module:spotlessCheck

# Générer le rapport de couverture Jacoco
./gradlew :agency-module:jacocoTestReport
# → Rapport HTML : agency-module/build/reports/jacoco/test/html/index.html

# Analyser la qualité avec SonarQube (Docker doit tourner)
./gradlew sonar

# Vérifier l'architecture hexagonale (ArchUnit)
./gradlew :agency-module:test --tests "*.HexagonalArchitectureTest"

# Lancer uniquement les tests e2e Cucumber
./gradlew :agency-module:test --tests "*.CucumberE2ERunner"
```

---

## Outils de qualité inclus

| Outil                   | Rôle                            | Déclenchement                |
|-------------------------|---------------------------------|------------------------------|
| **Spotless + Palantir** | Formatage automatique           | `./gradlew spotlessApply`    |
| **Error Prone**         | Détection bugs à la compilation | Automatique à chaque `build` |
| **Checkstyle**          | Conventions de code             | Automatique à chaque `build` |
| **Jacoco**              | Couverture de code              | Après chaque `test`          |
| **SonarQube**           | Analyse qualité globale         | `./gradlew sonar`            |
| **ArchUnit**            | Vérification architecture       | Pendant les `test`           |
| **Testcontainers**      | Vraie DB en tests               | Pendant les `test`           |
| **Cucumber**            | Tests e2e en Gherkin            | Pendant les `test`           |

---

## Règles d'architecture (imposées par ArchUnit)

Ces règles sont vérifiées **automatiquement** à chaque build :

1. Le **domaine** ne dépend d'aucune infrastructure (pas de Spring, pas de JPA)
2. Les **controllers** appellent les ports (interfaces), jamais les services directement
3. Les **adapters** implémentent les ports sortants du domaine
4. La couche **application** ne connaît pas l'infrastructure

---

## Flux d'un événement domaine (monolithe → microservices)

### Phase 1 — Monolithe modulaire (maintenant)
```
PassengerBooks → BookingService → BookingCreatedEvent
                                        ↓
                               Spring ApplicationEvent
                                        ↓
                               PaymentService.onBookingCreated()
```

### Phase 2 — Microservices (plus tard)
```
PassengerBooks → BookingService → BookingCreatedEvent
                                        ↓
                               RabbitMQ Publisher
                                        ↓
                          [booking.created queue]
                                        ↓
                               Payment Service Consumer
```

**Le domaine et l'application ne changent pas entre les deux phases.**
Seuls les adapters d'infrastructure changent.

---

## Modules — État d'avancement

- [x] Agency Module — Domaine, Application, Infrastructure, Tests, OpenAPI
- [ ] Booking Module — En cours
- [ ] Payment Module — À faire
- [ ] Ticketing Module — À faire
- [ ] Notification Module — À faire

---

## Conventions de commit

```
feat(agency): ajouter endpoint de suspension d'agence
fix(booking): corriger la validation du nombre de places
test(agency): ajouter scénario Cucumber pour agence suspendue
refactor(domain): extraire la validation dans des Value Objects
docs: mettre à jour le README avec les nouvelles commandes
```

---

## Project Status Report (May 2026)

### ✅ Done & Functional
*   **Architectural Foundation:** Multi-module Gradle structure with shared conventions (`buildSrc`). Strict Hexagonal Architecture and DDD patterns applied.
*   **Agency Module (Reference Implementation):**
    *   **Core Domain:** `Agency` Aggregate Root managing status and `Routes`.
    *   **Persistence:** PostgreSQL integration with Liquibase migrations.
    *   **API-First:** Contract-first development using OpenAPI.
    *   **Messaging:** Outbox pattern for `AgencyRegisteredEvent` via RabbitMQ.
*   **Quality Gates:** Full integration of Spotless, Checkstyle, Error Prone, and Jacoco.
*   **Testing:** Comprehensive suite including ArchUnit (architectural rules), Cucumber (BDD/E2E), and Testcontainers (integration).

### 🚀 The Good (Strengths)
*   **High Rigor:** Architectural boundaries are enforced by code (ArchUnit), preventing technical debt in the domain layer.
*   **Scalability:** The project is "Microservices Ready" by design. Moving a module to its own repo would require minimal effort.
*   **Developer Experience:** Standardized commands for formatting, testing, and quality checks.

### ⚠️ Areas for Improvement
*   **Module Imbalance:** Other modules (`booking`, `payment`, `ticketing`, `notification`) are currently skeletons.
*   **Inter-Module Strategy:** Need to finalize the pattern for synchronous vs. asynchronous communication between modules as they grow.
*   **Boilerplate:** The hexagonal layers (Domain ↔ Entity ↔ DTO) add overhead. Ensure MapStruct mappers stay updated to minimize manual work.
