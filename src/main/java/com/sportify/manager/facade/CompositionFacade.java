package com.sportify.manager.facade;

import com.sportify.manager.services.Composition;
import com.sportify.manager.services.CompositionManager;

public class CompositionFacade {

    private static CompositionFacade instance;
    private final CompositionManager compositionManager;

    private CompositionFacade() {
        // On récupère l'instance du manager située dans tes services
        this.compositionManager = CompositionManager.getInstance();
    }

    public static CompositionFacade getInstance() {
        if (instance == null) {
            instance = new CompositionFacade();
        }
        return instance;
    }

    /**
     * UC : créer / modifier une composition.
     * Appelle la logique métier pour validation et enregistrement.
     */
    public boolean saveComposition(Composition composition) {
        return compositionManager.saveComposition(composition);
    }
}