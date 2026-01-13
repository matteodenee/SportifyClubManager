# Rapport de Management - Sprint 2

## Résumé Exécutif

Ce rapport présente les résultats du Sprint 2 du projet SportifyClubManager. Ce sprint a porté sur l'implémentation de plusieurs use cases clés avec une répartition des tâches entre les membres de l'équipe.

---

## 1. Répartition des Tâches du Sprint 2

| Use Case | Diagrammes | Code | Responsable Diagrammes | Responsable Code |
|----------|-----------|------|------------------------|------------------|
| **Training Management** | ✅ | 🔄 | Matteo | Ayoub |
| **Stats Management** | ✅ | ✅ | Ayoub | Rasim |
| **Match Management** | ✅ | ✅ | Théo | Matteo |
| **Team Manager** | ✅ | 🔄 | Rasim | Théo |

**Légende** : ✅ Complété | 🔄 En cours/Placeholder

---

## 2. Analyse Quantitative

### 2.1 Statistiques du Code - Stats Management

| Métrique | Valeur |
|----------|--------|
| **Nombre de classes créées** | 6 |
| **Nombre de fichiers Java** | 6 |
| **Lignes de code estimées** | ~400 LOC |
| **Méthodes publiques** | 15+ |
| **Tables de base de données** | 1 (`small_events`) |

**Fichiers créés pour Stats Management** :
- `StatManager.java` (82 lignes) - Couche métier
- `StatController.java` (79 lignes) - Contrôleur
- `StatFacade.java` (46 lignes) - Facade
- `StatDAO.java` (44 lignes) - Interface DAO
- `PostgresStatDAO.java` (145 lignes) - Implémentation DAO
- `StatFrame.java` (110 lignes) - Interface graphique JavaFX
- `Statistique.java` (28 lignes) - Modèle de données
- `SmallEvent.java` (49 lignes) - Modèle d'événement

### 2.2 Statistiques du Code - Match Management

| Métrique | Valeur |
|----------|--------|
| **Nombre de classes créées** | 12 |
| **Nombre de fichiers Java** | 12 |
| **Lignes de code estimées** | ~800 LOC |
| **Méthodes publiques** | 35+ |
| **Tables de base de données** | 3 (`matchs`, `match_requests`, `match_composition`) |

**Fichiers créés pour Match Management** :
- Modèles : `Match.java`, `MatchRequest.java`, `Composition.java`, `RoleAssignment.java`
- Managers : `MatchManager.java`, `MatchRequestManager.java`, `CompositionManager.java`
- DAOs : `MatchDAO.java`, `PostgresMatchDAO.java`, `MatchRequestDAO.java`, `PostgresMatchRequestDAO.java`, `CompositionDAO.java`, `PostgresCompositionDAO.java`
- Facades : `MatchFacade.java`, `MatchRequestFacade.java`, `CompositionFacade.java`
- Controllers : `MatchController.java`, `MatchRequestController.java`, `CompositionController.java`
- Enums : `MatchStatus.java`, `MatchRequestStatus.java`
- UI : Intégré dans `AdminDashboardFrame.java`, `CoachDashboardFrame.java`

### 2.3 Couverture des Use Cases

| Use Case | Fonctionnalités prévues | Fonctionnalités implémentées | Couverture |
|----------|------------------------|------------------------------|------------|
| **Stats Management** | 5 | 5 | **100%** |
| **Match Management** | 8 | 8 | **100%** |
| **Training Management** | 4 | 0 | **0%** (Placeholder) |
| **Team Manager** | 6 | 2 | **33%** (Partiel) |

---

## 3. Détails Techniques par Use Case

### 3.1 Stats Management (Ayoub - diag, Rasim - code)

#### Architecture implémentée

