package com.sportify.manager.CompositionManagement;


import compositionManagement.facade.CompositionFacade;
import compositionManagement.model.Composition;

public class CompositionController {

    private static CompositionController instance;
    private CompositionFacade compositionFacade;

    private CompositionController() {
        compositionFacade = CompositionFacade.getInstance();
    }

    public static CompositionController getInstance() {
        if (instance == null) {
            instance = new CompositionController();
        }
        return instance;
    }

    // UC : créer / modifier composition
    public boolean handleSaveComposition(Composition composition) {
        return compositionFacade.saveComposition(composition);
    }
}
