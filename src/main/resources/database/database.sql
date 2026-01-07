---------------------------------------------------------
-- NETTOYAGE
---------------------------------------------------------
DROP TABLE IF EXISTS small_events;
DROP TABLE IF EXISTS membership_requests;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS clubs;
DROP TABLE IF EXISTS users;

---------------------------------------------------------
-- TABLES (Inchangées, elles sont correctes)
---------------------------------------------------------
CREATE TABLE users (
                       id VARCHAR(50) PRIMARY KEY,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100),
                       email VARCHAR(100),
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE clubs (
                       clubid SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       description TEXT,
                       type VARCHAR(50),
                       meetingschedule TEXT,
                       maxcapacity INT,
                       requirements TEXT,
                       status VARCHAR(20) DEFAULT 'Active',
                       manager_id VARCHAR(50) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE members (
                         userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                         clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                         role_in_club VARCHAR(50),
                         PRIMARY KEY (userid, clubid)
);

CREATE TABLE membership_requests (
                                     requestid SERIAL PRIMARY KEY,
                                     clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                     userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                     status VARCHAR(20) DEFAULT 'PENDING',
                                     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dans la création de la table 5 (Statistiques / Événements)
CREATE TABLE small_events (
                              id SERIAL PRIMARY KEY,
                              type VARCHAR(50),
                              description TEXT,
    -- AJOUT DE "ON DELETE CASCADE" ICI
                              team_id INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                              player_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                              period VARCHAR(50),
                              event_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
---------------------------------------------------------
-- DONNÉES DE TEST (Enrichies pour valider le code Java)
---------------------------------------------------------

-- 1. Utilisateurs
INSERT INTO users (id, password, name, email, role) VALUES
                                                        ('admin', 'admin123', 'Super Admin', 'admin@sportify.com', 'ADMIN'),
                                                        ('dir_foot', 'dir123', 'Marc Football', 'marc@sportify.com', 'DIRECTOR'),
                                                        ('dir_tennis', 'dir123', 'Sophie Tennis', 'sophie@sportify.com', 'DIRECTOR'),
                                                        ('coach_zidane', 'coach123', 'Zinedine Zidane', 'zizou@foot.com', 'COACH'),
                                                        ('coach_nadal', 'coach123', 'Rafa Nadal', 'rafa@tennis.com', 'COACH'), -- Second coach pour tests
                                                        ('user1', 'user123', 'Alice Membre', 'alice@gmail.com', 'MEMBER'),
                                                        ('user2', 'user123', 'Bob Sportif', 'bob@gmail.com', 'MEMBER');

-- 2. Clubs
INSERT INTO clubs (name, description, type, meetingschedule, maxcapacity, requirements, manager_id) VALUES
                                                                                                        ('Club de Football', 'Passionnés de foot', 'Sport', 'Lundi 18h', 22, 'Certificat médical', 'dir_foot'), -- ID 1
                                                                                                        ('Club de Tennis', 'Tennis loisir', 'Sport', 'Mercredi 14h', 10, 'Raquette personnelle', 'dir_tennis'); -- ID 2

-- 3. Membres (Lien Coach <-> Club)
INSERT INTO members (userid, clubid, role_in_club) VALUES
                                                       ('coach_zidane', 1, 'COACH'),
                                                       ('coach_nadal', 2, 'COACH'), -- Nadal est coach du club 2
                                                       ('user1', 1, 'PLAYER'),
                                                       ('user2', 2, 'PLAYER');

-- 4. Événements (Pour tester le filtrage par équipe)
-- Événements pour le Club de FOOT (ID 1)
INSERT INTO small_events (type, description, team_id, player_id, period) VALUES
                                                                             ('GOAL', 'But sur corner', 1, 'user1', 'Saison 2024'),
                                                                             ('GOAL', 'Penalty', 1, 'user1', 'Saison 2024'),
                                                                             ('YELLOW_CARD', 'Tacle en retard', 1, 'user1', 'Saison 2024');

-- Événements pour le Club de TENNIS (ID 2)
INSERT INTO small_events (type, description, team_id, player_id, period) VALUES
                                                                             ('ACE', 'Service gagnant', 2, 'user2', 'Saison 2024'),
                                                                             ('DOUBLE_FAULT', 'Erreur service', 2, 'user2', 'Saison 2024');