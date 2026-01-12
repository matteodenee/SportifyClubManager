---------------------------------------------------------
-- NETTOYAGE (Ordre respectant les contraintes)
---------------------------------------------------------
DROP TABLE IF EXISTS team_member CASCADE;
DROP TABLE IF EXISTS team CASCADE;
DROP TABLE IF EXISTS match_composition CASCADE;
DROP TABLE IF EXISTS match_requests CASCADE;
DROP TABLE IF EXISTS matchs CASCADE;
DROP TABLE IF EXISTS event_participation CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS equipment_reservations CASCADE;
DROP TABLE IF EXISTS equipments CASCADE;
DROP TABLE IF EXISTS equipment_types CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS conversation_participant CASCADE;
DROP TABLE IF EXISTS conversation CASCADE;
DROP TABLE IF EXISTS small_events CASCADE;
DROP TABLE IF EXISTS membership_requests CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS licences CASCADE;
DROP TABLE IF EXISTS sport_roles CASCADE;
DROP TABLE IF EXISTS sport_stats CASCADE;
DROP TABLE IF EXISTS clubs CASCADE;
DROP TABLE IF EXISTS type_sports CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS entrainement_participation CASCADE;
DROP TABLE IF EXISTS entrainements CASCADE;

---------------------------------------------------------
-- TABLES DE BASE
---------------------------------------------------------

-- 1. Table Utilisateurs
CREATE TABLE users (
                       id VARCHAR(50) PRIMARY KEY,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100),
                       email VARCHAR(100),
                       role VARCHAR(20) NOT NULL -- ADMIN, DIRECTOR, COACH, MEMBER
);

-- 2. Table des Disciplines (TypeSport)
CREATE TABLE type_sports (
                             id SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             description TEXT,
                             nb_joueurs INT
);

-- 3. Table Clubs
CREATE TABLE clubs (
                       clubid SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       description TEXT,
                       sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
                       maxcapacity INT,
                       status VARCHAR(20) DEFAULT 'Active',
                       manager_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL,
                       UNIQUE (manager_id)
);

---------------------------------------------------------
-- GESTION DES RÔLES, STATS ET MATCHS
---------------------------------------------------------

-- 4. Rôles spécifiques par sport
CREATE TABLE sport_roles (
                             id SERIAL PRIMARY KEY,
                             sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
                             role_name VARCHAR(100) NOT NULL
);

-- 5. Statistiques spécifiques par sport
CREATE TABLE sport_stats (
                             id SERIAL PRIMARY KEY,
                             sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
                             stat_name VARCHAR(100) NOT NULL
);

-- 6.1 Table Equipes
CREATE TABLE team (
                      id_team SERIAL PRIMARY KEY,
                      nom VARCHAR(100) NOT NULL,
                      categorie VARCHAR(100),
                      clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                      coach_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL,
                      type_sport_id INT REFERENCES type_sports(id) ON DELETE SET NULL
);

-- 6.2 Table des membres d'equipe
CREATE TABLE team_member (
                             id_team INT REFERENCES team(id_team) ON DELETE CASCADE,
                             id_user VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                             PRIMARY KEY (id_team, id_user)
);

-- 6.3 Entrainements
CREATE TABLE entrainements (
                               id SERIAL PRIMARY KEY,
                               date DATE NOT NULL,
                               heure TIME NOT NULL,
                               lieu VARCHAR(255),
                               activite VARCHAR(255),
                               clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                               team_id INT REFERENCES team(id_team) ON DELETE SET NULL
);

-- 6.4 Participation aux entrainements
CREATE TABLE entrainement_participation (
                                            entrainement_id INT REFERENCES entrainements(id) ON DELETE CASCADE,
                                            user_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                            status VARCHAR(20) DEFAULT 'PENDING',
                                            PRIMARY KEY (entrainement_id, user_id)
);

-- 6.5 Table des Matchs (Nouveau)
CREATE TABLE matchs (
                        id SERIAL PRIMARY KEY,
                        type_sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
                        home_team_id INT REFERENCES team(id_team) ON DELETE CASCADE,
                        away_team_id INT REFERENCES team(id_team) ON DELETE CASCADE,
                        datetime TIMESTAMP NOT NULL,
                        location VARCHAR(255),
                        referee VARCHAR(100),
                        composition_deadline TIMESTAMP,
                        status VARCHAR(20) DEFAULT 'SCHEDULED',
                        home_score INT DEFAULT 0,
                        away_score INT DEFAULT 0
);

