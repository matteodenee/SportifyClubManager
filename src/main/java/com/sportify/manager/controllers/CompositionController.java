package com.sportify.manager.controllers;

import com.sportify.manager.facade.CompositionFacade;
import com.sportify.manager.services.Composition;


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


    public boolean handleSaveComposition(Composition composition) {
        try {
            return compositionFacade.saveComposition(composition);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}