# Documentation Complète - Use Case Statistiques (Stats)

## Résumé Exécutif pour le Management Report

Ce document présente une synthèse complète du module Statistiques (Stats) de l'application SportifyClubManager. Il détaille l'architecture, les design patterns utilisés, le flux de données, les fonctionnalités implémentées et les intégrations avec les autres modules du système.

---

## Table des Matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Architecture Technique](#2-architecture-technique)
3. [Design Patterns Utilisés](#3-design-patterns-utilisés)
4. [Modèle de Données](#4-modèle-de-données)
5. [Composants du Système](#5-composants-du-système)
6. [Fonctionnalités Implémentées](#6-fonctionnalités-implémentées)
7. [Intégrations avec les Autres Modules](#7-intégrations-avec-les-autres-modules)
8. [Interface Utilisateur](#8-interface-utilisateur)
9. [Flux de Données](#9-flux-de-données)
10. [Use Cases Supportés](#10-use-cases-supportés)

---

## 1. Vue d'ensemble

### 1.1 Objectif du Module
Le module Statistiques permet de :
- **Collecter** des événements sportifs (buts, victoires, participations, etc.)
- **Calculer** des métriques de performance (ratios, pourcentages, tendances)
- **Visualiser** les données sous forme de graphiques (PieChart, KPIs)
- **Analyser** les performances des équipes et des joueurs

### 1.2 Acteurs Concernés
| Acteur | Accès aux Stats | Actions Possibles |
|--------|-----------------|-------------------|
| **Coach** | ✅ Complet | Visualiser les stats de son équipe via le dashboard |
| **Directeur** | ✅ Lecture | Consulter les performances globales |
| **Admin** | ✅ Indirect | Les matchs terminés génèrent automatiquement des stats |
| **Membre** | ❌ Non implémenté | Fonctionnalité future potentielle |

### 1.3 Technologies Utilisées
- **Langage** : Java 17
- **Framework UI** : JavaFX 21.0.1
- **Base de données** : PostgreSQL 42.7.3
- **Architecture** : Layered Architecture (MVC + Facade + Factory)

---

## 2. Architecture Technique

### 2.1 Structure des Packages

```
src/main/java/com/sportify/manager/
├── controllers/        # Contrôleurs (logique de présentation)
│   └── StatController.java
├── facade/             # Façades (interface simplifiée)
│   └── StatFacade.java
├── services/           # Logique métier
│   ├── StatManager.java
│   ├── Statistique.java
│   └── SmallEvent.java
├── dao/                # Accès aux données
│   ├── StatDAO.java (interface)
│   └── PostgresStatDAO.java (implémentation)
├── persistence/        # Factory Pattern
│   ├── AbstractFactory.java
│   └── PostgresFactory.java
└── frame/              # Interface utilisateur
    └── StatFrame.java
```

### 2.2 Diagramme de Couches

```
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE PRÉSENTATION                       │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   StatFrame     │    │  CoachDashboardFrame            │ │
│  │   (JavaFX)      │◄───│  (intègre StatFrame)            │ │
│  └────────┬────────┘    └─────────────────────────────────┘ │
└───────────┼─────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE CONTRÔLEUR                         │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                  StatController                          ││
│  │  • getTeamDistribution(teamId, period)                  ││
│  │  • getPerformanceRatios(teamId, period)                 ││
│  │  • getTopScorers(teamId)                                ││
│  │  • compareTeams(id1, id2, period)                       ││
│  └────────────────────────┬────────────────────────────────┘│
└───────────────────────────┼─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE FAÇADE                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    StatFacade                            ││
│  │  • getTeamStats(teamId, period)                         ││
│  │  • getAggregatedStatsByTeam(teamId, period)             ││
│  │  • getTopPerformers(teamId, eventType, limit)           ││
│  │  • getTrendData(teamId, eventType, start, end)          ││
│  └────────────────────────┬────────────────────────────────┘│
└───────────────────────────┼─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE MÉTIER (SERVICES)                  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    StatManager                           ││
│  │  • calculateTeamPerformance(teamId, period)             ││
│  │  • getPlayerParticipationRate(playerId, teamId, period) ││
│  │  • getAggregatedStats(teamId, period)                   ││
│  │  • getRanking(teamId, eventType, limit)                 ││
│  │  • getTrends(teamId, eventType, start, end)             ││
│  └────────────────────────┬────────────────────────────────┘│
└───────────────────────────┼─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE PERSISTENCE (DAO)                  │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ AbstractFactory │───►│      PostgresFactory            │ │
│  │   (Singleton)   │    │  createStatDAO() → PostgresStatDAO│
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                               │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │    StatDAO      │◄───│      PostgresStatDAO            │ │
│  │   (interface)   │    │  (implémentation PostgreSQL)    │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    BASE DE DONNÉES                           │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                  PostgreSQL                              ││
│  │           Table: small_events                            ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Design Patterns Utilisés

### 3.1 Abstract Factory Pattern

**Fichiers concernés** : `AbstractFactory.java`, `PostgresFactory.java`

**Objectif** : Permettre la création d'objets DAO sans spécifier leurs classes concrètes.

```java
// AbstractFactory.java
public abstract class AbstractFactory {
    private static AbstractFactory instance = null;
    
    static {
        // Auto-initialisation avec PostgresFactory
        if (instance == null) {
            instance = new PostgresFactory();
        }
    }
    
    public static AbstractFactory getFactory() {
        return instance;
    }
    
    public abstract StatDAO createStatDAO();
    // ... autres méthodes factory
}

// PostgresFactory.java
public class PostgresFactory extends AbstractFactory {
    @Override
    public StatDAO createStatDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresStatDAO(connection);
    }
}
```

**Avantages** :
- Découplage entre la logique métier et l'implémentation de la persistance
- Facilite le changement de base de données (ex: MySQL, MongoDB)
- Permet l'injection de dépendances pour les tests unitaires

### 3.2 Facade Pattern

**Fichier concerné** : `StatFacade.java`

**Objectif** : Fournir une interface simplifiée aux couches supérieures.

```java
public class StatFacade {
    private StatManager statManager;

    public StatFacade() {
        this.statManager = new StatManager();
    }

    // Interface simplifiée pour récupérer les stats
    public List<Statistique> getTeamStats(int teamId, String period) throws SQLException {
        return statManager.calculateTeamPerformance(teamId, period);
    }
    
    // ... autres méthodes de façade
}
```

**Avantages** :
- Simplifie l'interface pour le contrôleur
- Cache la complexité des calculs métier
- Point d'entrée unique pour les opérations statistiques

### 3.3 Data Access Object (DAO) Pattern

**Fichiers concernés** : `StatDAO.java` (interface), `PostgresStatDAO.java` (implémentation)

**Objectif** : Séparer la logique d'accès aux données de la logique métier.

```java
// Interface
public interface StatDAO {
    List<SmallEvent> getEventsByTeam(int teamId, String period) throws SQLException;
    Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException;
    Map<String, Integer> getTopPerformers(int teamId, String eventType, int limit) throws SQLException;
    // ... autres méthodes
}

// Implémentation PostgreSQL
public class PostgresStatDAO implements StatDAO {
    private Connection connection;
    
    @Override
    public Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException {
        String query = "SELECT type, COUNT(*) as total FROM small_events " +
                       "WHERE team_id = ? AND period = ? GROUP BY type";
        // ... exécution de la requête
    }
}
```

**Avantages** :
- Abstraction de la couche de persistance
- Testabilité améliorée (mock des DAO)
- Réutilisation du code d'accès aux données

### 3.4 Domain Model Pattern

**Fichiers concernés** : `Statistique.java`, `SmallEvent.java`

**Objectif** : Représenter les entités métier du domaine.

```java
// Statistique.java - Représente une métrique calculée
public class Statistique {
    private String type;    // Ex: "Taux de victoire"
    private double valeur;  // Valeur numérique
    private String periode; // Ex: "Saison 2024"
    private String unite;   // Ex: "%", "buts"
}

// SmallEvent.java - Représente un événement sportif brut
public class SmallEvent {
    private int id;
    private String type;        // "GOAL", "VICTOIRE", "PARTICIPATION"
    private String description;
    private int teamId;
    private String playerId;
    private Timestamp timestamp;
    private String period;
}
```

---

## 4. Modèle de Données

### 4.1 Table `small_events`

```sql
CREATE TABLE small_events (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50),           -- Type d'événement: GOAL, VICTOIRE, MATCH, PARTICIPATION
    description TEXT,           -- Description optionnelle
    team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
    period VARCHAR(50),         -- Ex: "Saison 2024"
    event_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.2 Types d'Événements Supportés

| Type | Description | Source |
|------|-------------|--------|
| `GOAL` | But marqué | Saisie manuelle ou import |
| `MATCH` | Match joué | Généré automatiquement par MatchDAO |
| `VICTOIRE` | Victoire de l'équipe | Généré automatiquement par MatchDAO |
| `DEFAITE` | Défaite de l'équipe | Généré automatiquement par MatchDAO |
| `NUL` | Match nul | Généré automatiquement par MatchDAO |
| `PARTICIPATION` | Participation d'un joueur | Généré automatiquement par CompositionDAO |

### 4.3 Relations avec les Autres Tables

```
users (id) ◄─────────────────┐
                             │
clubs (clubid) ◄─────────────┤
                             │
small_events ────────────────┘
     │
     │ Alimenté par:
     ├── matchs (via PostgresMatchDAO.generateStatsAfterMatch)
     └── match_composition (via PostgresCompositionDAO.generateParticipationStats)
```

---

## 5. Composants du Système

### 5.1 StatController

**Rôle** : Contrôleur principal pour les opérations statistiques côté UI.

**Méthodes principales** :

| Méthode | Entrée | Sortie | Description |
|---------|--------|--------|-------------|
| `getTeamDistribution` | teamId, period | `Map<String, Integer>` | Distribution des événements pour PieChart |
| `getPerformanceRatios` | teamId, period | `Map<String, Double>` | Ratios calculés (WinRate, GoalsPerMatch) |
| `getTopScorers` | teamId | `Map<String, Integer>` | Top 5 buteurs de l'équipe |
| `compareTeams` | id1, id2, period | `Map<String, Map<String, Integer>>` | Comparaison entre deux équipes |

### 5.2 StatFacade

**Rôle** : Interface simplifiée vers la couche métier.

**Méthodes principales** :

| Méthode | Use Case | Description |
|---------|----------|-------------|
| `getTeamStats` | UC 3 & 9 | Récupère les données brutes de performance |
| `getAggregatedStatsByTeam` | UC 9.2.1 | Données agrégées pour les graphiques |
| `getTopPerformers` | UC 5 & 9.2.1 | Classement des meilleurs joueurs |
| `getTrendData` | UC 9.2.1 | Données historiques pour les tendances |

### 5.3 StatManager

**Rôle** : Logique métier et calculs statistiques.

**Algorithmes clés** :

```java
// Calcul du taux de victoire (Use Case 9.2.1)
public List<Statistique> calculateTeamPerformance(int teamId, String period) {
    List<SmallEvent> events = getDAO().getEventsByTeam(teamId, period);
    
    long buts = events.stream().filter(e -> "GOAL".equals(e.getType())).count();
    long matchs = events.stream().filter(e -> "MATCH".equals(e.getType())).count();
    long victoires = events.stream().filter(e -> "VICTOIRE".equals(e.getType())).count();
    
    if (matchs > 0) {
        double winRate = ((double) victoires / matchs) * 100;
        double moyenneButs = (double) buts / matchs;
        // ...
    }
}

// Calcul du taux de participation (lien avec CompositionManagement)
public double getPlayerParticipationRate(String playerId, int teamId, String period) {
    long participations = /* count PARTICIPATION events for player */;
    long totalMatchs = /* count MATCH events for team */;
    return totalMatchs > 0 ? (double) participations / totalMatchs : 0.0;
}
```

### 5.4 PostgresStatDAO

**Rôle** : Accès aux données PostgreSQL.

**Requêtes SQL principales** :

```sql
-- Récupération des événements d'une équipe
SELECT * FROM small_events 
WHERE team_id = ? AND period = ? 
ORDER BY event_date DESC;

-- Agrégation par type d'événement (pour PieChart)
SELECT type, COUNT(*) as total 
FROM small_events 
WHERE team_id = ? AND period = ? 
GROUP BY type;

-- Top performers (meilleurs joueurs)
SELECT player_id, COUNT(*) as score 
FROM small_events 
WHERE team_id = ? AND type = ? 
GROUP BY player_id 
ORDER BY score DESC 
LIMIT ?;

-- Tendances temporelles (pour LineChart)
SELECT DATE(event_date) as day, COUNT(*) as total 
FROM small_events 
WHERE team_id = ? AND type = ? AND event_date BETWEEN ? AND ? 
GROUP BY day 
ORDER BY day ASC;
```

### 5.5 StatFrame

**Rôle** : Interface graphique JavaFX pour la visualisation des statistiques.

**Composants UI** :

```
┌─────────────────────────────────────────────────────────────┐
│ Tableau de Bord Statistique - Sportify                      │
├─────────────────────────────────────────────────────────────┤
│ Période : [Saison 2024 ▼] [Dernier Mois] [Global]           │
├───────────────────────────────────┬─────────────────────────┤
│                                   │    KPI Cards            │
│         PIE CHART                 │  ┌─────────────────┐   │
│    "Répartition des Actions"      │  │ WinRate         │   │
│                                   │  │ 75.00%          │   │
│         [🔵 Buts]                 │  └─────────────────┘   │
│         [🟢 Victoires]            │  ┌─────────────────┐   │
│         [🔴 Défaites]             │  │ GoalsPerMatch   │   │
│                                   │  │ 2.50            │   │
│                                   │  └─────────────────┘   │
├───────────────────────────────────┴─────────────────────────┤
│                    [Fermer l'analyse]                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Fonctionnalités Implémentées

### 6.1 Visualisation des Statistiques (Use Case 2)

**Description** : Affichage graphique de la distribution des actions sportives.

**Composant** : PieChart dans `StatFrame`

**Données affichées** :
- Répartition des types d'événements (Buts, Victoires, Défaites, Matchs nuls)
- Mise à jour dynamique selon la période sélectionnée

### 6.2 Calcul des Performances (Use Case 3 & 9)

**Description** : Calcul automatique des métriques de performance.

**Métriques calculées** :
- Total des buts
- Nombre de victoires
- Moyenne de buts par match
- Taux de victoire (%)

### 6.3 Classement des Joueurs (Use Case 5)

**Description** : Identification des meilleurs performeurs.

**Fonctionnalité** :
```java
// Top 5 buteurs
Map<String, Integer> topScorers = statFacade.getTopPerformers(teamId, "BUT", 5);
```

### 6.4 Filtrage par Période (Use Case 7)

**Description** : Sélection de la période d'analyse.

**Options disponibles** :
- Saison 2024
- Dernier Mois
- Global

### 6.5 Comparaison d'Équipes (Use Case 8)

**Description** : Comparaison des statistiques entre deux équipes.

```java
Map<String, Map<String, Integer>> comparison = statController.compareTeams(id1, id2, period);
// comparison.get("TeamA") -> stats de l'équipe A
// comparison.get("TeamB") -> stats de l'équipe B
```

### 6.6 Identification des Tendances (Use Case 9.2.1)

**Description** : Analyse de l'évolution temporelle des performances.

```java
Map<String, Integer> trends = statFacade.getTrendData(teamId, "GOAL", "2024-01-01", "2024-12-31");
// Retourne: {"2024-01-15": 3, "2024-01-22": 2, ...}
```

---

## 7. Intégrations avec les Autres Modules

### 7.1 Intégration avec MatchManagement

**Fichier** : `PostgresMatchDAO.java`

**Mécanisme** : Génération automatique de statistiques à la fin d'un match.

```java
private void generateStatsAfterMatch(Match m) throws SQLException {
    PostgresStatDAO statDAO = new PostgresStatDAO(con);
    Timestamp now = new Timestamp(System.currentTimeMillis());
    String period = "Saison " + m.getDateTime().getYear();

    // Résultat pour l'équipe à domicile
    String resultHome = (m.getHomeScore() > m.getAwayScore()) ? "VICTOIRE" :
                        (m.getHomeScore() < m.getAwayScore()) ? "DEFAITE" : "NUL";

    statDAO.addSmallEvent(new SmallEvent(0, "MATCH", "Match joué", m.getHomeTeamId(), null, now, period));
    statDAO.addSmallEvent(new SmallEvent(0, resultHome, "Résultat final", m.getHomeTeamId(), null, now, period));

    // Idem pour l'équipe à l'extérieur
    // ...
}
```

**Déclencheur** : Lorsqu'un match passe au statut `FINISHED` avec un score défini.

### 7.2 Intégration avec CompositionManagement

**Fichier** : `PostgresCompositionDAO.java`

**Mécanisme** : Génération automatique des événements de participation.

```java
private void generateParticipationStats(int matchId, int teamId, List<RoleAssignment> assignments) throws SQLException {
    PostgresStatDAO statDAO = new PostgresStatDAO(con);
    
    for (RoleAssignment a : assignments) {
        SmallEvent participation = new SmallEvent(
            0,
            "PARTICIPATION",
            "Participation au match " + matchId,
            teamId,
            a.getPlayerId(),
            now,
            "Saison Actuelle"
        );
        statDAO.addSmallEvent(participation);
    }
}
```

**Déclencheur** : Lors de l'enregistrement d'une composition d'équipe pour un match.

### 7.3 Intégration avec le Dashboard Coach

**Fichier** : `CoachDashboardFrame.java`

**Mécanisme** : Ouverture de `StatFrame` avec l'ID du club du coach.

```java
btnStats.setOnAction(e -> {
    if (coachClubId != -1) {
        StatFrame statFrame = new StatFrame();
        statFrame.show(coachClubId); // ID dynamique du club
    }
});
```

---

## 8. Interface Utilisateur

### 8.1 Accès aux Statistiques

| Rôle | Point d'Accès | Navigation |
|------|---------------|------------|
| Coach | CoachDashboardFrame | Menu latéral → "📊 Stat Management" |
| Director | Non directement accessible | Fonctionnalité future |
| Admin | Non directement accessible | Fonctionnalité future |

### 8.2 Éléments de l'Interface

1. **Barre de Filtres** (HBox en haut)
   - ComboBox pour sélectionner la période
   - Options : "Saison 2024", "Dernier Mois", "Global"

2. **Graphique Central** (PieChart)
   - Titre : "Répartition des Actions"
   - Légende visible
   - Données dynamiques selon le filtre

3. **Cartes KPI** (VBox à droite)
   - Style : Cartes blanches avec ombre portée
   - Affichage : Label du titre + valeur formatée
   - Ex: "WinRate : 75.00%", "GoalsPerMatch : 2.50"

4. **Bouton de Fermeture** (HBox en bas)
   - Texte : "Fermer l'analyse"
   - Style : Fond bleu foncé (#2c3e50), texte blanc

### 8.3 Dimensions de la Fenêtre

- Largeur : 900 pixels
- Hauteur : 600 pixels
- Background : #f4f7f6 (gris clair)

---

## 9. Flux de Données

### 9.1 Flux de Collecte des Données

```
   ┌────────────────────┐
   │   MATCH TERMINÉ    │
   │  (Admin Dashboard) │
   └─────────┬──────────┘
             │ update(match)
             ▼
   ┌────────────────────┐
   │  PostgresMatchDAO  │
   │   update(match)    │
   └─────────┬──────────┘
             │ if status == FINISHED
             ▼
   ┌────────────────────────────────────┐
   │  generateStatsAfterMatch(match)   │
   │                                    │
   │  → Crée SmallEvent "MATCH"        │
   │  → Crée SmallEvent "VICTOIRE/..."│
   └─────────┬──────────────────────────┘
             │ addSmallEvent()
             ▼
   ┌────────────────────┐
   │    small_events    │
   │    (PostgreSQL)    │
   └────────────────────┘
```

### 9.2 Flux de Consultation des Statistiques

```
   ┌────────────────────┐
   │   COACH DASHBOARD  │
   │  Clic sur "Stats"  │
   └─────────┬──────────┘
             │ show(clubId)
             ▼
   ┌────────────────────┐
   │     StatFrame      │
   │ updateDashboard()  │
   └─────────┬──────────┘
             │ getTeamDistribution(teamId, period)
             │ getPerformanceRatios(teamId, period)
             ▼
   ┌────────────────────┐
   │   StatController   │
   └─────────┬──────────┘
             │
             ▼
   ┌────────────────────┐
   │     StatFacade     │
   └─────────┬──────────┘
             │
             ▼
   ┌────────────────────┐
   │    StatManager     │
   │   (calculs métier) │
   └─────────┬──────────┘
             │ getDAO().getEventsByTeam()
             ▼
   ┌────────────────────┐
   │  AbstractFactory   │
   │   → PostgresFactory│
   │   → PostgresStatDAO│
   └─────────┬──────────┘
             │ SQL Query
             ▼
   ┌────────────────────┐
   │    small_events    │
   │    (PostgreSQL)    │
   └────────────────────┘
```

---

## 10. Use Cases Supportés

### 10.1 Tableau Récapitulatif

| ID | Use Case | Statut | Composants |
|----|----------|--------|------------|
| UC 2 | View Statistics | ✅ Implémenté | StatFrame (PieChart) |
| UC 3 | Calculate Performance | ✅ Implémenté | StatManager.calculateTeamPerformance() |
| UC 5 | Generate Player Statistics | ✅ Implémenté | StatController.getTopScorers() |
| UC 7 | Filter Statistics | ✅ Implémenté | StatFrame (ComboBox période) |
| UC 8 | Compare Statistics | ✅ Implémenté | StatController.compareTeams() |
| UC 9 | Detailed Performance Calculation | ✅ Implémenté | StatManager (ratios, moyennes) |
| UC 9.2.1 | Calculate Statistics | ✅ Implémenté | Agrégation, ratios, tendances |

### 10.2 Use Case 9.2.1 - Calculate Statistics (Détail)

**Basic Flow implémenté** :

1. ✅ **Récupération des données brutes** : `getEventsByTeam()`
2. ✅ **Agrégation par type** : `getAggregatedStatsByTeam()`
3. ✅ **Calcul des ratios** : `getPerformanceRatios()` (WinRate, GoalsPerMatch)
4. ✅ **Classement des performances** : `getTopPerformers()`
5. ✅ **Identification des tendances** : `getTrendData()`

---

## Annexes

### A. Diagramme de Séquence - Affichage des Stats

```
Coach          StatFrame      StatController     StatFacade      StatManager      StatDAO         PostgreSQL
  │                │                │                │               │               │                │
  │──clic Stats───►│                │                │               │               │                │
  │                │                │                │               │               │                │
  │                │ show(clubId)   │                │               │               │                │
  │                │───────────────►│                │               │               │                │
  │                │                │                │               │               │                │
  │                │ updateDashboard(period)         │               │               │                │
  │                │───────────────────────────────►│                │               │                │
  │                │                │                │               │               │                │
  │                │                │ getAggregatedStatsByTeam()     │               │                │
  │                │                │───────────────────────────────►│               │                │
  │                │                │                │               │               │                │
  │                │                │                │   getAggregatedStats()        │                │
  │                │                │                │──────────────────────────────►│                │
  │                │                │                │               │               │                │
  │                │                │                │               │ getDAO()      │                │
  │                │                │                │               │──────────────►│                │
  │                │                │                │               │               │                │
  │                │                │                │               │               │ SQL Query      │
  │                │                │                │               │               │───────────────►│
  │                │                │                │               │               │                │
  │                │                │                │               │               │ ResultSet      │
  │                │                │                │               │◄──────────────│◄───────────────│
  │                │                │                │               │               │                │
  │                │                │                │ Map<String, Integer>          │                │
  │                │                │◄──────────────────────────────────────────────│                │
  │                │                │                │               │               │                │
  │                │ Map<String, Integer>            │               │               │                │
  │                │◄───────────────────────────────│                │               │                │
  │                │                │                │               │               │                │
  │                │ affiche PieChart + KPIs         │               │               │                │
  │◄───────────────│                │                │               │               │                │
```

### B. Métriques Clés Calculées

| Métrique | Formule | Unité |
|----------|---------|-------|
| WinRate | (Victoires / Matchs) × 100 | % |
| GoalsPerMatch | Buts / Matchs | ratio |
| ParticipationRate | Participations / Matchs | ratio |
| GoalRatio | Buts du joueur / Total événements joueur | ratio |

### C. Points d'Extension Futurs

1. **Bar Charts** pour les classements (Top Scorers)
2. **Line Charts** pour les tendances temporelles
3. **Export PDF/Excel** des rapports
4. **Dashboard Directeur** avec vue globale multi-clubs
5. **Statistiques individuelles** accessibles par les membres
6. **Comparaisons inter-saisons**
7. **Alertes de performance** (notifications si baisse significative)

---

*Document généré pour le Management Report - SportifyClubManager*
*Version: 1.0*
*Date: Janvier 2026*