-- 6.3 Evenements
CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(255) NOT NULL,
                        description TEXT,
                        date_debut TIMESTAMP NOT NULL,
                        duree_minutes INT NOT NULL,
                        lieu VARCHAR(255),
                        type VARCHAR(50),
                        club_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                        createur_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL
);

-- 6.4 Participation aux evenements
CREATE TABLE event_participation (
                                     event_id INT REFERENCES events(id) ON DELETE CASCADE,
                                     user_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                     status VARCHAR(20) NOT NULL DEFAULT 'MAYBE',
                                     PRIMARY KEY (event_id, user_id)
);

-- 6.5 Types d'equipement
CREATE TABLE equipment_types (
                                 id SERIAL PRIMARY KEY,
                                 name VARCHAR(100) NOT NULL,
                                 description TEXT
);

-- 6.6 Equipements
CREATE TABLE equipments (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            type VARCHAR(100) NOT NULL,
                            condition VARCHAR(100) NOT NULL,
                            quantity INT NOT NULL,
                            type_id INT REFERENCES equipment_types(id) ON DELETE SET NULL,
                            club_id INT REFERENCES clubs(clubid) ON DELETE SET NULL
);

-- 6.7 Reservations d'equipements
CREATE TABLE equipment_reservations (
                                        id SERIAL PRIMARY KEY,
                                        equipment_id INT REFERENCES equipments(id) ON DELETE CASCADE,
                                        user_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                        start_date DATE NOT NULL,
                                        end_date DATE NOT NULL,
                                        status VARCHAR(20) DEFAULT 'PENDING'
);

