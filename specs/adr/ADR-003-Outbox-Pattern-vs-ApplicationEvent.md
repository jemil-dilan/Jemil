# ADR-003: Choix Outbox Pattern vs ApplicationEvent direct

## Contexte

Nous devons choisir comment gérer les événements de domaine (Domain Events) dans l'architecture JEMIL. Les deux approches principales sont :
- **Outbox Pattern** : Stocker les événements dans une table avant publication
- **Spring ApplicationEvent** : Publier directement les événements via le contexte Spring

## Décision

**Nous choisissons le Outbox Pattern.**

## Justification

### Comparaison Outbox Pattern vs ApplicationEvent

| Critère           | Outbox Pattern              | Spring ApplicationEvent            |
|-------------------|-----------------------------|------------------------------------|
| **Fiabilité**     | ✅ At-least-once delivery   | ❌ At-most-once (peut être perdu)  |
| **Persistance**   | ✅ Événements stockés en DB | ❌ Événements en mémoire           |
| **Résilience**    | ✅ Survit aux crashes       | ❌ Perdu si crash avant traitement |
| **Ordre garanti** | ✅ Par timestamp            | ❌ Dépend de l'ordre d'exécution   |
| **Complexité**    | ❌ Plus complexe            | ✅ Simple et direct                |
| **Performance**   | ❌ Latence DB               | ✅ Temps réel                      |

### Avantages du Outbox Pattern pour JEMIL

1. **Garantie de livraison (At-least-once)**
   - Les événements sont persistés dans la base de données avant d'être publiés
   - Si l'application crash, les événements sont relus au redémarrage
   - Aucun événement perdu même en cas d'échec

2. **Consistance transactionnelle**
   - L'écriture de l'événement et la modification du domaine sont dans la même transaction
   - Si la transaction échoue, l'événement n'est pas créé
   - Cohérence entre l'état du domaine et les événements publiés

3. **Résilience face aux échecs**
   - Un scheduler vérifie périodiquement les événements non traités
   - Gestion des retries automatiques
   - Détection des événements expirés (max retry)

4. **Audit et traçabilité**
   - Table `outbox_events` avec statut (PENDING, SENT, FAILED)
   - Historique complet des événements
   - Possibilité de rejouer des événements si nécessaire

5. **Intégration avec l'event-driven architecture**
   - Permet une publication asynchrone (via RabbitMQ)
   - Découplage entre les modules producteurs et consommateurs
   - Meilleure évolutivité

### Implémentation dans JEMIL

#### Structure de la table `outbox_events`

```sql
CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,  -- PENDING, SENT, FAILED
    processed_at TIMESTAMP
);
```

#### Flux de traitement

```
1. Module produit un événement (ex: AgencyRegisteredEvent)
2. L'événement est sérialisé en JSON et stocké dans outbox_events
3. Le OutboxScheduler (toutes les 5 secondes) lit les événements PENDING
4. L'événement est désérialisé et publié via Spring ApplicationEvent
5. Si succès : statut → SENT
6. Si échec : statut → FAILED (avec retry)
7. Si expiré : statut → FAILED (max retry atteint)
```

#### Classes principales

- `OutboxEvent` : Entité JPA pour la table outbox_events
- `OutboxRepository` : Repository Spring Data JPA
- `OutboxEventPublisher` : Service pour publier dans l'outbox
- `OutboxScheduler` : Component @Scheduled pour traiter les événements

### Conséquences

- **Positive** : Fiabilité, résilience, traçabilité, cohérence transactionnelle
- **Negative** : Complexité accrue, latence, maintenance de la table outbox

## Statut

✅ **Accepté** - Décision validée pour le MVP
