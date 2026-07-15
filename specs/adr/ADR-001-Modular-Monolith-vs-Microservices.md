# ADR-001: Choix Architecture Modular Monolith vs Microservices

## Contexte

Nous devons choisir l'architecture de base pour le système JEMIL Transport Ecosystem. Les deux options principales sont :
- **Modular Monolith** : Une seule application avec des modules logiquement séparés
- **Microservices** : Plusieurs services indépendants communiquant via API

## Décision

**Nous choisissons le Modular Monolith.**

## Justification

### Avantages du Modular Monolith pour JEMIL

1. **Simplicité de développement et déploiement**
   - Un seul codebase à gérer
   - Un seul artefact à déployer
   - Pas de complexité de communication inter-services (network latency, retries, etc.)
   - Transaction ACID plus simple à travers les modules

2. **Coût opérationnel réduit**
   - Une seule base de données à gérer (PostgreSQL)
   - Pas de besoin d'orchestration de conteneurs complexe
   - Monitoring et logging centralisé

3. **Adéquation avec l'équipe**
   - Équipe de 1 développeur backend (Solo Developer)
   - Moins de charge cognitive à gérer
   - Plus facile à tester en local

4. **Évolutivité suffisante**
   - Spring Boot 4 + Java 25 offre d'excellentes performances
   - Peut être déployé avec plusieurs instances derrière un load balancer
   - La modularité permet une séparation claire des responsabilités

### Structure du Modular Monolith

```
jemil-backend/
├── auth/           # Module d'authentification
├── agency/         # Module de gestion des agences
├── booking/        # Module de réservation
├── payment/        # Module de paiement
├── ticket/         # Module de billetterie
├── trip/           # Module de gestion des voyages
└── shared/         # Code partagé (Outbox, Events, etc.)
```

### Quand passer aux Microservices ?

La migration vers une architecture microservices sera envisagée lorsque :
- L'équipe grandit à 3+ développeurs backend
- Le trafic dépasse 1000 requêtes/minute
- Les modules ont des besoins de scaling indépendants
- Les technologies divergent entre modules (ex: un module en Node.js)

## Conséquences

- **Positive** : Développement plus rapide, déploiement simplifié, cohérence des données
- **Negative** : Moins de scaling horizontal granulaire, refactoring plus difficile si séparation future nécessaire

## Statut

✅ **Accepté** - Décision validée pour le MVP
