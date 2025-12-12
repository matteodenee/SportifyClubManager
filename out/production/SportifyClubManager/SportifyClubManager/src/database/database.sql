

-- Table des utilisateurs 
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

-- Données de test 


-- Utilisateur admin
INSERT INTO users (id, password) 
VALUES ('admin', 'admin123')
ON CONFLICT (id) DO NOTHING;

-- Utilisateur manager
INSERT INTO users (id, password) 
VALUES ('manager1', 'manager123')
ON CONFLICT (id) DO NOTHING;

-- Utilisateur standard
INSERT INTO users (id, password) 
VALUES ('user1', 'user123')
ON CONFLICT (id) DO NOTHING;

-- Utilisateur invité
INSERT INTO users (id, password) 
VALUES ('guest', 'guest123')
ON CONFLICT (id) DO NOTHING;


-- Vérification des données

SELECT * FROM users;