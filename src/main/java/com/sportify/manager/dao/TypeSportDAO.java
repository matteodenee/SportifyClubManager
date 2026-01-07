package com.sportify.manager.dao;

import com.sportify.manager.services.TypeSport;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface abstraite définissant les opérations de persistance pour les types de sports.
 * Ce contrat permet d'isoler la logique métier de l'implémentation PostgreSQL.
 */
public abstract class TypeSportDAO {

    // Création d'un nouveau type de sport (ex: ajout d'un nouveau sport au catalogue)
    public abstract TypeSport create(TypeSport typeSport) throws SQLException;

    // Récupération de tous les types de sport pour remplir les menus déroulants
    public abstract List<TypeSport> getAll() throws SQLException;

    // Récupération d'un type de sport par son ID unique
    public abstract TypeSport getById(int id) throws SQLException;

    // Récupération d'un type de sport par son nom (ex: "Football")
    public abstract TypeSport getByNom(String nom) throws SQLException;

    // Mise à jour des informations d'un sport (description, nb joueurs, etc.)
    public abstract void update(TypeSport typeSport) throws SQLException;

    // Suppression d'un type de sport
    public abstract void delete(int id) throws SQLException;

    // Sécurité : vérifie si des clubs sont inscrits à ce sport avant de le supprimer
    public abstract boolean isUsedByClubs(int id) throws SQLException;
}