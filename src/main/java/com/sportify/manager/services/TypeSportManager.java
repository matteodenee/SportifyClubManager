package com.sportify.manager.services;

import com.sportify.manager.dao.TypeSportDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.sql.SQLException;
import java.util.List;

public class TypeSportManager {
    private static TypeSportManager instance;
    private final TypeSportDAO typeSportDAO;

    // Passage en privé pour le Singleton et utilisation de la Factory
    private TypeSportManager() {
        // Au lieu de "new PostgresTypeSportDAO", on demande à la factory
        // Cela respecte ton architecture "Abstract Factory"
        this.typeSportDAO = AbstractFactory.getFactory().createTypeSportDAO();
    }

    public static synchronized TypeSportManager getInstance() {
        if (instance == null) {
            instance = new TypeSportManager();
        }
        return instance;
    }

    // Méthode pour créer un type de sport
    public void createTypeSport(String nom, String description, int nbJoueurs, List<String> roles, List<String> statistiques) throws SQLException {
        TypeSport existant = typeSportDAO.getByNom(nom);
        if (existant != null) {
            throw new SQLException("Un type de sport avec le nom '" + nom + "' existe déjà.");
        }

        TypeSport newTypeSport = new TypeSport(nom, description, nbJoueurs, roles, statistiques);
        typeSportDAO.create(newTypeSport);
    }

    // Méthode pour récupérer tous les types de sport
    public List<TypeSport> getAllTypeSports() throws SQLException {
        return typeSportDAO.getAll();
    }

    // Méthode pour récupérer un type de sport par son ID
    public TypeSport getTypeSportById(int id) throws SQLException {
        return typeSportDAO.getById(id);
    }

    // Méthode pour modifier un type de sport
    public void updateTypeSport(TypeSport typeSport) throws SQLException {
        typeSportDAO.update(typeSport);
    }

    // Méthode pour supprimer un type de sport
    public boolean deleteTypeSport(int id) throws SQLException {
        if (typeSportDAO.isUsedByClubs(id)) {
            return false;
        }
        typeSportDAO.delete(id);
        return true;
    }

    // Méthode pour rechercher un type de sport par nom
    public TypeSport searchTypeSportByNom(String nom) throws SQLException {
        return typeSportDAO.getByNom(nom);
    }
}