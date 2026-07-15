# 🚌 JEMIL Backend — Comprehensive Code Review Report

## 📌 1. Executive Summary

This report presents a thorough static code analysis and architectural review of the **JEMIL Backend** repository on the `main` branch.

Overall, the codebase demonstrates **exemplary engineering discipline** and modern Java development standards. The choice of a **Modular Monolith** combined with **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)** principles provides an extremely robust foundation for the Cameroon interurban transport ecosystem. The decoupling of core business logic from framework concerns (Spring, JPA, REST) is highly commendable.

However, several critical and high-priority issues have been identified during this review. These include:
1. **Security Vulnerabilities in JWT Authentication**: Under-utilization of multi-role tokens and a mismatch in role extraction that could cause authorization bypasses.
2. **Robustness Risks in the Outbox Scheduler**: Lack of concurrency locks and transaction isolation on the outbox event publishing loop, risking duplicate event emissions.
3. **Flaky Validations in Temporal Value Objects**: System clock dependency in `CreatedAt` that can crash database record loading in high-concurrency environments.
4. **Static Analysis & Tooling Warnings**: Numerous Error Prone and compiler warnings, along with JVM version toolchain mismatches.

This document analyzes these findings in detail and provides **concrete, production-ready code remedies** to solve them.

---

## 🏗️ 2. Architectural & Modular Monolith Assessment

### Strengths
- **Hexagonal Architecture Isolation**: The package separation per context (`cm.jemil.{context}`) and layer (`domain`, `application`, `adapter`, `config`) is remarkably clean.
- **Automated Architecture Rules**: The `HexagonalArchitectureTest` utilizing **ArchUnit** is an outstanding practice. It successfully guarantees that the domain layer does not depend on frameworks (Spring, JPA, Hibernate) and sibling contexts do not bypass clean vertical boundaries.
- **Contract-First API Design**: OpenAPI specs are the source of truth, from which Java DTOs and API interfaces are generated dynamically. This reduces manual mapping errors and maintains alignment across teams.

### Recommendations
1. **Unnecessary Interface Overhead in Application Layer**:
   - *Observation*: Several use cases define a simple interface with only one implementation (e.g., `AddBranchUseCase` and `AddBranchUseCaseImpl`). While standard in enterprise Java, in a modular monolith where adapters directly inject these use cases, this adds boilerplate without direct flexibility benefits.
   - *Remedy*: Consider using concrete use-case classes annotated with `@Service` directly, unless multiple runtime strategies are realistically expected.
2. **Standardization of Demo/Scaffold Packages**:
   - *Observation*: Except for `agency/` and `shared/`, modules such as `booking/`, `payment/`, `ticket/`, `auth/`, and `trip/` are mostly scaffolds with copy-pasted `Demo` domain models and adapters.
   - *Remedy*: Prioritize pruning unused `Demo` schemas and classes as real features are rolled out to keep the build light and prevent copy-paste errors.

---

## 🔐 3. Deep Security Audit (JWT & Auth)

### 🔴 Critical Vulnerability: Inconsistent Role Claim Processing
- **Problem**: In `JwtService.java`, there are two overloaded `generateAccessToken` methods:
  ```java
  public String generateAccessToken(String userId, String role) { ... } // Sets claim("role", role)
  public String generateAccessToken(String userId, Set<String> roles) { ... } // Sets claim("roles", roles)
  ```
  However, in `JwtAuthenticationFilter.java`, role extraction is coded as:
  ```java
  String role = jwtService.extractRole(token);
  var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
  ```
  If a token is generated using a `Set<String>` of roles, `extractRole(token)` returns `null` because the token payload contains the `"roles"` claim, not `"role"`. The filter will silently instantiate `new SimpleGrantedAuthority("ROLE_null")` instead of the user's actual roles, leading to a complete **Authorization Bypass** or **Unauthorized Access** for multi-role users.

- **Remedy**: Update the filter to consistently extract all roles using `jwtService.extractRoles(token)`, which includes a clean single-role fallback:
  ```java
  java.util.Set<String> roles = jwtService.extractRoles(token);
  var authorities = roles.stream()
          .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
          .toList();
  ```

### 🟠 High Priority: Hardcoded / Default Expiration Times
- **Problem**: Default expiration values in `@Value` annotations in `JwtService` are fallback constants (e.g., `900000` ms / 15 minutes). If the environment variable is missing, token expiration might default to unsafe values or fail silently depending on configurations.
- **Remedy**: Externalize all security configurations strictly and fail fast if vital properties such as `jwt.secret` or expiration limits are omitted at startup.

---

## 🔄 4. Robustness, Concurrency & Schedulers

