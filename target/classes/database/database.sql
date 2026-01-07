---------------------------------------------------------
-- NETTOYAGE (Ordre respectant les contraintes)
---------------------------------------------------------
DROP TABLE IF EXISTS small_events;
DROP TABLE IF EXISTS membership_requests;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS licences;
DROP TABLE IF EXISTS sport_roles;
DROP TABLE IF EXISTS sport_stats;
DROP TABLE IF EXISTS clubs;
DROP TABLE IF EXISTS type_sports;
DROP TABLE IF EXISTS users;

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
-- GESTION DES RÔLES ET STATS
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

---------------------------------------------------------
-- GESTION DES MEMBRES ET LICENCES
---------------------------------------------------------

-- 6. Table Licences
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

-- 7. Table Membres
CREATE TABLE members (
                         userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                         clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                         role_in_club VARCHAR(50),
                         PRIMARY KEY (userid, clubid)
);

-- 8. Demandes d'adhésion
CREATE TABLE membership_requests (
                                     requestid SERIAL PRIMARY KEY,
                                     clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                     userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                     status VARCHAR(20) DEFAULT 'PENDING',
                                     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Événements / Stats de match
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
                                                        ('coach_zidane', 'coach123', 'Zinedine Zidane', 'zizou@foot.com', 'COACH'),
                                                        ('user1', 'user123', 'Alice Membre', 'alice@gmail.com', 'MEMBER'),
                                                        ('user2', 'user123', 'Bob Sportif', 'bob@gmail.com', 'MEMBER');

-- 2. Types de Sports
INSERT INTO type_sports (nom, description, nb_joueurs) VALUES
                                                           ('Football', 'Sport collectif 11 vs 11', 11),
                                                           ('Basketball', 'Sport de salle 5 vs 5', 5),
                                                           ('Tennis', 'Sport de raquette individuel', 2),
                                                           ('Handball', 'Sport collectif 7 vs 7', 7),
                                                           ('Rugby', 'Sport de contact 15 vs 15', 15);

-- 3. Rôles et Stats
INSERT INTO sport_roles (sport_id, role_name) VALUES (1, 'Gardien'), (1, 'Buteur'), (5, 'Pilier');
INSERT INTO sport_stats (sport_id, stat_name) VALUES (1, 'Buts'), (5, 'Essais');

-- 4. Clubs
INSERT INTO clubs (name, description, sport_id, meetingschedule, maxcapacity, requirements, manager_id) VALUES
                                                                                                            ('Olympique Sportify', 'Club spécialisé en Football', 1, 'Lundi 18h', 50, 'Certificat médical', 'dir_foot'),
                                                                                                            ('Rugby Club Erben', 'Club de rugby local', 5, 'Mercredi 14h', 30, 'Aucun', 'dir_foot');

-- 5. Licences initiales
INSERT INTO licences (id, sport_id, type_licence, statut, membre_id) VALUES
                                                                         ('lic-001', 1, 'JOUEUR', 'EN_ATTENTE', 'user1'),
                                                                         ('lic-002', 1, 'COACH', 'EN_ATTENTE', 'coach_zidane');

-- 6. Demandes d'adhésion
INSERT INTO membership_requests (clubid, userid, status) VALUES (1, 'user1', 'PENDING');

---------------------------------------------------------
-- AJOUT CRUCIAL : LIAISON DU COACH AU CLUB
---------------------------------------------------------
-- Sans cette ligne, PostgresUserDAO.getClubIdByCoach('coach_zidane') renverra toujours -1
INSERT INTO members (userid, clubid, role_in_club) VALUES
    ('coach_zidane', 1, 'COACH');

---------------------------------------------------------
-- DONNÉES DE TEST POUR LES STATISTIQUES (SMALL_EVENTS)
---------------------------------------------------------

INSERT INTO small_events (type, description, team_id, player_id, period, event_date) VALUES
-- Match 1
('MATCH', 'Match de championnat vs Lyon', 1, NULL, 'Saison 2024', '2024-01-10 14:00:00'),
('VICTOIRE', 'Résultat Match 1', 1, NULL, 'Saison 2024', '2024-01-10 16:00:00'),
('GOAL', 'But magnifique de Alice', 1, 'user1', 'Saison 2024', '2024-01-10 14:30:00'),
('GOAL', 'But de la tête de Bob', 1, 'user2', 'Saison 2024', '2024-01-10 15:15:00'),
-- Match 2
('MATCH', 'Match amical vs Paris', 1, NULL, 'Saison 2024', '2024-02-15 18:00:00'),
('CARD', 'Carton jaune pour Bob', 1, 'user2', 'Saison 2024', '2024-02-15 18:45:00'),
('GOAL', 'Egalisation de Alice', 1, 'user1', 'Saison 2024', '2024-02-15 19:20:00'),
-- Match 3
('MATCH', 'Quart de finale Coupe', 1, NULL, 'Saison 2024', '2024-03-20 20:45:00'),
('DEFAITE', 'Résultat Match 3', 1, NULL, 'Saison 2024', '2024-03-20 22:30:00'),
('CARD', 'Carton rouge Alice', 1, 'user1', 'Saison 2024', '2024-03-20 21:10:00'),
('FAUTE', 'Faute technique Bob', 1, 'user2', 'Saison 2024', '2024-03-20 21:40:00');