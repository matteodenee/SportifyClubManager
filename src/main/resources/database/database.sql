-- Suppression des tables dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS membership_requests;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS clubs;
DROP TABLE IF EXISTS users;

-- Table des utilisateurs (MISE À JOUR)
CREATE TABLE users (
                       id VARCHAR(50) PRIMARY KEY,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100),       -- Ajouté pour User.java
                       email VARCHAR(100),      -- Ajouté pour User.java
                       role VARCHAR(20) NOT NULL -- Ajouté pour la gestion des accès (ADMIN, DIRECTOR, MEMBER)
);

-- Table des Clubs (MISE À JOUR selon le diagramme de classes)
CREATE TABLE clubs (
                       clubid SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       description TEXT,
                       type VARCHAR(50),
                       meetingschedule TEXT,
                       maxcapacity INT,
                       requirements TEXT,        -- Ajouté pour être 100% conforme au diagramme
                       status VARCHAR(20) DEFAULT 'Active'
);

-- Table des Membres
CREATE TABLE members (
                         userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                         clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                         role_in_club VARCHAR(50), -- Optionnel : pour distinguer qui est coach ou membre dans ce club
                         PRIMARY KEY (userid, clubid)
);
CREATE TABLE membership_requests (
                                     requestid SERIAL PRIMARY KEY,
                                     clubid INT REFERENCES clubs(clubid) ON DELETE CASCADE,
                                     userid VARCHAR(50) REFERENCES users(id) ON DELETE CASCADE,
                                     status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
                                     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertion des données de base (MISE À JOUR)
-- Note : Utilisez les rôles en majuscules pour correspondre à la logique canManageClubs
INSERT INTO users (id, password, name, email, role) VALUES
                                                        ('admin', 'admin123', 'Administrateur Système', 'admin@sportify.com', 'ADMIN'),
                                                        ('dir1', 'dir123', 'Jean Directeur', 'jean@sportify.com', 'DIRECTOR'),
                                                        ('user1', 'user123', 'Paul Membre', 'paul@gmail.com', 'MEMBER');

INSERT INTO clubs (name, description, type, meetingschedule, maxcapacity, requirements)
VALUES
    ('Club de Football', 'Passionnés de foot', 'Sport', 'Lundi 18h', 22, 'Certificat médical requis'),
    ('Club de Tennis', 'Tennis loisir', 'Sport', 'Mercredi 14h', 10, 'Raquette personnelle');