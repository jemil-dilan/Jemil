# ADR-002: Choix Liquibase vs Flyway pour la gestion des migrations

## Contexte

Nous devons choisir un outil de gestion des migrations de base de données pour le projet JEMIL. Les deux options principales sont :
- **Liquibase** : Utilise des fichiers XML, YAML, JSON ou SQL
- **Flyway** : Utilise principalement des fichiers SQL

## Décision

**Nous choisissons Liquibase.**

## Justification

### Comparaison Liquibase vs Flyway

| Critère                      | Liquibase                           | Flyway                        |
|------------------------------|-------------------------------------|-------------------------------|
| **Format des migrations**    | XML, YAML, JSON, SQL                | SQL uniquement                |
| **Rollback natif**           | ✅ Oui                              | ❌ Non (nécessite SQL manuel) |
| **Support des bases**        | Multi-SGBD                          | Multi-SGBD                    |
| **Gestion des refactorings** | ✅ changeSets avec checksum         | ❌ Basé sur le nom de fichier |
| **Flexibilité**              | ✅ Très flexible (changelog master) | ✅ Simple et conventionnel    |
| **Intégration Spring Boot**  | ✅ Excellente                       | ✅ Excellente                 |

### Avantages de Liquibase pour JEMIL

1. **XML structuré**
   - Meilleure organisation visuelle des migrations
   - Validation du schéma via XSD
   - Support des commentaires dans le XML

2. **Rollback natif**
   - Chaque changeSet peut avoir son propre rollback
   - Pas besoin d'écrire manuellement le SQL inverse
   - Plus sûr pour les environnements de production

3. **Changelog master**
   - Permet d'inclure plusieurs fichiers de changelog
   - Meilleure modularité (un changelog par module)
   - Exemple : `v1.xml` pour agency, `v2.xml` pour booking, etc.

4. **Support des préconditions**
   - Vérification avant application (ex: colonne existe, table existe)
   - Moins de risques d'erreurs

5. **Historique complet**
   - Table `DATABASECHANGELOG` et `DATABASECHANGELOGLOCK`
   - Suivi précis de ce qui a été appliqué

### Exemple de structure

```
src/main/resources/db/changelog/
├── db.changelog-master.xml          # Fichier maître
├── v0.xml                           # Migrations initiales
├── v1.xml                           # Module Agency
├── v2.xml                           # Module Booking
├── v3.xml                           # Module Payment
└── v4.xml                           # Module Ticket
```

### Configuration Spring Boot

```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    enabled: true
```

## Conséquences

- **Positive** : Migrations bien structurées, rollback facile, meilleure maintenance
- **Negative** : Courbe d'apprentissage légèrement plus élevée que Flyway

## Statut

✅ **Accepté** - Décision validée pour le MVP
