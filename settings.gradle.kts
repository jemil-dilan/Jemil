rootProject.name = "jemil-backend"

// Gradle auto-loads the default `libs` catalog from `gradle/libs.versions.toml`.
// Defining it manually here causes the catalog to call `from(...)` twice.

// ── Déclaration de tous les modules ─────────────────────────────────────────
// C'est ici que Gradle "sait" que le projet est multi-module.
// Chaque include() = un appartement dans la résidence.
include(
    "agency-module",      // gestion des agences, routes, horaires
    "booking-module",     // réservations passagers
    "payment-module",     // paiements MoMo / Stripe
    "ticketing-module",   // génération et validation QR
    "notification-module" // SMS / email / push
)
