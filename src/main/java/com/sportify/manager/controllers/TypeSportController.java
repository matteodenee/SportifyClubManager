package com.sportify.manager.controllers;

import com.sportify.manager.facade.TypeSportFacade;
import com.sportify.manager.services.TypeSport;
import java.util.List;

/**
 * Contrôleur pour le module TypeSport.
 * Fait le lien entre les vues (Frames) et la Facade.
 */
public class TypeSportController {

    private final TypeSportFacade typeSportFacade;

    public TypeSportController() {
        // On utilise l'instance Singleton de la Facade
        this.typeSportFacade = TypeSportFacade.getInstance();
    }

    // Gérer la création d'un type de sport
    public boolean handleCreateTypeSport(String nom, String description, int nbJoueurs, List<String> roles, List<String> statistiques) {
        try {
            typeSportFacade.createTypeSport(nom, description, nbJoueurs, roles, statistiques);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gérer la récupération de tous les types de sport
    public List<TypeSport> handleGetAllTypeSports() {
        try {
            return typeSportFacade.getAllTypeSports();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Gérer la récupération d'un type de sport par ID
    public TypeSport handleGetTypeSportById(int id) {
        try {
            return typeSportFacade.getTypeSportById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Gérer la modification d'un type de sport
    public boolean handleUpdateTypeSport(TypeSport typeSport) {
        try {
            typeSportFacade.updateTypeSport(typeSport);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gérer la suppression d'un type de sport
    public boolean handleDeleteTypeSport(int id) {
        try {
            return typeSportFacade.deleteTypeSport(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gérer la recherche d'un type de sport par nom
    public TypeSport handleSearchTypeSportByNom(String nom) {
        try {
            return typeSportFacade.searchTypeSportByNom(nom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}