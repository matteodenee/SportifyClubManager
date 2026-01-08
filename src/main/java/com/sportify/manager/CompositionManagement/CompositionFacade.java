package com.sportify.manager.CompositionManagement;

import compositionManagement.model.Composition;
import compositionManagement.services.CompositionManager;

public class CompositionFacade {

    private static CompositionFacade instance;
    private CompositionManager compositionManager;

    private CompositionFacade() {
        compositionManager = CompositionManager.getInstance();
    }

    public static CompositionFacade getInstance() {
        if (instance == null) {
            instance = new CompositionFacade();
        }
        return instance;
    }

    // UC : créer / modifier une composition
    public boolean saveComposition(Composition composition) {
        return compositionManager.saveComposition(composition);
    }
}
