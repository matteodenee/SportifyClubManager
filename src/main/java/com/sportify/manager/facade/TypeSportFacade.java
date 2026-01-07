package com.sportify.manager.facade;

import com.sportify.manager.services.TypeSportManager;
import com.sportify.manager.services.TypeSport;
import java.sql.SQLException;
import java.util.List;

public class TypeSportFacade {
    private static TypeSportFacade instance;
    private final TypeSportManager typeSportManager;

    // Constructeur privé pour le Singleton
    private TypeSportFacade() {
        // On récupère l'instance du Manager (via son propre Singleton)
        this.typeSportManager = TypeSportManager.getInstance();
    }

    public static synchronized TypeSportFacade getInstance() {
        if (instance == null) {
            instance = new TypeSportFacade();
        }
        return instance;
    }

    // Créer un nouveau type de sport
    public void createTypeSport(String nom, String description, int nbJoueurs, List<String> roles, List<String> statistiques) throws SQLException {
        typeSportManager.createTypeSport(nom, description, nbJoueurs, roles, statistiques);
    }

    // Récupérer tous les types de sport (Très important pour ta ComboBox Licence)
    public List<TypeSport> getAllTypeSports() throws SQLException {
        return typeSportManager.getAllTypeSports();
    }

    // Récupérer un type de sport par son ID
    public TypeSport getTypeSportById(int id) throws SQLException {
        return typeSportManager.getTypeSportById(id);
    }

    // Modifier un type de sport
    public void updateTypeSport(TypeSport typeSport) throws SQLException {
        typeSportManager.updateTypeSport(typeSport);
    }

    // Supprimer un type de sport
    public boolean deleteTypeSport(int id) throws SQLException {
        return typeSportManager.deleteTypeSport(id);
    }

    // Rechercher un type de sport par nom
    public TypeSport searchTypeSportByNom(String nom) throws SQLException {
        return typeSportManager.searchTypeSportByNom(nom);
    }
}