```
┌─────────────────┐
│   StatFrame     │  ← Interface JavaFX (PieChart, KPIs)
│   (UI Layer)    │
└────────┬────────┘
         │
┌────────▼────────┐
│  StatController │  ← Gestion des requêtes UI
│  (Controller)   │
└────────┬────────┘
         │
┌────────▼────────┐
│   StatFacade    │  ← Simplification des appels
│   (Facade)      │
└────────┬────────┘
         │
┌────────▼────────┐
│   StatManager   │  ← Logique métier (calculs, ratios)
│   (Business)    │
└────────┬────────┘
         │
┌────────▼────────┐
│ PostgresStatDAO │  ← Persistance PostgreSQL
│   (DAO)         │
└────────┬────────┘
         │
┌────────▼────────┐
│   small_events  │  ← Table de la base de données
│   (Database)    │
└─────────────────┘
```

#### Fonctionnalités implémentées

| ID | Fonctionnalité | Description | Méthode |
|----|----------------|-------------|---------|
| F1 | Calcul de performance | Calcul des statistiques d'équipe (buts, victoires, ratio) | `calculateTeamPerformance()` |
| F2 | Données agrégées | Distribution par type d'événement pour graphiques | `getAggregatedStats()` |
| F3 | Top Performers | Classement des meilleurs joueurs | `getRanking()` |
| F4 | Tendances | Analyse de l'évolution temporelle | `getTrends()` |
| F5 | Taux de participation | Calcul de présence des joueurs | `getPlayerParticipationRate()` |

#### Interface utilisateur

- **PieChart** : Répartition des actions (buts, fautes, arrêts)
- **KPI Cards** : Affichage des ratios (WinRate, GoalsPerMatch)
- **Filtres** : Sélection de période (Saison 2024, Dernier Mois, Global)

#### Schéma de base de données

```sql
CREATE TABLE small_events (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50),           -- GOAL, MATCH, VICTOIRE, PARTICIPATION
    description TEXT,
    team_id INT REFERENCES clubs(clubid),
    player_id VARCHAR(50) REFERENCES users(id),
    period VARCHAR(50),         -- "Saison 2024"
    event_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3.2 Match Management (Théo - diag, Matteo - code)

#### Fonctionnalités implémentées

| ID | Fonctionnalité | Acteur | Description |
|----|----------------|--------|-------------|
| M1 | Planifier un match | Admin | Création d'un match avec équipes, date, lieu, arbitre |
| M2 | Modifier un match | Admin | Mise à jour des informations et du score |
| M3 | Demander un match | Coach | Soumission d'une demande au nom du club |
| M4 | Valider une demande | Admin | Approbation créant automatiquement le match |
| M5 | Refuser une demande | Admin | Rejet avec passage en statut REJECTED |
| M6 | Composer une équipe | Coach | Attribution des joueurs aux rôles |
| M7 | Valider composition | Système | Vérification du nombre de joueurs et rôles |
| M8 | Générer stats | Système | Création automatique d'événements à la fin du match |

#### Workflow principal

```
┌───────────┐      ┌─────────────────┐      ┌───────────┐
│   Coach   │ ──▶  │  MatchRequest   │ ──▶  │   Admin   │
│           │      │   (PENDING)     │      │           │
└───────────┘      └─────────────────┘      └─────┬─────┘
                                                   │
                            ┌──────────────────────┼──────────────────────┐
                            │                      │                      │
                            ▼                      ▼                      │
                   ┌────────────────┐    ┌────────────────┐               │
                   │    APPROVED    │    │    REJECTED    │               │
                   └───────┬────────┘    └────────────────┘               │
                           │                                               │
                           ▼                                               │
                   ┌────────────────┐                                      │
                   │  Match créé    │                                      │
                   │  (SCHEDULED)   │                                      │
                   └───────┬────────┘                                      │
                           │                                               │
                           ▼                                               │
                   ┌────────────────┐                                      │
                   │  Composition   │ ◀────────────────────────────────────┘
                   │  par le Coach  │
                   └───────┬────────┘
                           │
                           ▼
                   ┌────────────────┐
                   │    FINISHED    │
                   │  + SmallEvents │
                   └────────────────┘
