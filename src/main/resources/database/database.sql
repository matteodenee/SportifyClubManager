---------------------------------------------------------
-- NETTOYAGE
---------------------------------------------------------
DROP TABLE IF EXISTS small_events;
DROP TABLE IF EXISTS membership_requests;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS licences; -- Ajouté pour le nettoyage
DROP TABLE IF EXISTS clubs;
DROP TABLE IF EXISTS users;

---------------------------------------------------------
-- TABLES
---------------------------------------------------------

-- 1. Table Utilisateurs
CREATE TABLE users (
                       id VARCHAR(50) PRIMARY KEY,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100),
                       email VARCHAR(100),
                       role VARCHAR(20) NOT NULL
);

-- 2. Table Clubs
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

-- 3. Table Licences (NOUVEAUTÉ)
CREATE TABLE licences (
                          id VARCHAR(255) PRIMARY KEY,
                          sport VARCHAR(100) NOT NULL,
                          type_licence VARCHAR(50) NOT NULL,    -- JOUEUR, COACH
                          statut VARCHAR(50) NOT NULL,           -- EN_ATTENTE, ACTIVE, REFUSEE
                          date_demande DATE DEFAULT CURRENT_DATE,
                          date_debut DATE,
                          date_fin DATE,
                          membre_id VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                          date_decision DATE,
                          commentaire_admin TEXT
);

-- 4. Table Membres
CREATE TABLE members (
                         userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                         clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                         role_in_club VARCHAR(50),
                         PRIMARY KEY (userid, clubid)
);

-- 5. Demandes d'adhésion
CREATE TABLE membership_requests (
                                     requestid SERIAL PRIMARY KEY,
                                     clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                     userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                     status VARCHAR(20) DEFAULT 'PENDING',
                                     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Statistiques / Événements
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
                                                        ('dir_tennis', 'dir123', 'Sophie Tennis', 'sophie@sportify.com', 'DIRECTOR'),
                                                        ('coach_zidane', 'coach123', 'Zinedine Zidane', 'zizou@foot.com', 'COACH'),
                                                        ('user1', 'user123', 'Alice Membre', 'alice@gmail.com', 'MEMBER'),
                                                        ('user2', 'user123', 'Bob Sportif', 'bob@gmail.com', 'MEMBER');

-- 2. Clubs
INSERT INTO clubs (name, description, type, meetingschedule, maxcapacity, requirements, manager_id) VALUES
                                                                                                        ('Club de Football', 'Passionnés de foot', 'Sport', 'Lundi 18h', 22, 'Certificat médical', 'dir_foot'),
                                                                                                        ('Club de Tennis', 'Tennis loisir', 'Sport', 'Mercredi 14h', 10, 'Raquette personnelle', 'dir_tennis');

-- 3. DONNÉES DE TEST : LICENCES (Pour visualiser ton Dashboard)
-- Demandes en attente (visibles pour le Directeur)
INSERT INTO licences (id, sport, type_licence, statut, date_demande, membre_id, commentaire_admin) VALUES
                                                                                                       ('lic-001', 'Football', 'JOUEUR', 'EN_ATTENTE', '2026-01-05', 'user1', ''),
                                                                                                       ('lic-002', 'Tennis', 'JOUEUR', 'EN_ATTENTE', '2026-01-06', 'user2', ''),
                                                                                                       ('lic-003', 'Football', 'COACH', 'EN_ATTENTE', '2026-01-07', 'coach_zidane', '');

-- Licences déjà validées (historique)
INSERT INTO licences (id, sport, type_licence, statut, date_demande, date_debut, date_fin, membre_id, date_decision) VALUES
    ('lic-old-1', 'Football', 'JOUEUR', 'ACTIVE', '2025-01-01', '2025-01-02', '2026-01-02', 'user1', '2025-01-02');

-- 4. Demandes d'adhésion aux clubs
INSERT INTO membership_requests (clubid, userid, status) VALUES
                                                             (1, 'user1', 'PENDING'),
                                                             (2, 'user2', 'PENDING');