# Event Management Use Case - Résumé Technique Complet

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture globale](#architecture-globale)
3. [Design Patterns utilisés](#design-patterns-utilisés)
4. [Structure des composants](#structure-des-composants)
5. [Modèle de données](#modèle-de-données)
6. [Flux de données et séquences](#flux-de-données-et-séquences)
7. [Fonctionnalités principales](#fonctionnalités-principales)
8. [Intégrations avec d'autres modules](#intégrations-avec-dautres-modules)
9. [Interface utilisateur](#interface-utilisateur)
10. [Base de données](#base-de-données)

---

## Vue d'ensemble

Le module **Event Management** de SportifyClubManager gère la planification, l'organisation et le suivi des matchs sportifs. Il comprend :

- **Gestion des matchs** : Création, modification et suivi des matchs
- **Demandes de match** : Workflow d'approbation pour les demandes des coachs
- **Compositions d'équipes** : Feuilles de match avec attribution des rôles
- **Intégration statistiques** : Génération automatique de statistiques après les matchs

### Acteurs principaux

| Acteur | Responsabilités |
|--------|----------------|
| **Admin** | Planifier les matchs, valider/refuser les demandes de match, gérer les scores |
| **Coach** | Soumettre des demandes de match, composer les équipes, gérer les feuilles de match |
| **Director** | Visualiser les événements (module placeholder) |

---

## Architecture globale

L'architecture suit une structure **MVC-like en couches** avec une séparation claire des responsabilités :

```
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE PRÉSENTATION                       │
│  ┌───────────────────┐  ┌───────────────────┐               │
│  │ AdminDashboard    │  │ CoachDashboard    │               │
│  │ Frame.java        │  │ Frame.java        │               │
│  └───────────────────┘  └───────────────────┘               │
├─────────────────────────────────────────────────────────────┤
│                    COUCHE CONTRÔLEUR                         │
│  ┌─────────────────┐ ┌───────────────────┐ ┌─────────────┐  │
│  │MatchController  │ │MatchRequestContr │ │Composition  │  │
│  │                 │ │oller             │ │Controller   │  │
│  └────────┬────────┘ └────────┬─────────┘ └──────┬──────┘  │
├───────────┼───────────────────┼──────────────────┼──────────┤
│           │       COUCHE FACADE                  │          │
│  ┌────────▼────────┐ ┌────────▼─────────┐ ┌──────▼──────┐  │
│  │ MatchFacade     │ │MatchRequestFacade│ │Composition  │  │
│  │                 │ │                  │ │Facade       │  │
│  └────────┬────────┘ └────────┬─────────┘ └──────┬──────┘  │
├───────────┼───────────────────┼──────────────────┼──────────┤
│           │      COUCHE MÉTIER (MANAGERS)        │          │
│  ┌────────▼────────┐ ┌────────▼─────────┐ ┌──────▼──────┐  │
│  │ MatchManager    │ │MatchRequest     │ │Composition  │  │
│  │                 │ │Manager          │ │Manager      │  │
│  └────────┬────────┘ └────────┬─────────┘ └──────┬──────┘  │
├───────────┼───────────────────┼──────────────────┼──────────┤
│           │      COUCHE PERSISTANCE (DAO)        │          │
│  ┌────────▼────────┐ ┌────────▼─────────┐ ┌──────▼──────┐  │
│  │ PostgresMatch   │ │PostgresMatch    │ │Postgres     │  │
│  │ DAO             │ │RequestDAO       │ │CompositionDAO│ │
│  └────────┬────────┘ └────────┬─────────┘ └──────┬──────┘  │
├───────────┼───────────────────┼──────────────────┼──────────┤
│           └───────────────────┴──────────────────┘          │
│                       PostgreSQL                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Design Patterns utilisés

### 1. Singleton Pattern

**Utilisation** : Toutes les classes Manager et Controller utilisent le pattern Singleton pour garantir une instance unique.

```java
// Exemple dans MatchManager.java
private static MatchManager instance;

private MatchManager() {
    this.matchDAO = AbstractFactory.getFactory().createMatchDAO();
    this.typeSportManager = TypeSportManager.getInstance();
}

public static synchronized MatchManager getInstance() {
    if (instance == null) {
        instance = new MatchManager();
    }
    return instance;
}
```

**Classes concernées** :
- `MatchManager`, `MatchRequestManager`, `CompositionManager`
- `MatchController`, `MatchRequestController`, `CompositionController`
- `MatchFacade`, `MatchRequestFacade`, `CompositionFacade`
- `TypeSportManager`

### 2. Abstract Factory Pattern

**Utilisation** : Création des objets DAO via une factory abstraite permettant l'abstraction de la base de données.

```java
// AbstractFactory.java
public abstract class AbstractFactory {
    private static AbstractFactory instance = null;

    // Auto-initialisation avec PostgresFactory par défaut
    static {
        if (instance == null) {
            instance = new PostgresFactory();
        }
    }

    public static AbstractFactory getFactory() {
        return instance;
    }

    // Méthodes abstraites pour créer les DAO
    public abstract MatchDAO createMatchDAO();
    public abstract CompositionDAO createCompositionDAO();
    public abstract MatchRequestDAO createMatchRequestDAO();
}
```

```java
// PostgresFactory.java - Implémentation concrète
@Override
public MatchDAO createMatchDAO() {
    Connection connection = PostgresUserDAO.getConnection();
    return new PostgresMatchDAO(connection);
}
```

### 3. Facade Pattern

**Utilisation** : Simplification de l'interface vers les couches métier.

```java
// MatchFacade.java
public class MatchFacade {
    private final MatchManager matchManager;

    private MatchFacade() {
        this.matchManager = MatchManager.getInstance();
    }

    public Match createMatch(Match m) throws Exception {
        return matchManager.createMatch(m);
    }
    
    public List<Match> getAllMatches() throws Exception {
        return matchManager.getAllMatches();
    }
}
```

### 4. Data Access Object (DAO) Pattern

**Utilisation** : Séparation de la logique d'accès aux données.

```java
// Interface MatchDAO.java
public interface MatchDAO {
    Match create(Match match) throws Exception;
    void update(Match match) throws Exception;
    Match getById(int id) throws Exception;
    List<Match> getAll() throws Exception;
    List<Match> getByClub(int clubId) throws Exception;
    int getTypeSportId(int matchId) throws Exception;
    LocalDateTime getCompositionDeadline(int matchId) throws Exception;
}
```

### 5. MVC (Model-View-Controller) Pattern

**Utilisation** : Séparation entre modèle de données, vue (UI) et contrôleur.

| Composant | Classes |
|-----------|---------|
| **Model** | `Match`, `MatchRequest`, `Composition`, `RoleAssignment`, `SmallEvent` |
| **View** | `AdminDashboardFrame`, `CoachDashboardFrame` |
| **Controller** | `MatchController`, `MatchRequestController`, `CompositionController` |

---

## Structure des composants

### Modèles de données (Services Layer)

| Classe | Description | Attributs principaux |
|--------|-------------|---------------------|
| `Match` | Représente un match sportif | `id`, `typeSportId`, `homeTeamId`, `awayTeamId`, `dateTime`, `location`, `referee`, `compositionDeadline`, `status`, `homeScore`, `awayScore` |
| `MatchRequest` | Demande de match par un coach | `id`, `requesterClubId`, `opponentClubId`, `homeTeamId`, `awayTeamId`, `typeSportId`, `requestedDateTime`, `location`, `referee`, `requestedBy`, `status`, `requestDate`, `matchId` |
| `Composition` | Feuille de match d'une équipe | `matchId`, `teamId`, `assignments` (List<RoleAssignment>) |
| `RoleAssignment` | Attribution d'un joueur à un rôle | `role`, `slotIndex`, `playerId` |
| `SmallEvent` | Événement de match pour statistiques | `id`, `type`, `description`, `teamId`, `playerId`, `timestamp`, `period` |

### Énumérations

```java
// MatchStatus.java
public enum MatchStatus {
    SCHEDULED,   // Match planifié
    FINISHED,    // Match terminé
    CANCELLED,   // Match annulé
    FORFEIT      // Forfait
}

// MatchRequestStatus.java
public enum MatchRequestStatus {
    PENDING,     // En attente de validation
    APPROVED,    // Demande approuvée
    REJECTED     // Demande refusée
}
```

---

## Modèle de données

### Diagramme de classes simplifié

```
┌──────────────────┐      ┌───────────────────┐
│     TypeSport    │      │       Club        │
├──────────────────┤      ├───────────────────┤
│ - id: int        │      │ - clubId: int     │
│ - nom: String    │      │ - name: String    │
│ - nbJoueurs: int │      │ - sportId: int    │
│ - roles: List    │      │ - managerId: String│
└────────┬─────────┘      └────────┬──────────┘
         │                         │
         │                         │
         ▼                         ▼
┌────────────────────────────────────────────┐
│                   Match                     │
├────────────────────────────────────────────┤
│ - id: Integer                               │
│ - typeSportId: int                          │
│ - homeTeamId: int                           │
│ - awayTeamId: int                           │
│ - dateTime: LocalDateTime                   │
│ - location: String                          │
│ - referee: String                           │
│ - compositionDeadline: LocalDateTime        │
│ - status: MatchStatus                       │
│ - homeScore: Integer                        │
│ - awayScore: Integer                        │
├────────────────────────────────────────────┤
│ + determineResultForTeam(teamId): String    │
└────────────────────────────────────────────┘
         │
         │ 1:N
         ▼
┌────────────────────────────────────────────┐
│               Composition                   │
├────────────────────────────────────────────┤
│ - matchId: int                              │
│ - teamId: int                               │
│ - assignments: List<RoleAssignment>         │
└────────────────────────────────────────────┘
         │
         │ 1:N
         ▼
┌────────────────────────────────────────────┐
│              RoleAssignment                 │
├────────────────────────────────────────────┤
│ - role: String                              │
│ - slotIndex: int                            │
│ - playerId: String                          │
└────────────────────────────────────────────┘
```

---

## Flux de données et séquences

### Séquence 1 : Création d'une demande de match (Coach)

```
┌───────┐          ┌────────────┐      ┌────────────────┐      ┌───────────────┐      ┌──────────────────┐      ┌────┐
│ Coach │          │CoachDashboard│   │MatchRequest   │      │MatchRequest   │      │PostgresMatch     │      │ DB │
│       │          │Frame         │   │Controller     │      │Facade/Manager │      │RequestDAO        │      │    │
└───┬───┘          └─────┬───────┘   └───────┬───────┘      └───────┬───────┘      └────────┬─────────┘      └──┬─┘
    │                    │                    │                      │                       │                   │
    │ Remplit formulaire │                    │                      │                       │                   │
    │ et clique "Demander"│                   │                      │                       │                   │
    │───────────────────>│                    │                      │                       │                   │
    │                    │                    │                      │                       │                   │
    │                    │handleCreateRequest │                      │                       │                   │
    │                    │(MatchRequest)     │                      │                       │                   │
    │                    │───────────────────>│                      │                       │                   │
    │                    │                    │                      │                       │                   │
    │                    │                    │ createRequest(req)   │                       │                   │
    │                    │                    │─────────────────────>│                       │                   │
    │                    │                    │                      │                       │                   │
    │                    │                    │                      │ validateRequest()     │                   │
    │                    │                    │                      │──────┐                │                   │
    │                    │                    │                      │      │                │                   │
    │                    │                    │                      │<─────┘                │                   │
    │                    │                    │                      │                       │                   │
    │                    │                    │                      │ setStatus(PENDING)    │                   │
    │                    │                    │                      │──────┐                │                   │
    │                    │                    │                      │      │                │                   │
    │                    │                    │                      │<─────┘                │                   │
    │                    │                    │                      │                       │                   │
    │                    │                    │                      │ create(request)       │                   │
    │                    │                    │                      │──────────────────────>│                   │
    │                    │                    │                      │                       │                   │
    │                    │                    │                      │                       │ INSERT INTO       │
    │                    │                    │                      │                       │ match_requests    │
    │                    │                    │                      │                       │──────────────────>│
    │                    │                    │                      │                       │                   │
    │                    │                    │                      │                       │ RETURNING id      │
    │                    │                    │                      │                       │<──────────────────│
    │                    │                    │                      │                       │                   │
    │                    │                    │                      │ MatchRequest (with id)│                   │
    │                    │                    │                      │<──────────────────────│                   │
    │                    │                    │                      │                       │                   │
    │                    │                    │ MatchRequest         │                       │                   │
    │                    │                    │<─────────────────────│                       │                   │
    │                    │                    │                      │                       │                   │
    │                    │ MatchRequest       │                      │                       │                   │
    │                    │<───────────────────│                      │                       │                   │
    │                    │                    │                      │                       │                   │
    │ "Demande envoyée"  │                    │                      │                       │                   │
    │<───────────────────│                    │                      │                       │                   │
```

### Séquence 2 : Approbation d'une demande de match (Admin)

```
┌───────┐       ┌──────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌──────────────┐
│ Admin │       │AdminDashboard│     │MatchRequest    │     │MatchRequest     │     │MatchManager  │
│       │       │Frame         │     │Controller      │     │Manager          │     │              │
└───┬───┘       └──────┬───────┘     └───────┬────────┘     └────────┬────────┘     └──────┬───────┘
    │                  │                      │                       │                     │
    │ Sélectionne      │                      │                       │                     │
    │ demande et       │                      │                       │                     │
    │ clique "Valider" │                      │                       │                     │
    │─────────────────>│                      │                       │                     │
    │                  │                      │                       │                     │
    │                  │handleApproveRequest  │                       │                     │
    │                  │(requestId)           │                       │                     │
    │                  │─────────────────────>│                       │                     │
    │                  │                      │                       │                     │
    │                  │                      │ approveRequest(id)    │                     │
    │                  │                      │──────────────────────>│                     │
    │                  │                      │                       │                     │
    │                  │                      │                       │ getById(requestId)  │
    │                  │                      │                       │──────┐              │
    │                  │                      │                       │      │              │
    │                  │                      │                       │<─────┘              │
    │                  │                      │                       │                     │
    │                  │                      │                       │ new Match(...)      │
    │                  │                      │                       │──────┐              │
    │                  │                      │                       │      │              │
    │                  │                      │                       │<─────┘              │
    │                  │                      │                       │                     │
    │                  │                      │                       │ createMatch(match)  │
    │                  │                      │                       │────────────────────>│
    │                  │                      │                       │                     │
    │                  │                      │                       │                     │ validateMatch()
    │                  │                      │                       │                     │─────┐
    │                  │                      │                       │                     │     │
    │                  │                      │                       │                     │<────┘
    │                  │                      │                       │                     │
    │                  │                      │                       │                     │ matchDAO.create()
    │                  │                      │                       │                     │─────┐
    │                  │                      │                       │                     │     │
    │                  │                      │                       │                     │<────┘
    │                  │                      │                       │                     │
    │                  │                      │                       │ Match (avec ID)     │
    │                  │                      │                       │<────────────────────│
    │                  │                      │                       │                     │
    │                  │                      │                       │ updateStatus(       │
    │                  │                      │                       │   APPROVED, matchId)│
    │                  │                      │                       │──────┐              │
    │                  │                      │                       │      │              │
    │                  │                      │                       │<─────┘              │
    │                  │                      │                       │                     │
    │                  │                      │ true                  │                     │
    │                  │                      │<──────────────────────│                     │
    │                  │                      │                       │                     │
    │                  │ true                 │                       │                     │
    │                  │<─────────────────────│                       │                     │
    │                  │                       │                       │                     │
    │ Refresh tables   │                       │                       │                     │
    │<─────────────────│                       │                       │                     │
```

### Séquence 3 : Création d'une composition d'équipe (Coach)

```
┌───────┐       ┌──────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌──────────────────┐
│ Coach │       │CoachDashboard│     │Composition     │     │Composition      │     │PostgresComposition│
│       │       │Frame         │     │Controller      │     │Manager          │     │DAO               │
└───┬───┘       └──────┬───────┘     └───────┬────────┘     └────────┬────────┘     └────────┬─────────┘
    │                  │                      │                       │                       │
    │ Double-clic sur  │                      │                       │                       │
    │ un match         │                      │                       │                       │
    │─────────────────>│                      │                       │                       │
    │                  │                      │                       │                       │
    │                  │ openComposition      │                       │                       │
    │                  │ Dialog(match)        │                       │                       │
    │                  │─────┐                │                       │                       │
    │                  │     │                │                       │                       │
    │ Dialog avec      │<────┘                │                       │                       │
    │ formulaire roles │                      │                       │                       │
    │<─────────────────│                      │                       │                       │
    │                  │                      │                       │                       │
    │ Remplit les      │                      │                       │                       │
    │ joueurs et       │                      │                       │                       │
    │ clique "Enregistrer"                    │                       │                       │
    │─────────────────>│                      │                       │                       │
    │                  │                      │                       │                       │
    │                  │handleSaveComposition │                       │                       │
    │                  │(Composition)         │                       │                       │
    │                  │─────────────────────>│                       │                       │
    │                  │                      │                       │                       │
    │                  │                      │ saveComposition(comp) │                       │
    │                  │                      │──────────────────────>│                       │
    │                  │                      │                       │                       │
    │                  │                      │                       │ matchDAO.getTypeSportId()
    │                  │                      │                       │──────┐                │
    │                  │                      │                       │      │                │
    │                  │                      │                       │<─────┘                │
    │                  │                      │                       │                       │
    │                  │                      │                       │ matchDAO.getDeadline()│
    │                  │                      │                       │──────┐                │
    │                  │                      │                       │      │                │
    │                  │                      │                       │<─────┘                │
    │                  │                      │                       │                       │
    │                  │                      │                       │ validateComposition() │
    │                  │                      │                       │──────┐                │
    │                  │                      │                       │      │                │
    │                  │                      │                       │<─────┘                │
    │                  │                      │                       │                       │
    │                  │                      │                       │ compositionDAO.save   │
    │                  │                      │                       │ Composition()         │
    │                  │                      │                       │──────────────────────>│
    │                  │                      │                       │                       │
    │                  │                      │                       │                       │ DELETE old comp
    │                  │                      │                       │                       │─────┐
    │                  │                      │                       │                       │     │
    │                  │                      │                       │                       │<────┘
    │                  │                      │                       │                       │
    │                  │                      │                       │                       │ INSERT new comp
    │                  │                      │                       │                       │─────┐
    │                  │                      │                       │                       │     │
    │                  │                      │                       │                       │<────┘
    │                  │                      │                       │                       │
    │                  │                      │                       │                       │ Generate PARTICIPATION
    │                  │                      │                       │                       │ stats
    │                  │                      │                       │                       │─────┐
    │                  │                      │                       │                       │     │
    │                  │                      │                       │                       │<────┘
    │                  │                      │                       │                       │
    │                  │                      │                       │ true                  │
    │                  │                      │                       │<──────────────────────│
    │                  │                      │                       │                       │
    │                  │                      │ true                  │                       │
    │                  │                      │<──────────────────────│                       │
    │                  │                      │                       │                       │
    │                  │ true                 │                       │                       │
    │                  │<─────────────────────│                       │                       │
    │                  │                       │                       │                       │
    │ Ferme dialog     │                       │                       │                       │
    │<─────────────────│                       │                       │                       │
```

---

## Fonctionnalités principales

### 1. Gestion des Matchs (Admin)

| Fonctionnalité | Description | Classe/Méthode |
|----------------|-------------|----------------|
| **Créer un match** | Planifier un nouveau match avec tous les détails | `MatchManager.createMatch()` |
| **Modifier un match** | Mettre à jour les informations d'un match | `MatchManager.updateMatch()` |
| **Lister les matchs** | Afficher tous les matchs ou par club | `MatchManager.getAllMatches()`, `getMatchesByClub()` |
| **Terminer un match** | Enregistrer le score et passer en FINISHED | Génère automatiquement les stats |

### 2. Demandes de Match (Coach → Admin)

| Fonctionnalité | Description | Classe/Méthode |
|----------------|-------------|----------------|
| **Créer une demande** | Coach soumet une demande de match | `MatchRequestManager.createRequest()` |
| **Lister demandes pending** | Admin voit les demandes en attente | `MatchRequestManager.getPendingRequests()` |
| **Approuver demande** | Crée automatiquement le match | `MatchRequestManager.approveRequest()` |
| **Refuser demande** | Passe le statut à REJECTED | `MatchRequestManager.rejectRequest()` |

### 3. Compositions d'équipe (Coach)

| Fonctionnalité | Description | Classe/Méthode |
|----------------|-------------|----------------|
| **Créer/modifier composition** | Attribution des joueurs aux rôles | `CompositionManager.saveComposition()` |
| **Validation métier** | Vérifie nombre de joueurs, rôles valides, deadline | `CompositionManager.validateComposition()` |
| **Génération stats** | Crée événements PARTICIPATION pour chaque joueur | `PostgresCompositionDAO.generateParticipationStats()` |

### 4. Validations métier

```java
// MatchManager.validateMatch()
private void validateMatch(Match m, boolean creating) throws Exception {
    if (m == null) throw new Exception("Match null");
    if (m.getTypeSportId() <= 0) throw new Exception("TypeSport invalide");
    
    TypeSport ts = typeSportManager.getTypeSportById(m.getTypeSportId());
    if (ts == null) throw new Exception("TypeSport introuvable");
    
    if (m.getHomeTeamId() <= 0 || m.getAwayTeamId() <= 0) 
        throw new Exception("Teams invalides");
    if (m.getHomeTeamId() == m.getAwayTeamId()) 
        throw new Exception("Même équipe home/away");
    if (m.getDateTime() == null) 
        throw new Exception("Date/heure manquante");
    
    if (m.getCompositionDeadline() != null && 
        m.getCompositionDeadline().isAfter(m.getDateTime())) {
        throw new Exception("Deadline de composition incohérente");
    }
}
```

```java
// CompositionManager.validateComposition()
private boolean validateComposition(List<RoleAssignment> assignments, TypeSport typeSport) {
    // A) Nombre exact de joueurs
    if (assignments.size() != typeSport.getNbJoueurs()) return false;
    
    // B) Joueurs uniques
    Set<String> players = new HashSet<>();
    for (RoleAssignment a : assignments) {
        if (a.getPlayerId() == null || a.getPlayerId().isBlank()) return false;
        if (!players.add(a.getPlayerId())) return false;
    }
    
    // C) Vérification des rôles selon template du sport
    // ...
    return true;
}
```

---

## Intégrations avec d'autres modules

### 1. Intégration avec Statistics Management

Lorsqu'un match est terminé (`status = FINISHED`), le système génère automatiquement des événements statistiques :

```java
// PostgresMatchDAO.generateStatsAfterMatch()
private void generateStatsAfterMatch(Match m) throws SQLException {
    PostgresStatDAO statDAO = new PostgresStatDAO(con);
    Timestamp now = new Timestamp(System.currentTimeMillis());
    String period = "Saison " + m.getDateTime().getYear();

    // Stats équipe domicile
    String resultHome = (m.getHomeScore() > m.getAwayScore()) ? "VICTOIRE" :
            (m.getHomeScore() < m.getAwayScore()) ? "DEFAITE" : "NUL";

    statDAO.addSmallEvent(new SmallEvent(0, "MATCH", "Match joué", 
        m.getHomeTeamId(), null, now, period));
    statDAO.addSmallEvent(new SmallEvent(0, resultHome, "Résultat final", 
        m.getHomeTeamId(), null, now, period));

    // Stats équipe extérieure
    String resultAway = (m.getAwayScore() > m.getHomeScore()) ? "VICTOIRE" :
            (m.getAwayScore() < m.getHomeScore()) ? "DEFAITE" : "NUL";

    statDAO.addSmallEvent(new SmallEvent(0, "MATCH", "Match joué", 
        m.getAwayTeamId(), null, now, period));
    statDAO.addSmallEvent(new SmallEvent(0, resultAway, "Résultat final", 
        m.getAwayTeamId(), null, now, period));
}
```

### 2. Intégration avec Composition → Statistics

Lors de l'enregistrement d'une composition, des événements de participation sont générés :

```java
// PostgresCompositionDAO.generateParticipationStats()
private void generateParticipationStats(int matchId, int teamId, 
    List<RoleAssignment> assignments) throws SQLException {
    
    PostgresStatDAO statDAO = new PostgresStatDAO(con);
    Timestamp now = new Timestamp(System.currentTimeMillis());

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

### 3. Intégration avec Type Sport Management

Les matchs et compositions dépendent de la configuration des types de sport :

- **Nombre de joueurs** : `TypeSport.getNbJoueurs()` définit combien de joueurs sont requis
- **Rôles** : `TypeSport.getRoles()` définit les postes disponibles pour la composition

### 4. Intégration avec Club Management

Les matchs relient les clubs via :
- `homeTeamId` → `clubs.clubid`
- `awayTeamId` → `clubs.clubid`

---

## Interface utilisateur

### Admin Dashboard - Gestion des Matchs

**Fonctionnalités disponibles** :
- Formulaire complet de création/modification de match
- Tableau des demandes de match en attente (avec boutons Valider/Refuser)
- Tableau des matchs planifiés avec détails

**Champs du formulaire** :
| Champ | Type | Description |
|-------|------|-------------|
| Sport | ComboBox | Sélection de la discipline |
| Domicile | ComboBox | Club jouant à domicile |
| Extérieur | ComboBox | Club visiteur |
| Date | DatePicker | Date du match |
| Heure | TextField | Heure au format HH:mm |
| Lieu | TextField | Stade/emplacement |
| Arbitre | TextField | Nom de l'arbitre |
| Statut | ComboBox | SCHEDULED, FINISHED, CANCELLED, FORFEIT |
| Deadline composition | DatePicker + TextField | Date limite pour soumettre la composition |
| Score Domicile/Extérieur | TextField | Scores finaux |

### Coach Dashboard - Demandes de Match

**Fonctionnalités disponibles** :
- Formulaire de demande de match
- Tableau des demandes du coach (avec statut coloré)
- Tableau des matchs du club
- Double-clic pour ouvrir le dialogue de composition

**Dialogue de composition** :
- Liste dynamique des rôles selon le sport du match
- Champs texte pour saisir l'ID de chaque joueur
- Validation des règles métier à l'enregistrement

### Auto-refresh

Le `CoachDashboardFrame` implémente un rafraîchissement automatique toutes les 5 secondes :

```java
private void startMatchAutoRefresh() {
    if (matchRefreshTimeline == null) {
        matchRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            refreshMatchRequestList();
            refreshMatchList();
            updateLastRefreshLabel();
        }));
        matchRefreshTimeline.setCycleCount(Animation.INDEFINITE);
    }
    if (matchRefreshTimeline.getStatus() != Animation.Status.RUNNING) {
        matchRefreshTimeline.play();
    }
}
```

---

## Base de données

### Schéma des tables Event Management

```sql
-- Table des Matchs
CREATE TABLE matchs (
    id SERIAL PRIMARY KEY,
    type_sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
    home_team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    away_team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    datetime TIMESTAMP NOT NULL,
    location VARCHAR(255),
    referee VARCHAR(100),
    composition_deadline TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    home_score INT DEFAULT 0,
    away_score INT DEFAULT 0
);

-- Table des Demandes de Match
CREATE TABLE match_requests (
    id SERIAL PRIMARY KEY,
    requester_club_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    opponent_club_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    home_team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    away_team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    type_sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
    requested_datetime TIMESTAMP NOT NULL,
    location VARCHAR(255),
    referee VARCHAR(100),
    requested_by VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    match_id INT REFERENCES matchs(id) ON DELETE SET NULL
);

-- Table des Compositions
CREATE TABLE match_composition (
    match_id INT REFERENCES matchs(id) ON DELETE CASCADE,
    team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(100),
    slot_index INT,
    PRIMARY KEY (match_id, team_id, slot_index)
);

-- Table des Événements Statistiques
CREATE TABLE small_events (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50),
    description TEXT,
    team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
    player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
    period VARCHAR(50),
    event_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Relations entre tables

```
type_sports ──┐
              │
              ├──> matchs ──────> match_composition
              │      │
clubs ────────┼──────┤
              │      │
              │      └──────> match_requests
              │
users ────────┴──────────────> small_events
```

---

## Conclusion

Le module Event Management de SportifyClubManager est une implémentation robuste suivant les bonnes pratiques de développement logiciel :

### Points forts

1. **Architecture en couches** : Séparation claire des responsabilités (UI → Controller → Facade → Manager → DAO)
2. **Design patterns** : Utilisation appropriée de Singleton, Abstract Factory, Facade et DAO
3. **Intégration cohérente** : Le module s'intègre naturellement avec Statistics et Type Sport Management
4. **Génération automatique de statistiques** : Les événements sont créés automatiquement lors de la fin des matchs
5. **Validation métier complète** : Vérifications des règles à plusieurs niveaux

### Technologies utilisées

- **Langage** : Java 17
- **UI** : JavaFX 21.0.1
- **Base de données** : PostgreSQL
- **Architecture** : MVC + Layered Architecture
- **Build** : Maven

### Fichiers clés

| Catégorie | Fichiers |
|-----------|----------|
| **Modèles** | `Match.java`, `MatchRequest.java`, `Composition.java`, `RoleAssignment.java`, `SmallEvent.java` |
| **Managers** | `MatchManager.java`, `MatchRequestManager.java`, `CompositionManager.java` |
| **DAOs** | `PostgresMatchDAO.java`, `PostgresMatchRequestDAO.java`, `PostgresCompositionDAO.java` |
| **Facades** | `MatchFacade.java`, `MatchRequestFacade.java`, `CompositionFacade.java` |
| **Controllers** | `MatchController.java`, `MatchRequestController.java`, `CompositionController.java` |
| **UI** | `AdminDashboardFrame.java`, `CoachDashboardFrame.java` |
| **Factory** | `AbstractFactory.java`, `PostgresFactory.java` |
| **Enums** | `MatchStatus.java`, `MatchRequestStatus.java` |