```

#### Design Patterns utilisés

| Pattern | Utilisation |
|---------|-------------|
| **Singleton** | `MatchManager`, `MatchRequestManager`, `CompositionManager` |
| **Abstract Factory** | `AbstractFactory.createMatchDAO()` |
| **Facade** | `MatchFacade`, `MatchRequestFacade`, `CompositionFacade` |
| **DAO** | `PostgresMatchDAO`, `PostgresMatchRequestDAO`, `PostgresCompositionDAO` |

### 3.3 Training Management (Matteo - diag, Ayoub - code)

#### Statut actuel

⚠️ **Ce module n'est PAS encore implémenté dans le code.**

L'interface affiche actuellement un placeholder :
```
"Ce module sera ajouté prochainement."
```

#### Fonctionnalités prévues (non implémentées)

| ID | Fonctionnalité prévue | Statut |
|----|----------------------|--------|
| T1 | Planifier un entraînement | ❌ Non implémenté |
| T2 | Modifier un entraînement | ❌ Non implémenté |
| T3 | Annuler un entraînement | ❌ Non implémenté |
| T4 | Lister les entraînements | ❌ Non implémenté |

#### Impact sur le Sprint

- **Couverture** : 0% du use case
- **Raison** : Priorité donnée à Stats et Match Management
- **Plan de remédiation** : À implémenter au Sprint 3

### 3.4 Team Manager (Rasim - diag, Théo - code)

#### Fonctionnalités existantes

La gestion des équipes repose sur les composants suivants :

| Composant | Fichier | Description |
|-----------|---------|-------------|
| ClubController | `ClubController.java` | Gestion des clubs et membres |
| ClubManager | `ClubManager.java` | Logique métier des clubs |
| ClubDAO | `PostgresClubDAO.java` | Persistance des clubs |

#### Fonctionnalités partiellement implémentées

| ID | Fonctionnalité | Statut |
|----|----------------|--------|
| TM1 | Créer un club | ✅ Implémenté |
| TM2 | Gérer les membres | ✅ Implémenté |
| TM3 | Assigner des rôles | 🔄 Partiel (via composition) |
| TM4 | Gérer les coachs | ❌ Non implémenté |
| TM5 | Statistiques équipe | ✅ Via Stats Management |
| TM6 | Historique équipe | ❌ Non implémenté |

---

## 4. Problèmes Rencontrés et Solutions

### 4.1 Problèmes techniques

| Problème | Impact | Solution adoptée |
|----------|--------|------------------|
| Intégration Stats ↔ Match | Moyen | Création de `SmallEvent` générés automatiquement par `PostgresMatchDAO` |
| Validation des compositions | Faible | Ajout de `CompositionManager.validateComposition()` |
| Connexion BDD partagée | Faible | Utilisation de `PostgresUserDAO.getConnection()` via AbstractFactory |

### 4.2 Problèmes organisationnels

| Problème | Impact | Solution adoptée |
|----------|--------|------------------|
| Training Management non implémenté | Élevé | Report au Sprint 3, priorité aux modules critiques |
| Temps estimé vs temps réel | Moyen | Stats: +20% du temps prévu, Match: +15% du temps prévu |

---

## 5. Leçons Apprises

### 5.1 Points positifs

1. **Architecture en couches** : La séparation UI → Controller → Facade → Manager → DAO facilite la maintenance
2. **Design Patterns** : L'utilisation systématique de Singleton et Factory assure la cohérence
3. **Intégration automatique** : Les SmallEvents générés automatiquement réduisent le code manuel
4. **Réutilisation** : Les composants Match sont réutilisables pour d'autres fonctionnalités

### 5.2 Points à améliorer

1. **Estimation du temps** : Les use cases ont pris 15-20% plus de temps que prévu
2. **Training Management** : Ce module critique n'a pas été livré
3. **Tests** : Pas de tests unitaires automatisés
4. **Documentation** : Documentation technique créée en fin de sprint

---

## 6. Métriques de Productivité

### 6.1 Par développeur

| Développeur | Use Case | Rôle | Tâches accomplies | Évaluation |
|-------------|----------|------|-------------------|------------|
| **Rasim** | Stats | Code | 6 classes, ~400 LOC, UI fonctionnelle | ⭐⭐⭐⭐⭐ |
| **Matteo** | Match | Code | 12 classes, ~800 LOC, workflow complet | ⭐⭐⭐⭐⭐ |
| **Ayoub** | Stats | Diagrammes | Diagrammes de séquence et classes | ⭐⭐⭐⭐ |
| **Ayoub** | Training | Code | Non implémenté (placeholder uniquement) | ⚠️ En attente |
| **Théo** | Match | Diagrammes | Diagrammes d'architecture | ⭐⭐⭐⭐ |
| **Théo** | Team Manager | Code | Implémentation partielle | ⭐⭐⭐ |

### 6.2 Résumé global du sprint

| Métrique | Valeur |
|----------|--------|
| **Durée du sprint** | 2 semaines |
| **Use cases prévus** | 4 |
| **Use cases complétés à 100%** | 2 (Stats, Match) |
| **Use cases partiels** | 1 (Team Manager - 33%) |
| **Use cases non commencés** | 1 (Training - 0%) |
| **Taux de complétion global** | **58%** |
| **Classes Java créées** | ~20 |
| **Lignes de code** | ~1200 LOC |

---

## 7. Recommandations pour le Sprint 3

### 7.1 Priorités

1. **🔴 Critique** : Implémenter Training Management
2. **🟠 Important** : Compléter Team Manager
3. **🟡 Souhaitable** : Ajouter des tests unitaires
4. **🟢 Bonus** : Améliorer l'UI des dashboards

### 7.2 Plan d'action

| Tâche | Responsable suggéré | Durée estimée |
|-------|---------------------|---------------|
| Training Management - Models | Ayoub | 2 jours |
| Training Management - DAO | Ayoub | 2 jours |
| Training Management - Controller/Facade | Ayoub | 2 jours |
| Training Management - UI | Ayoub | 2 jours |
| Team Manager - Complétion | Théo | 3 jours |
| Tests unitaires | Équipe | 2 jours |

---

## 8. Conclusion

Le Sprint 2 a permis de livrer avec succès les modules **Stats Management** et **Match Management**, qui représentent le cœur fonctionnel de l'application SportifyClubManager. Ces deux modules sont pleinement opérationnels et suivent les bonnes pratiques d'architecture logicielle (patterns Singleton, Factory, Facade, DAO).

Cependant, le module **Training Management** n'a pas été implémenté et reste un placeholder dans l'interface. Ce manque représente une dette technique à résoudre en priorité au Sprint 3.

Le taux de complétion global de **58%** indique qu'une meilleure estimation et répartition des tâches serait bénéfique pour les prochains sprints.

---

## Annexes

### A. Liste des fichiers créés

```
src/main/java/com/sportify/manager/
├── controllers/
│   ├── MatchController.java
│   ├── MatchRequestController.java
│   ├── CompositionController.java
│   └── StatController.java
├── dao/
│   ├── MatchDAO.java
│   ├── PostgresMatchDAO.java
│   ├── MatchRequestDAO.java
│   ├── PostgresMatchRequestDAO.java
│   ├── CompositionDAO.java
│   ├── PostgresCompositionDAO.java
│   ├── StatDAO.java
│   └── PostgresStatDAO.java
├── facade/
│   ├── MatchFacade.java
│   ├── MatchRequestFacade.java
│   ├── CompositionFacade.java
│   └── StatFacade.java
├── services/
│   ├── Match.java
│   ├── MatchManager.java
│   ├── MatchRequest.java
│   ├── MatchRequestManager.java
│   ├── MatchStatus.java
│   ├── MatchRequestStatus.java
│   ├── Composition.java
│   ├── CompositionManager.java
│   ├── RoleAssignment.java
│   ├── SmallEvent.java
│   ├── StatManager.java
│   └── Statistique.java
└── frame/
    └── StatFrame.java
```

### B. Schéma de base de données

```sql
-- Tables créées durant le Sprint 2
CREATE TABLE matchs (...);
CREATE TABLE match_requests (...);
CREATE TABLE match_composition (...);
CREATE TABLE small_events (...);
```

### C. Technologies utilisées

- **Langage** : Java 17
- **UI** : JavaFX 21.0.1
- **Base de données** : PostgreSQL
- **Build** : Maven
- **Patterns** : MVC, Singleton, Factory, Facade, DAO
