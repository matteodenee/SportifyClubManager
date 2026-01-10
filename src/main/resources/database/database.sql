---------------------------------------------------------
-- NETTOYAGE (Ordre respectant les contraintes)
---------------------------------------------------------
DROP TABLE IF EXISTS team_member CASCADE;
DROP TABLE IF EXISTS team CASCADE;
DROP TABLE IF EXISTS match_composition CASCADE;
DROP TABLE IF EXISTS match_requests CASCADE;
DROP TABLE IF EXISTS matchs CASCADE;
DROP TABLE IF EXISTS small_events CASCADE;
DROP TABLE IF EXISTS membership_requests CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS licences CASCADE;
DROP TABLE IF EXISTS sport_roles CASCADE;
DROP TABLE IF EXISTS sport_stats CASCADE;
DROP TABLE IF EXISTS clubs CASCADE;
DROP TABLE IF EXISTS type_sports CASCADE;
DROP TABLE IF EXISTS users CASCADE;

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
                       meetingschedule TEXT,
                       maxcapacity INT,
                       requirements TEXT,
                       status VARCHAR(20) DEFAULT 'Active',
                       manager_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL
);

---------------------------------------------------------
-- GESTION DES ÉQUIPES (TEAMS)
---------------------------------------------------------

-- 3.1 Table des équipes
CREATE TABLE team (
    id_team SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    categorie VARCHAR(50), -- Ex: Senior, U18, etc.
    id_club INT NOT NULL REFERENCES clubs(clubid) ON DELETE CASCADE,
    id_coach VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL, -- Un coach est un User
    id_type_sport INT REFERENCES type_sports(id) ON DELETE SET NULL
);

-- 3.2 Table de liaison Joueurs <-> Équipes (Many-to-Many)
CREATE TABLE team_member (
    id_team INT REFERENCES team(id_team) ON DELETE CASCADE,
    id_user VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (id_team, id_user)
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

-- 6. Table des Matchs (Nouveau)
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

-- 6.1 Demandes de match (Coach -> Admin)
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

-- 7. Table Composition des Matchs (Nouveau)
CREATE TABLE match_composition (
                                   match_id INT REFERENCES matchs(id) ON DELETE CASCADE,
                                   team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
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
                                     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Événements (Stats de match légères)
CREATE TABLE small_events (
                              id SERIAL PRIMARY KEY,
                              type VARCHAR(50),
                              description TEXT,
                              team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                              player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                              period VARCHAR(50),
                              event_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

-- 3. Clubs
INSERT INTO clubs (name, description, sport_id, meetingschedule, maxcapacity, requirements, manager_id) VALUES
                                                                                                            ('Olympique Sportify', 'Club spécialisé en Football', 1, 'Lundi 18h', 50, 'Certificat médical', 'dir_foot'),
                                                                                                            ('Rugby Club Erben', 'Club de rugby local', 2, 'Mercredi 14h', 30, 'Aucun', 'dir_rugby'),
                                                                                                            ('Basket City', 'Club de basket compétitif', 3, 'Mardi 19h', 25, 'Certificat médical', 'dir_basket'),
                                                                                                            ('Handball United', 'Club de handball régional', 4, 'Jeudi 18h', 28, 'Aucun', 'dir_hand'),
                                                                                                            ('Tennis Academy', 'Club de tennis loisirs', 5, 'Samedi 10h', 40, 'Licence obligatoire', 'dir_foot'),
                                                                                                            ('Volley Stars', 'Club de volley-ball mixte', 6, 'Vendredi 20h', 24, 'Aucun', 'dir_volley');

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

-- 5. MATCHS DE TEST (Pour ton nouveau MatchController)
INSERT INTO matchs (type_sport_id, home_team_id, away_team_id, datetime, location, referee, status) VALUES
                                                                                                        (1, 1, 2, '2024-06-15 20:45:00', 'Stade de France', 'M. Turpin', 'SCHEDULED'),
                                                                                                        (1, 1, 2, '2024-07-20 18:00:00', 'Parc des Princes', 'Mme Frappart', 'SCHEDULED'),
                                                                                                        (3, 3, 1, '2024-08-10 19:30:00', 'Arena Sportify', 'M. Lemoine', 'SCHEDULED'),
                                                                                                        (4, 4, 3, '2024-08-22 20:00:00', 'Gymnase Central', 'Mme Durant', 'SCHEDULED'),
                                                                                                        (6, 6, 1, '2024-09-05 21:00:00', 'Volley Dome', 'M. Perez', 'SCHEDULED'),
                                                                                                        (5, 5, 2, '2024-09-12 10:00:00', 'Courts Municipaux', 'Mme Gomez', 'SCHEDULED');

-- 5.1 DEMANDES DE MATCH (Coach -> Admin)
INSERT INTO match_requests (requester_club_id, opponent_club_id, home_team_id, away_team_id, type_sport_id, requested_datetime, location, referee, requested_by, status) VALUES
                                                                                                                                                                            (1, 3, 1, 3, 1, '2024-10-01 18:30:00', 'Stade Sportify', 'M. Roussel', 'coach_zidane', 'PENDING'),
                                                                                                                                                                            (2, 1, 2, 1, 2, '2024-10-05 15:00:00', 'Rugby Park', 'Mme Klein', 'coach_marie', 'PENDING'),
                                                                                                                                                                            (3, 4, 3, 4, 3, '2024-10-12 19:00:00', 'Arena Basket', 'M. Silva', 'coach_tony', 'PENDING'),
                                                                                                                                                                            (6, 3, 6, 3, 6, '2024-10-20 20:30:00', 'Volley Dome', 'Mme Leroy', 'coach_paul', 'PENDING');

-- 6. COMPOSITION DE TEST
INSERT INTO match_composition (match_id, team_id, player_id, role, slot_index) VALUES
                                                                                   (1, 1, 'user1', 'Attaquant', 1),
                                                                                   (1, 1, 'user2', 'Milieu', 2);

-- 7. Licences
INSERT INTO licences (id, sport_id, type_licence, statut, membre_id) VALUES
                                                                         ('lic-001', 1, 'JOUEUR', 'EN_ATTENTE', 'user1'),
                                                                         ('lic-002', 1, 'COACH', 'EN_ATTENTE', 'coach_zidane'),
                                                                         ('lic-003', 2, 'JOUEUR', 'EN_ATTENTE', 'user4'),
                                                                         ('lic-004', 3, 'JOUEUR', 'EN_ATTENTE', 'user7'),
                                                                         ('lic-005', 4, 'JOUEUR', 'EN_ATTENTE', 'user9'),
                                                                         ('lic-006', 6, 'JOUEUR', 'EN_ATTENTE', 'user11'),
                                                                         ('lic-007', 2, 'COACH', 'EN_ATTENTE', 'coach_marie'),
                                                                         ('lic-008', 3, 'COACH', 'EN_ATTENTE', 'coach_tony');