-- 6.8 Communication (Chat)
CREATE TABLE conversation (
                              id SERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              type VARCHAR(20) NOT NULL, -- GLOBAL, GROUP
                              created_by VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL,
                              club_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE conversation_participant (
                                          conversation_id INT REFERENCES conversation(id) ON DELETE CASCADE,
                                          user_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                          PRIMARY KEY (conversation_id, user_id)
);

CREATE TABLE message (
                         id SERIAL PRIMARY KEY,
                         conversation_id INT REFERENCES conversation(id) ON DELETE CASCADE,
                         sender_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL,
                         content TEXT NOT NULL,
                         sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6.1 Demandes de match (Coach -> Admin)
CREATE TABLE match_requests (
                                id SERIAL PRIMARY KEY,
                                requester_club_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                opponent_club_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                home_team_id INT REFERENCES team(id_team) ON DELETE CASCADE,
                                away_team_id INT REFERENCES team(id_team) ON DELETE CASCADE,
                                type_sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
                                requested_datetime TIMESTAMP NOT NULL,
                                location VARCHAR(255),
                                referee VARCHAR(100),
                                requested_by VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL,
                                status VARCHAR(20) DEFAULT 'PENDING',
                                request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                match_id INT REFERENCES matchs(id) ON DELETE SET NULL
);

-- 7. Table Composition des Matchs (Nouveau)
CREATE TABLE match_composition (
                                   match_id INT REFERENCES matchs(id) ON DELETE CASCADE,
                                   team_id INT REFERENCES team(id_team) ON DELETE CASCADE,
                                   player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                   role VARCHAR(100),
                                   slot_index INT,
                                   PRIMARY KEY (match_id, team_id, slot_index)
);

---------------------------------------------------------
-- GESTION DES MEMBRES ET LICENCES
---------------------------------------------------------

-- 8. Table Licences
CREATE TABLE licences (
                          id VARCHAR(255) PRIMARY KEY,
                          sport_id INT REFERENCES type_sports(id) ON DELETE CASCADE,
                          type_licence VARCHAR(50) NOT NULL,
                          statut VARCHAR(50) NOT NULL,
                          date_demande DATE DEFAULT CURRENT_DATE,
                          date_debut DATE,
                          date_fin DATE,
                          membre_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                          date_decision DATE,
                          commentaire_admin TEXT
);

-- 9. Table Membres (Liaison User-Club)
CREATE TABLE members (
                         userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                         clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                         role_in_club VARCHAR(50),
                         PRIMARY KEY (userid, clubid)
);

-- 10. Demandes d'adhésion
CREATE TABLE membership_requests (
                                     requestid SERIAL PRIMARY KEY,
                                     clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                     userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                     status VARCHAR(20) DEFAULT 'PENDING',
                                     role_in_club VARCHAR(20) DEFAULT 'JOUEUR',
                                     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Événements (Stats de match légères)
CREATE TABLE small_events (
                              id SERIAL PRIMARY KEY,
                              type VARCHAR(50),
                              description TEXT,
                              team_id INT REFERENCES team(id_team) ON DELETE CASCADE,
                              player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                              period VARCHAR(50),
                              event_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              match_id INT REFERENCES matchs(id) ON DELETE CASCADE
);

CREATE INDEX idx_entrainements_clubid ON entrainements(clubid);
CREATE INDEX idx_entrainements_date ON entrainements(date);
CREATE INDEX idx_entrainements_team ON entrainements(team_id);
CREATE INDEX idx_participation_entrainement ON entrainement_participation(entrainement_id);
CREATE INDEX idx_participation_user ON entrainement_participation(user_id);
CREATE INDEX idx_events_clubid ON events(club_id);
CREATE INDEX idx_events_date ON events(date_debut);
CREATE INDEX idx_event_participation_event ON event_participation(event_id);
CREATE INDEX idx_event_participation_user ON event_participation(user_id);
CREATE INDEX idx_small_events_match ON small_events(match_id);
CREATE INDEX idx_equipment_types_name ON equipment_types(name);
CREATE INDEX idx_equipments_type_id ON equipments(type_id);
CREATE INDEX idx_equipments_club_id ON equipments(club_id);
CREATE INDEX idx_equipment_reservations_equipment ON equipment_reservations(equipment_id);
CREATE INDEX idx_equipment_reservations_user ON equipment_reservations(user_id);
CREATE INDEX idx_conversation_type ON conversation(type);
CREATE INDEX idx_conversation_club ON conversation(club_id);
CREATE INDEX idx_conversation_participant_user ON conversation_participant(user_id);
CREATE INDEX idx_message_conversation ON message(conversation_id);


---------------------------------------------------------
-- DONNÉES DE TEST
---------------------------------------------------------

-- 1. Utilisateurs
INSERT INTO users (id, password, name, email, role) VALUES
                                                        ('admin', 'admin123', 'Super Admin', 'admin@sportify.com', 'ADMIN'),
                                                        ('dir_foot', 'dir123', 'Marc Football', 'marc@sportify.com', 'DIRECTOR'),
                                                        ('dir_rugby', 'dir123', 'Claire Rugby', 'claire@sportify.com', 'DIRECTOR'),
                                                        ('dir_basket', 'dir123', 'Lucas Basket', 'lucas@sportify.com', 'DIRECTOR'),
                                                        ('dir_hand', 'dir123', 'Nina Handball', 'nina@sportify.com', 'DIRECTOR'),
                                                        ('dir_volley', 'dir123', 'Omar Volley', 'omar@sportify.com', 'DIRECTOR'),
                                                        ('dir_free1', 'dir123', 'Alex Martin', 'alex.martin@sportify.com', 'DIRECTOR'),
                                                        ('dir_free2', 'dir123', 'Sofia Bernard', 'sofia.bernard@sportify.com', 'DIRECTOR'),
                                                        ('dir_tennis', 'dir123', 'Elsa Tennis', 'elsa@sportify.com', 'DIRECTOR'),
                                                        ('coach_zidane', 'coach123', 'Zinedine Zidane', 'zizou@foot.com', 'COACH'),
                                                        ('coach_marie', 'coach123', 'Marie Dupont', 'marie@rugby.com', 'COACH'),
                                                        ('coach_tony', 'coach123', 'Tony Parker', 'tony@basket.com', 'COACH'),
                                                        ('coach_sarah', 'coach123', 'Sarah Martin', 'sarah@handball.com', 'COACH'),
                                                        ('coach_paul', 'coach123', 'Paul Lambert', 'paul@volley.com', 'COACH'),
                                                        ('user1', 'user123', 'Alice Membre', 'alice@gmail.com', 'MEMBER'),
                                                        ('user2', 'user123', 'Bob Sportif', 'bob@gmail.com', 'MEMBER'),
                                                        ('user3', 'user123', 'Chloé Garnier', 'chloe@gmail.com', 'MEMBER'),
                                                        ('user4', 'user123', 'David Morel', 'david@gmail.com', 'MEMBER'),
                                                        ('user5', 'user123', 'Emma Petit', 'emma@gmail.com', 'MEMBER'),
                                                        ('user6', 'user123', 'Farid Benali', 'farid@gmail.com', 'MEMBER'),
                                                        ('user7', 'user123', 'Giulia Rossi', 'giulia@gmail.com', 'MEMBER'),
                                                        ('user8', 'user123', 'Hugo Leroy', 'hugo@gmail.com', 'MEMBER'),
                                                        ('user9', 'user123', 'Ines Bernard', 'ines@gmail.com', 'MEMBER'),
                                                        ('user10', 'user123', 'Jules Noel', 'jules@gmail.com', 'MEMBER'),
                                                        ('user11', 'user123', 'Kenza Ali', 'kenza@gmail.com', 'MEMBER'),
                                                        ('user12', 'user123', 'Leo Martin', 'leo@gmail.com', 'MEMBER'),
                                                        ('user13', 'user123', 'Maya Colin', 'maya@gmail.com', 'MEMBER'),
                                                        ('user14', 'user123', 'Nabil Hamdi', 'nabil@gmail.com', 'MEMBER'),
                                                        ('user15', 'user123', 'Olivia Roy', 'olivia@gmail.com', 'MEMBER');

-- 2. Types de Sports
INSERT INTO type_sports (nom, description, nb_joueurs) VALUES
                                                           ('Football', 'Sport collectif 11 vs 11', 11),
                                                           ('Rugby', 'Sport de contact 15 vs 15', 15),
                                                           ('Basketball', 'Sport collectif 5 vs 5', 5),
                                                           ('Handball', 'Sport collectif 7 vs 7', 7),
                                                           ('Tennis', 'Sport individuel', 1),
                                                           ('Volleyball', 'Sport collectif 6 vs 6', 6);

INSERT INTO sport_roles (sport_id, role_name) VALUES
                                                   (1, 'Gardien'),
                                                   (1, 'Defenseur'),
                                                   (1, 'Defenseur'),
                                                   (1, 'Defenseur'),
                                                   (1, 'Defenseur'),
                                                   (1, 'Milieu'),
                                                   (1, 'Milieu'),
                                                   (1, 'Milieu'),
                                                   (1, 'Attaquant'),
                                                   (1, 'Attaquant'),
                                                   (1, 'Attaquant'),
                                                   (2, 'Pilier'),
                                                   (2, 'Pilier'),
                                                   (2, 'Talonneur'),
                                                   (2, 'Deuxieme ligne'),
                                                   (2, 'Deuxieme ligne'),
                                                   (2, 'Troisieme ligne'),
                                                   (2, 'Troisieme ligne'),
                                                   (2, 'Troisieme ligne'),
                                                   (2, 'Demi de melee'),
                                                   (2, 'Demi d ouverture'),
                                                   (2, 'Centre'),
                                                   (2, 'Centre'),
                                                   (2, 'Ailier'),
                                                   (2, 'Ailier'),
                                                   (2, 'Arriere'),
                                                   (3, 'Meneur'),
                                                   (3, 'Arriere'),
                                                   (3, 'Ailier'),
                                                   (3, 'Ailier fort'),
                                                   (3, 'Pivot'),
                                                   (4, 'Gardien'),
                                                   (4, 'Arriere gauche'),
                                                   (4, 'Arriere droit'),
                                                   (4, 'Demi-centre'),
                                                   (4, 'Ailier gauche'),
                                                   (4, 'Ailier droit'),
                                                   (4, 'Pivot'),
                                                   (5, 'Joueur'),
                                                   (6, 'Passeur'),
                                                   (6, 'Receptionneur'),
                                                   (6, 'Receptionneur'),
                                                   (6, 'Central'),
                                                   (6, 'Central'),
                                                   (6, 'Oppose');

INSERT INTO sport_stats (sport_id, stat_name) VALUES
                                                   (1, 'GOAL'),
                                                   (1, 'YELLOW_CARD'),
                                                   (1, 'RED_CARD'),
                                                   (1, 'CORNER'),
                                                   (2, 'TRY'),
                                                   (2, 'YELLOW_CARD'),
                                                   (2, 'RED_CARD'),
                                                   (3, 'POINT'),
                                                   (3, 'FOUL'),
                                                   (4, 'GOAL'),
                                                   (4, 'YELLOW_CARD'),
                                                   (5, 'ACE'),
                                                   (5, 'FAULT'),
                                                   (6, 'POINT'),
                                                   (6, 'BLOCK');

-- 3. Clubs
INSERT INTO clubs (name, description, sport_id, maxcapacity, manager_id) VALUES
                                                                                                            ('Olympique Sportify', 'Club formateur avec une forte dynamique locale.', 1, 50, 'dir_foot'),
                                                                                                            ('Rugby Club Erben', 'Club engagé dans la formation et la cohésion sportive.', 2, 30, 'dir_rugby'),
                                                                                                            ('Basket City', 'Club compétitif orienté performance collective.', 3, 25, 'dir_basket'),
                                                                                                            ('Handball United', 'Club régional axé sur la progression des membres.', 4, 28, 'dir_hand'),
                                                                                                            ('Tennis Academy', 'Club convivial centré sur l’accompagnement des joueurs.', 5, 40, 'dir_tennis'),
                                                                                                            ('Volley Stars', 'Club mixte avec des entraînements réguliers.', 6, 24, 'dir_volley');

-- 4. Liaison Coach et Membres
INSERT INTO members (userid, clubid, role_in_club) VALUES
                                                       ('coach_zidane', 1, 'COACH'),
                                                       ('coach_marie', 2, 'COACH'),
                                                       ('coach_tony', 3, 'COACH'),
                                                       ('coach_sarah', 4, 'COACH'),
                                                       ('coach_paul', 6, 'COACH'),
                                                       ('user1', 1, 'JOUEUR'),
                                                       ('user2', 1, 'JOUEUR'),
                                                       ('user3', 1, 'JOUEUR'),
                                                       ('user4', 2, 'JOUEUR'),
                                                       ('user5', 2, 'JOUEUR'),
                                                       ('user6', 2, 'JOUEUR'),
                                                       ('user7', 3, 'JOUEUR'),
                                                       ('user8', 3, 'JOUEUR'),
                                                       ('user9', 4, 'JOUEUR'),
                                                       ('user10', 4, 'JOUEUR'),
                                                       ('user11', 6, 'JOUEUR'),
                                                       ('user12', 6, 'JOUEUR'),
                                                       ('user13', 5, 'JOUEUR'),
                                                       ('user14', 5, 'JOUEUR'),
                                                       ('user15', 3, 'JOUEUR');

-- 4.1 Equipes
INSERT INTO team (nom, categorie, clubid, coach_id, type_sport_id) VALUES
                                                                        ('Equipe A', 'Senior', 1, 'coach_zidane', 1),
                                                                        ('Equipe B', 'U18', 1, NULL, 1),
                                                                        ('Rugby Seniors', 'Senior', 2, 'coach_marie', 2),
                                                                        ('Basket Pro', 'Senior', 3, 'coach_tony', 3),
                                                                        ('Handball A', 'Senior', 4, 'coach_sarah', 4),
                                                                        ('Volley Mixte', 'Senior', 6, 'coach_paul', 6),
                                                                        ('Tennis Loisirs', 'Senior', 5, NULL, 5);

-- 4.2 Membres d'equipe
INSERT INTO team_member (id_team, id_user) VALUES
                                               (1, 'user1'),
                                               (1, 'user2'),
                                               (1, 'user3'),
                                               (2, 'user8'),
                                               (2, 'user10'),
                                               (3, 'user4'),
                                               (3, 'user5'),
                                               (3, 'user6'),
                                               (4, 'user7'),
                                               (4, 'user15'),
                                               (5, 'user9'),
                                               (5, 'user10'),
                                               (6, 'user11'),
                                               (6, 'user12');

-- 4.3 Entrainements
INSERT INTO entrainements (date, heure, lieu, activite, clubid, team_id) VALUES
                                                                            ('2025-10-02', '18:00', 'Stade Sportify', 'Footing', 1, 1),
                                                                            ('2025-10-04', '19:30', 'Stade Sportify', 'Tactique', 1, 1),
                                                                            ('2025-10-03', '17:00', 'Rugby Park', 'Contact', 2, 3),
                                                                            ('2025-10-06', '18:30', 'Arena Basket', 'Shoot', 3, 4),
                                                                            ('2025-10-07', '20:00', 'Gymnase Central', 'Defense', 4, 5),
                                                                            ('2025-10-08', '09:00', 'Courts Municipaux', 'Service', 5, 7),
                                                                            ('2025-10-09', '18:30', 'Stade Sportify', 'Jeu réduit', 1, 2);

-- 4.4 Participation entrainements
INSERT INTO entrainement_participation (entrainement_id, user_id, status) VALUES
                                                                              (1, 'user1', 'CONFIRMED'),
                                                                              (1, 'user2', 'PENDING'),
                                                                              (2, 'user3', 'PRESENT'),
                                                                              (3, 'user4', 'ABSENT');

-- 4.5 Evenements
INSERT INTO events (nom, description, date_debut, duree_minutes, lieu, type, club_id, createur_id) VALUES
                                                                                                      ('Reunion d equipe', 'Point hebdo et planning', '2025-10-02 19:00:00', 60, 'Club House', 'MEETING', 1, 'dir_foot'),
                                                                                                      ('Soiree club', 'Evenement convivial', '2025-10-12 20:30:00', 120, 'Salle polyvalente', 'SOCIAL', 2, 'dir_rugby'),
                                                                                                      ('Tournoi interne', 'Mini-tournoi membres', '2025-10-20 10:00:00', 240, 'Gymnase Central', 'TOURNAMENT', 4, 'coach_sarah');

-- 4.6 Participation aux evenements
INSERT INTO event_participation (event_id, user_id, status) VALUES
                                                                (1, 'user1', 'GOING'),
                                                                (1, 'user2', 'MAYBE'),
                                                                (2, 'user4', 'GOING'),
                                                                (3, 'user9', 'NOT_GOING');

-- 4.7 Types d'equipement
INSERT INTO equipment_types (name, description) VALUES
                                                    ('Ballon', 'Ballons officiels'),
                                                    ('Maillot', 'Tenues de match'),
                                                    ('Cones', 'Plots d entrainement');

-- 4.8 Equipements
INSERT INTO equipments (name, type, condition, quantity, type_id, club_id) VALUES
                                                                              ('Ballon #1', 'Ballon', 'Neuf', 10, 1, 1),
                                                                              ('Ballon #2', 'Ballon', 'Bon', 8, 1, 1),
                                                                              ('Maillot domicile', 'Maillot', 'Bon', 25, 2, 1),
                                                                              ('Cones lot A', 'Cones', 'Use', 50, 3, 2);

-- 4.9 Reservations d'equipements
INSERT INTO equipment_reservations (equipment_id, user_id, start_date, end_date, status) VALUES
                                                                                            (1, 'user1', '2025-10-05', '2025-10-06', 'APPROVED'),
                                                                                            (2, 'user2', '2025-10-10', '2025-10-12', 'PENDING');

-- 5. MATCHS DE TEST (Pour ton nouveau MatchController)
INSERT INTO matchs (type_sport_id, home_team_id, away_team_id, datetime, location, referee, status) VALUES
                                                                                                        (1, 1, 2, '2025-06-15 20:45:00', 'Stade de France', 'M. Turpin', 'SCHEDULED'),
                                                                                                        (1, 1, 2, '2025-07-20 18:00:00', 'Parc des Princes', 'Mme Frappart', 'SCHEDULED'),
                                                                                                        (3, 4, 1, '2025-08-10 19:30:00', 'Arena Sportify', 'M. Lemoine', 'SCHEDULED'),
                                                                                                        (4, 5, 4, '2025-08-22 20:00:00', 'Gymnase Central', 'Mme Durant', 'SCHEDULED'),
                                                                                                        (6, 6, 1, '2025-09-05 21:00:00', 'Volley Dome', 'M. Perez', 'SCHEDULED'),
                                                                                                        (5, 7, 2, '2025-09-12 10:00:00', 'Courts Municipaux', 'Mme Gomez', 'SCHEDULED');

-- 5.0 MATCHS TERMINES (Club 1)
INSERT INTO matchs (type_sport_id, home_team_id, away_team_id, datetime, location, referee, status, home_score, away_score) VALUES
                                                                                                                                (1, 1, 2, '2025-01-12 18:00:00', 'Stade Sportify', 'M. Durand', 'FINISHED', 2, 1),
                                                                                                                                (1, 1, 3, '2025-02-02 20:30:00', 'Stade Sportify', 'S. Martin', 'FINISHED', 3, 0),
                                                                                                                                (1, 4, 1, '2025-02-18 17:00:00', 'Gymnase Central', 'C. Petit', 'FINISHED', 1, 1),
                                                                                                                                (1, 1, 5, '2025-03-03 19:00:00', 'Stade Sportify', 'J. Noel', 'FINISHED', 0, 2),
                                                                                                                                (1, 6, 1, '2025-03-21 20:00:00', 'Salle Volley', 'L. Henry', 'FINISHED', 1, 4),
                                                                                                                                (1, 1, 4, '2025-04-07 18:30:00', 'Stade Sportify', 'P. Lemoine', 'FINISHED', 2, 2),
                                                                                                                                (1, 2, 1, '2025-04-19 21:00:00', 'Stade Rugby', 'R. Garcia', 'FINISHED', 0, 1),
                                                                                                                                (1, 1, 6, '2025-05-05 18:00:00', 'Stade Sportify', 'A. Simon', 'FINISHED', 4, 3),
                                                                                                                                (1, 3, 1, '2025-05-24 19:45:00', 'Arena Basket', 'V. Roux', 'FINISHED', 1, 2),
                                                                                                                                (1, 1, 2, '2025-06-02 16:00:00', 'Stade Sportify', 'N. Morel', 'FINISHED', 3, 1);

INSERT INTO small_events (type, description, team_id, player_id, period, event_date, match_id) VALUES
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-01-12 18:00:00', 7),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-01-12 18:00:00', 7),
                                                                                                  ('GOAL', 'But marque', 2, 'user8', 'Saison 2025', '2025-01-12 18:00:00', 7),
                                                                                                  ('YELLOW_CARD', 'Carton jaune', 1, 'user3', 'Saison 2025', '2025-01-12 18:00:00', 7),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-02-02 20:30:00', 8),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-02-02 20:30:00', 8),
                                                                                                  ('GOAL', 'But marque', 1, 'user3', 'Saison 2025', '2025-02-02 20:30:00', 8),
                                                                                                  ('CORNER', 'Corner', 1, 'user2', 'Saison 2025', '2025-02-02 20:30:00', 8),
                                                                                                  ('YELLOW_CARD', 'Carton jaune', 3, 'user4', 'Saison 2025', '2025-02-02 20:30:00', 8),
                                                                                                  ('GOAL', 'But marque', 4, 'user7', 'Saison 2025', '2025-02-18 17:00:00', 9),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-02-18 17:00:00', 9),
                                                                                                  ('RED_CARD', 'Carton rouge', 4, 'user15', 'Saison 2025', '2025-02-18 17:00:00', 9),
                                                                                                  ('GOAL', 'But marque', 5, 'user9', 'Saison 2025', '2025-03-03 19:00:00', 10),
                                                                                                  ('GOAL', 'But marque', 5, 'user10', 'Saison 2025', '2025-03-03 19:00:00', 10),
                                                                                                  ('YELLOW_CARD', 'Carton jaune', 1, 'user2', 'Saison 2025', '2025-03-03 19:00:00', 10),
                                                                                                  ('GOAL', 'But marque', 6, 'user11', 'Saison 2025', '2025-03-21 20:00:00', 11),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-03-21 20:00:00', 11),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-03-21 20:00:00', 11),
                                                                                                  ('GOAL', 'But marque', 1, 'user3', 'Saison 2025', '2025-03-21 20:00:00', 11),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-03-21 20:00:00', 11),
                                                                                                  ('YELLOW_CARD', 'Carton jaune', 6, 'user12', 'Saison 2025', '2025-03-21 20:00:00', 11),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-04-07 18:30:00', 12),
                                                                                                  ('GOAL', 'But marque', 1, 'user3', 'Saison 2025', '2025-04-07 18:30:00', 12),
                                                                                                  ('GOAL', 'But marque', 4, 'user7', 'Saison 2025', '2025-04-07 18:30:00', 12),
                                                                                                  ('GOAL', 'But marque', 4, 'user15', 'Saison 2025', '2025-04-07 18:30:00', 12),
                                                                                                  ('CORNER', 'Corner', 1, 'user1', 'Saison 2025', '2025-04-07 18:30:00', 12),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-04-19 21:00:00', 13),
                                                                                                  ('RED_CARD', 'Carton rouge', 2, 'user8', 'Saison 2025', '2025-04-19 21:00:00', 13),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 1, 'user3', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 6, 'user11', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 6, 'user12', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 6, 'user11', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('YELLOW_CARD', 'Carton jaune', 1, 'user2', 'Saison 2025', '2025-05-05 18:00:00', 14),
                                                                                                  ('GOAL', 'But marque', 3, 'user4', 'Saison 2025', '2025-05-24 19:45:00', 15),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-05-24 19:45:00', 15),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-05-24 19:45:00', 15),
                                                                                                  ('CORNER', 'Corner', 1, 'user3', 'Saison 2025', '2025-05-24 19:45:00', 15),
                                                                                                  ('GOAL', 'But marque', 1, 'user1', 'Saison 2025', '2025-06-02 16:00:00', 16),
                                                                                                  ('GOAL', 'But marque', 1, 'user2', 'Saison 2025', '2025-06-02 16:00:00', 16),
                                                                                                  ('GOAL', 'But marque', 1, 'user3', 'Saison 2025', '2025-06-02 16:00:00', 16),
                                                                                                  ('GOAL', 'But marque', 2, 'user8', 'Saison 2025', '2025-06-02 16:00:00', 16),
                                                                                                  ('YELLOW_CARD', 'Carton jaune', 2, 'user10', 'Saison 2025', '2025-06-02 16:00:00', 16);

