package com.sportify.manager.controllers;

import com.sportify.manager.facade.TypeSportFacade;
import com.sportify.manager.services.TypeSport;
import java.util.List;


public class TypeSportController {

    private final TypeSportFacade typeSportFacade;

    public TypeSportController() {

        this.typeSportFacade = TypeSportFacade.getInstance();
    }


    public boolean handleCreateTypeSport(String nom, String description, int nbJoueurs, List<String> roles, List<String> statistiques) {
        try {
            typeSportFacade.createTypeSport(nom, description, nbJoueurs, roles, statistiques);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<TypeSport> handleGetAllTypeSports() {
        try {
            return typeSportFacade.getAllTypeSports();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public TypeSport handleGetTypeSportById(int id) {
        try {
            return typeSportFacade.getTypeSportById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean handleUpdateTypeSport(TypeSport typeSport) {
        try {
            typeSportFacade.updateTypeSport(typeSport);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean handleDeleteTypeSport(int id) {
        try {
            return typeSportFacade.deleteTypeSport(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public TypeSport handleSearchTypeSportByNom(String nom) {
        try {
            return typeSportFacade.searchTypeSportByNom(nom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}