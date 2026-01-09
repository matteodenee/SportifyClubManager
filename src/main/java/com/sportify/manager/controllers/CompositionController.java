package com.sportify.manager.controllers;

import com.sportify.manager.facade.CompositionFacade;
import com.sportify.manager.services.Composition;

/**
 * Contrôleur pour la gestion des compositions d'équipe.
 * Fait le lien entre l'UI (Dashboard) et la Facade.
 */
public class CompositionController {

    private static CompositionController instance;
    private final CompositionFacade compositionFacade;

    private CompositionController() {
        // On pointe vers ta facade située dans com.sportify.manager.facade
        this.compositionFacade = CompositionFacade.getInstance();
    }

    public static CompositionController getInstance() {
        if (instance == null) {
            instance = new CompositionController();
        }
        return instance;
    }

    /**
     * UC 9.2.1 : Sauvegarder ou modifier une composition.
     * @param composition L'objet contenant le match, l'équipe et les joueurs.
     * @return true si la sauvegarde est réussie.
     */
    public boolean handleSaveComposition(Composition composition) {
        try {
            return compositionFacade.saveComposition(composition);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}