-- 5.1 DEMANDES DE MATCH (Coach -> Admin)
INSERT INTO match_requests (requester_club_id, opponent_club_id, home_team_id, away_team_id, type_sport_id, requested_datetime, location, referee, requested_by, status) VALUES
                                                                                                                                                                            (1, 3, 1, 4, 1, '2025-10-01 18:30:00', 'Stade Sportify', 'M. Roussel', 'coach_zidane', 'PENDING'),
                                                                                                                                                                            (2, 1, 3, 1, 2, '2025-10-05 15:00:00', 'Rugby Park', 'Mme Klein', 'coach_marie', 'PENDING'),
                                                                                                                                                                            (3, 4, 4, 5, 3, '2025-10-12 19:00:00', 'Arena Basket', 'M. Silva', 'coach_tony', 'PENDING'),
                                                                                                                                                                            (6, 3, 6, 4, 6, '2025-10-20 20:30:00', 'Volley Dome', 'Mme Leroy', 'coach_paul', 'PENDING');

-- 6. COMPOSITION DE TEST
INSERT INTO match_composition (match_id, team_id, player_id, role, slot_index) VALUES
                                                                                   (1, 1, 'user1', 'Attaquant', 1),
                                                                                   (1, 1, 'user2', 'Milieu', 2);

-- 7. Licences
INSERT INTO licences (id, sport_id, type_licence, statut, membre_id) VALUES
                                                                         ('lic-001', 1, 'JOUEUR', 'ACTIVE', 'user1'),
                                                                         ('lic-002', 1, 'COACH', 'ACTIVE', 'coach_zidane'),
                                                                         ('lic-003', 2, 'JOUEUR', 'EN_ATTENTE', 'user4'),
                                                                         ('lic-004', 3, 'JOUEUR', 'EN_ATTENTE', 'user7'),
                                                                         ('lic-005', 4, 'JOUEUR', 'EN_ATTENTE', 'user9'),
                                                                         ('lic-006', 6, 'JOUEUR', 'EN_ATTENTE', 'user11'),
                                                                         ('lic-007', 2, 'COACH', 'EN_ATTENTE', 'coach_marie'),
                                                                         ('lic-008', 3, 'COACH', 'EN_ATTENTE', 'coach_tony');