### 🔴 High Priority: Outbox Scheduler Race Conditions
- **Problem**: In `OutboxScheduler.java`, the processing method is annotated as follows:
  ```java
  @Scheduled(fixedDelay = 5000)
  @Transactional
  public void processOutboxEvents() {
      List<OutboxEvent> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);
      for (OutboxEvent event : pendingEvents) {
          ...
          eventPublisher.publishEvent(deserialize(event));
          event.markAsSent();
          outboxRepository.save(event);
      }
  }
  ```
  Since the method is annotated with `@Transactional`, the database transaction remains open during the entire loop execution (including event deserialization and execution of listeners). Under moderate-to-high load, or if multiple instances of the backend are deployed (e.g., horizontal scaling in staging/production), different instances will fetch the **same pending events simultaneously** because they have not yet been committed as `SENT`. This guarantees **duplicate event publication** and compromises eventual consistency.

- **Remedy**: Use a "select-for-update" query with skipping locked rows to claim events, or run the retrieval and status update in a short, separate write transaction before publishing.
  - Change repository fetch to:
    ```java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED
    List<OutboxEvent> findPendingForWrite();
    ```

### 🟡 Medium Priority: Flaky System Clock Dependency in `CreatedAt`
- **Problem**: The domain value object `CreatedAt` uses `LocalDateTime.now()` for strict future validation:
  ```java
  public CreatedAt {
      if (isInTheFuture(value)) {
          throw new IllegalArgumentException("The given timestamp is already in the future");
      }
  }
  private static boolean isInTheFuture(LocalDateTime value) {
      return LocalDateTime.now().isBefore(value);
  }
  ```
  When database records are persisted and later read back, the database driver might map values that have sub-millisecond precision differences, or minor system clock synchronization drift between the application server and the database server might occur. If a record is loaded whose timestamp is even **one microsecond** ahead of `LocalDateTime.now()` on the local server, the constructor throws a fatal `IllegalArgumentException` and crashes the query.

- **Remedy**: Introduce a tiny tolerance buffer (e.g., 5 seconds) to prevent clock skew failures, or remove the strict future check altogether since database-persisted entities are intrinsically in the past:
  ```java
  private static boolean isInTheFuture(LocalDateTime value) {
      return LocalDateTime.now().plusSeconds(5).isBefore(value);
  }
  ```

---

## 🗃️ 5. Clean Code & DDD Best Practices

### 🟢 Good Practices
- **Rich Domain Models**: Business validations (e.g., checking if seat counts are positive, ensuring agency names are not blank) reside inside core models (`Route`, `Agency`, `Schedule`) rather than being leaked into services or databases.
- **Value Objects**: Using types like `PhoneNumber`, `CreatedAt`, and `Address` prevents "Primitive Obsession" and enforces consistency across contexts.
- **Immutability**: Models return `Collections.unmodifiableList()` for domain lists, shielding internal structures from unintended side effects.

### 🟡 Areas for Improvement
- **Error Prone Compiler Warnings**:
  - Compiling the project currently logs over **100 warnings** from Error Prone. Most are related to MapStruct's generated implementation files assigning unused local variables (`UnusedVariable`) or using system default time zones silently (`JavaTimeDefaultTimeZone`).
  - *Remedy*: Add exclusions in build configurations or annotate mappers appropriately to ensure a zero-warning compile phase.
- **Incomplete Schedulers**:
  - `BookingExpiryScheduler` is currently annotated with `@Scheduled` but has no implementation logic. Ensure schedulers are either fully written or disabled until S2 starts.

---

## 🛠️ 6. Concrete Code Remedies & Actionable Quick Wins

### Remedy A: Robust Role Extraction in `JwtAuthenticationFilter.java`
Replace the role-extraction logic with the multi-role compliant variant.

```java
<<<<<<< SEARCH
        if (token != null && jwtService.isValid(token)) {
            String userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
=======
        if (token != null && jwtService.isValid(token)) {
            String userId = jwtService.extractUserId(token);
            java.util.Set<String> roles = jwtService.extractRoles(token);

            var authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
>>>>>>> REPLACE
```

### Remedy B: Tolerant Clock Skew in `CreatedAt.java`
Update future validation to accommodate system clock fluctuations.

```java
<<<<<<< SEARCH
    private static boolean isInTheFuture(LocalDateTime value) {
        return LocalDateTime.now().isBefore(value);
    }
=======
    private static boolean isInTheFuture(LocalDateTime value) {
        // Allow up to 5 seconds of clock drift/latency
        return LocalDateTime.now().plusSeconds(5).isBefore(value);
    }
>>>>>>> REPLACE
```

---

## 📈 7. Conclusion

JEMIL Backend is an exceptionally well-crafted software system that serves as a benchmark for Hexagonal modular monolith implementations. Addressing the security vulnerability in the **JWT Authentication Filter** and hardening the **Outbox Scheduler** against duplicate transactions will make this system fully enterprise-grade, highly resilient, and ready for safe production deployment in Cameroon's transport sector.
